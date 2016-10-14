package ru.radom.kabinet.services;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.exception.RecaptchaException;
import ru.askor.blagosfera.core.security.RecaptchaService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.core.util.cache.WebUtils;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityPermissionRepository;
import ru.askor.blagosfera.data.jpa.repositories.news.NewsSubscribeRepository;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalEvent;
import ru.askor.blagosfera.domain.events.file.ImagesEvent;
import ru.askor.blagosfera.domain.events.user.SharerEvent;
import ru.askor.blagosfera.domain.events.user.SharerEventType;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.user.SharerStatus;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.dao.*;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.dao.fields.FieldsGroupDao;
import ru.radom.kabinet.dao.settings.SharerSettingDao;
import ru.radom.kabinet.model.*;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;
import ru.radom.kabinet.model.news.NewsSubscribe;
import ru.radom.kabinet.model.registration.RegistrationRequest;
import ru.radom.kabinet.model.registration.RegistrationRequestStatus;
import ru.radom.kabinet.module.blagosfera.bp.util.BPMRabbitSignals;
import ru.radom.kabinet.security.RadomPasswordEncoder;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.security.context.SessionContext;
import ru.radom.kabinet.services.field.FieldValidatorBundle;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.registration.RegistrationRequestService;
import ru.radom.kabinet.services.registration.RegistratorService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.services.sharer.UserSettingsService;
import ru.radom.kabinet.util.MapsUtils;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.IpUtils;
import ru.radom.kabinet.utils.Roles;
import ru.radom.kabinet.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/*
 * 
 * Сервис по работе с профилями участников
 * 
 */

@Transactional
@Service("profileService")
public class ProfileService {

    public static final String EMAIL_SUFFIX_DELETED = "[deleted %s]";

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    // список допустимых расширений файлов картинок

    private static final List<String> EXTENSIONS = new ArrayList<String>();

    /**
     * Количество загружаемых пользователей для проверки за один запрос
     */
    private static final int COUNT_SHARERS_IN_PAGE = 100;

    /**
     * Ключ настройки - отображать ли сообщение о смене пароля
     */
    public static final String SHOW_DIALOG_TO_CHANGE_PASSWORD_SETTINGS_KEY = "needShowDialogToChangePassword";

    /**
     * Ключ настроки пользователя - TimeStamp первого входа пользователя в систему
     */
    public static final String FIRST_AUTH_SHARER_IN_SYSTEM_TIMESTAMP = "firstAuthSharerInSystemTimeStamp";

    static {
        EXTENSIONS.add("bmp");
        EXTENSIONS.add("jpeg");
        EXTENSIONS.add("jpg");
        EXTENSIONS.add("png");
        EXTENSIONS.add("gif");
        EXTENSIONS.add("pdf");
    }
    @Autowired
    private NewsSubscribeRepository newsSubscribeRepository;

    @Autowired
    private RegistratorService registratorService;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private SharersGroupDao sharersGroupDao;

    @Autowired
    private SharerSettingDao sharerSettingDao;

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private FieldsGroupDao fieldsGroupDao;

    @Autowired
    private FieldValidatorBundle fieldValidatorBundle;

    @Autowired
    private FieldValueDao fieldValueDao;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private FieldsService fieldsService;

    @Autowired
    private MaxMindGeoLite2CityBlocksIPv4Dao maxMindGeoLite2CityBlocksIPv4Dao;

    @Autowired
    private MaxMindGeoLite2CityLocationsEnDao maxMindGeoLite2CityLocationsEnDao;

    @Autowired
    private DisallowedWordDao disallowedWordDao;

    @Autowired
    private RegistrationRequestService registrationRequestService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private SessionContext radomSessionContext;

    @Autowired
    private RequestContext radomRequestContext;

    @Autowired
    private RadomPasswordEncoder passwordEncoder;

    @Autowired
    private CommunityPermissionRepository communityPermissionRepository;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecaptchaService recaptchaService;

    // метод регистрации нового участника, устанавливает начальные значения
    // полей, публикует событие
    public User register(User user) {
        // TODO Переделать
        /*user.setSalt(KeyGenerators.string().generateKey());
        user.setPassword(passwordEncoder.encodePassword(user.getPassword(), user.getSalt()));
        user.setStatus(SharerStatus.WAIT);
        user.setActivateCode(KeyGenerators.string().generateKey());
        Date now = new Date();
        user.setActivateCodeAt(now);
        user.setIkp("SHARER");
        user.setVerified(false);
        user.setGroup(sharersGroupDao.getById(12L));
        user.setAvatarSrc(Sharer.DEFAULT_AVATAR_URL);
        user.setProfileUnfilledAt(now);
        user.setDeleted(false);
        user.setArchived(false);
        user.setAllowMultipleSessions(true); // Параллельные сессии разрешены по умолчанию
        user.setRegisteredAt(now);

        user.getRoles().clear();
        user.getRoles().add(roleDao.getByName("USER"));

        sharerDao.save(user);

        String ikp = IkpUtils.longToIkpHash(MurmurHash.hash64(user.getId() + user.getIkp()));
        user.setIkp(ikp);

        sharerDao.update(user);

        publisher.publishEvent(new SharerEvent(this, SharerEventType.REGISTER, user));*/

        return user;
    }

    /*public Sharer register(Sharer sharer) {
        sharer.setSalt(KeyGenerators.string().generateKey());
        sharer.setPassword(passwordEncoder.encodePassword(sharer.getPassword(), sharer.getSalt()));
        sharer.setStatus(SharerStatus.WAIT);
        sharer.setActivateCode(KeyGenerators.string().generateKey());
        Date now = new Date();
        sharer.setActivateCodeAt(now);
        sharer.setIkp("SHARER");
        sharer.setVerified(false);
        sharer.setGroup(sharersGroupDao.getById(12L));
        sharer.setAvatarSrc(Sharer.DEFAULT_AVATAR_URL);
        sharer.setProfileUnfilledAt(now);
        sharer.setDeleted(false);
        sharer.setArchived(false);
        sharer.setAllowMultipleSessions(true); // Параллельные сессии разрешены по умолчанию
        sharer.setRegisteredAt(now);

        sharer.getRoles().clear();
        sharer.getRoles().add(roleDao.getByName("USER"));

        sharerDao.save(sharer);

        String ikp = IkpUtils.longToIkpHash(MurmurHash.hash64(sharer.getId() + sharer.getIkp()));
        sharer.setIkp(ikp);

        sharerDao.update(sharer);

        blagosferaEventPublisher.publishEvent(new SharerEvent(this, SharerEventType.REGISTER, sharer));

        return sharer;
    }*/

    // метод активации участника по коду активации, возвращает true если
    // активация происходит успешно, иначе false

    public boolean activate(String code, String addr, String defaultTimezone) {
        // TODO Переделать
        /*
        if (StringUtils.isEmpty(code)) {
            return false;
        }

        Sharer sharer = sharerDao.getByActivationCode(code);
        if (sharer == null) {
            return false;
        }

        if (!sharer.getActivateCode().equals(code)) {
            return false;
        }

        sharer.setStatus(SharerStatus.CONFIRM);
        sharer.setActivateCode(null);
        sharer.setActivateCodeAt(null);

        sharerDao.update(sharer);
        setSharerTimezone(sharer, addr, defaultTimezone);
        blagosferaEventPublisher.publishEvent(new SharerEvent(this, SharerEventType.ACTIVATE, sharer));
        */
        return true;
    }

    // проверка профилей участников по расписанию на предмет незаполненного или
    // не активированного профиля, если пора то профиль архивируется или
    // удаляется
    public void checkSharers() {
        // TODO Переделать
        // Загружаем пользователей частями с размером буфера COUNT_SHARERS_IN_PAGE
        /*
        int page = 0;
        int loadedCount = 0;
        do {
            List<Sharer> notDeletedSharers = sharerDao.getNotDeletedByPage(page, COUNT_SHARERS_IN_PAGE);
            for (Sharer sharer : notDeletedSharers) {
                updateProfileUnfilledAt(sharer);
            }
            loadedCount = notDeletedSharers.size();
            page++;
        } while (loadedCount == COUNT_SHARERS_IN_PAGE);

        int daysBeforeArchivation = settingsManager.getSystemSettingAsInt("profile.not-filled.archivation-days", 1);
        int daysBeforeDeletion = settingsManager.getSystemSettingAsInt("profile.not-filled.deletion-days", 3);
        int threshold = settingsManager.getSystemSettingAsInt("profile.filling-treshold", 70);

        // Количество часов по истечению которых после первохо входа пользователя в систему сделать оповещение о том,
        // что учетка будет удаелна если её не заполнить
        int notifyAfterHours = settingsManager.getSystemSettingAsInt("profile.notify-after-hours", 2);

        List<Integer> deleteionNotificationsDays = settingsManager.getSystemSettingAsIntegers("profile.not-filled.deletion-notifications-days");

        // Загружаем пользователей частями с размером буфера COUNT_SHARERS_IN_PAGE
        page = 0;
        loadedCount = 0;
        do {
            List<Sharer> notFilledSharers = sharerDao.getByNotFilledProfilesByPage(page, COUNT_SHARERS_IN_PAGE);
            Map<Sharer, Long> firstAuthSharerInSystemTimeStampMap = sharerSettingDao.getLongsBySharerList(notFilledSharers, FIRST_AUTH_SHARER_IN_SYSTEM_TIMESTAMP, null);

            for (Sharer sharer : notFilledSharers) {
                // Дата первого входа в систему пользователя
                Long firstAuthSharerInSystemTimeStamp = firstAuthSharerInSystemTimeStampMap.get(sharer);
                if (firstAuthSharerInSystemTimeStamp == null) {
                    continue;
                }
                // Если прошло notifyAfterHours и более после первого входа пользователя в систему - то начинаем его обрабатывать
                if ((new Date().getTime() - firstAuthSharerInSystemTimeStamp) < (notifyAfterHours * 60 * 60 * 1000)) {
                    continue;
                }


                ProfileFilling profileFilling = getProfileFilling(sharer);

                if (profileFilling.getPercent() < threshold) {

                    int notificationsSent = sharerSettingDao.getInteger(sharer, "profile.not-filled.notifications-sent", -1);

                    int findIndex = -1;
                    int findDays = -1;
                    for (int index = 0; index < deleteionNotificationsDays.size(); index++) {
                        int days = deleteionNotificationsDays.get(index);
                        int hours = days * 24;
                        // Ищем те профили, которые должны быть удалены через profileFilling.getHoursBeforeDeletion() часов
                        if (profileFilling.getHoursBeforeDeletion() <= hours) {
                            findIndex = index;
                            findDays = days;
                        }
                    }
                    // Если оповещение о том, что через findDays будет удалён профиль не отправлялось, то шлём
                    if (findIndex > -1 && notificationsSent != findIndex) {
                        notificationsSent = findIndex;
                        sharerSettingDao.set(sharer, "profile.not-filled.notifications-sent", Integer.toString(notificationsSent));
                        blagosferaEventPublisher.publishEvent(new DeletionNotificationSharerEvent(this, sharer, profileFilling));
                        logger.info(sharer.getEmail() + " : deletion notification for " + findDays + " days sent");
                    }

                    if (!sharer.isDeleted() && profileFilling.getHoursBeforeDeletion() != null && profileFilling.getHoursBeforeDeletion() <= 0) {
                        logger.info(sharer.getFullName() + " удален т.к. не заполнил профиль на " + threshold + "% за " + daysBeforeDeletion + " " + StringUtils.getDeclension(daysBeforeDeletion, "день", "дня", "дней"));
                        deleteSharer(sharer);
                    } else if (!sharer.isArchived() && profileFilling.getHoursBeforeArchivation() != null && profileFilling.getHoursBeforeArchivation() <= 0) {
                        logger.info(sharer.getFullName() + " перенесен в архив т.к. не заполнил профиль на " + threshold + "% за " + daysBeforeArchivation + " " + StringUtils.getDeclension(daysBeforeArchivation, "день", "дня", "дней"));
                        archiveSharer(sharer);
                    }

                }
            }

            loadedCount = notFilledSharers.size();
            page++;
        } while (loadedCount == COUNT_SHARERS_IN_PAGE);
        */
    }

    /**
     * обработчик событий приложения, слушает события, публикуемые другими
     * сервисами сейчас обрабатывается событие, публикуемое сервисом
     * ImagesService при загрузке аватара
     */
    @EventListener
    public void onImagesEvent(ImagesEvent event) {
        if ("sharer".equals(event.getObjectType())) {
            User user = userDataService.getByIdMinData(event.getObjectId());
            if (!isAllowSave(user.getEmail())) {
                throw new ProfileException("У Вас нет прав на редактирование данного профиля");
            }
            user.setAvatar(event.getUrl());
            user.setAvatarSrc(event.getUrlOriginal());

            userDataService.save(user);
            updateProfileUnfilledAt(user);
        }
    }

    // Выставляет пользователю часовой пояс по ip. Если не удалось определить часовой пояс по ip, то выставляет пользователю defaultTimezone.
    // Пользователь задаётся кодом активации.
    public void setSharerTimezone(User user, String addr, String defaultTimezone) {
        String timezone = "";
        if (!org.apache.commons.lang3.StringUtils.isBlank(defaultTimezone)) {
            timezone = defaultTimezone;
        }

        String sharerIp = addr;
        if (IpUtils.isValidIPv4(addr)) {
            MaxMindGeoLite2CityBlocksIPv4 block = maxMindGeoLite2CityBlocksIPv4Dao.getByIp(sharerIp);
            if (block != null && block.getGeonameId() != null) {
                MaxMindGeoLite2CityLocationsEn location = maxMindGeoLite2CityLocationsEnDao.getById(block.getGeonameId());
                if (location != null && location.getTimeZone() != null && !location.getTimeZone().equals("")) {
                    timezone = location.getTimeZone();
                }
            }
        }

        userSettingsService.set(user, "profile.timezone", timezone);
    }

    // метод проверяет насколько заполнен профиль участника и обновляет значение
    // поля profileUnfilledAt - дата когда профиль стал заполненным менее чем на
    // 70%

    public void updateProfileUnfilledAt(User user) {
        // TODO Переделать
        /*
        ProfileFilling profileFilling = getProfileFilling(user);
        boolean needUpdateSharer = false;
        if (profileFilling.getPercent() < settingsManager.getSystemSettingAsInt("profile.filling-treshold", 70)) {
            if (user.getProfileUnfilledAt() == null) {
                user.setProfileUnfilledAt(new Date());
                needUpdateSharer = true;
            }
        } else {
            userSettingsService.set(user, "profile.not-filled.notifications-sent", "0");
            if (user.getProfileUnfilledAt() != null || user.isArchived()) {
                user.setProfileUnfilledAt(null);
                user.setArchived(false);
                needUpdateSharer = true;
            }
        }
        if (needUpdateSharer) {
            userDataService.saveOrUpdate(user);
        }*/
    }

    /**
     * метод смены пароля
     *
     * @param user
     * @param oldPassword
     * @param newPassword
     */
    public void changePassword(User user, String oldPassword, String newPassword) {
        SharerStatus status = user.getStatus();
        UserEntity userEntity = userRepository.findOne(user.getId());
        if (!passwordEncoder.isPasswordValid(userEntity.getPassword(), oldPassword, userEntity.getSalt())) {

            String passwordName;

            if (SharerStatus.NEED_CHANGE_PASSWORD.equals(user.getStatus())) {
                passwordName = "проверочный код";
            } else {
                passwordName = "старый пароль";
            }

            throw new ProfileException("Введен неверный " + passwordName);
        }

        String salt = KeyGenerators.string().generateKey();
        userEntity.setSalt(salt);
        userEntity.setPassword(passwordEncoder.encodePassword(newPassword, salt));
        // Выставляем статус пользователя как подтверждённый при любой смене пароля чтобы не отображалось сообщение
        // О необходимости его поменять. Тут же обнуляем значение uri редиректа и обнуляем временный пароль в сессии.
        if ((userEntity.getStatus() != SharerStatus.CONFIRM) && (userEntity.getStatus() != SharerStatus.WAITING_FOR_CERTIFICATION)) {
            userEntity.setStatus(SharerStatus.CONFIRM);
        }

        userEntity.setChRootUrl(null);
        radomSessionContext.setGeneratedPassword(null);
        userEntity = userRepository.save(userEntity);
        user = userDataService.getByIdMinData(userEntity.getId());
        blagosferaEventPublisher.publishEvent(new SharerEvent(this, SharerEventType.CHANGE_PASSWORD, user));

        //Логически регистрация приглашенного пользователя заканчивается только на этом этапе
        if (SharerStatus.NEED_CHANGE_PASSWORD.equals(status)) {
            blagosferaEventPublisher.publishEvent(new SharerEvent(this, SharerEventType.REGISTER, user));
        }
    }

    /**
     * Отключить отображение диалога о смене пароля
     *
     * @param user
     */
    public void disableShowChangePasswordDialog(User user) {
        userSettingsService.set(user, SHOW_DIALOG_TO_CHANGE_PASSWORD_SETTINGS_KEY, "false");
    }

    // метод инициализации процесса смены емэйла

    public void initChangeEmail(User user) {
        user.setActivateCode(KeyGenerators.string().generateKey());
        userDataService.save(user);
        blagosferaEventPublisher.publishEvent(new SharerEvent(this, SharerEventType.CHANGE_EMAIL_INIT, user));
    }

    // метод завершения процесса смены емэйла

    public void completeChangeEmail(User user, String code, String newEmail) {
        if (StringUtils.isEmpty(user.getActivateCode()) || !code.equals(user.getActivateCode())) {
            throw new ProfileException("Введен неверный код");
        }
        if (!StringUtils.checkEmail(newEmail)) {
            throw new ProfileException("Введен некорректный e-mail");
        }
        if (userDataService.existsEmail(newEmail, user)) {
            throw new ProfileException("Введенный e-mail уже используется другим участником");
        }
        user.setEmail(newEmail);
        user.setActivateCode("");
        userDataService.save(user);
        SecurityUtils.getUser().setEmail(newEmail);
        blagosferaEventPublisher.publishEvent(new SharerEvent(this, SharerEventType.CHANGE_EMAIL_COMPLETE, user));

    }

    // метод инициализации процесса восстановления пароля

    public void restorePasswordInit(String email, String captchaResponse, HttpServletRequest request) throws RecaptchaException {
        recaptchaService.verify(WebUtils.getRemoteIp(request), captchaResponse);

        UserEntity userEntity = userRepository.findOneByEmail(email);

        if (userEntity == null) {
            return;
        }

        userEntity.setPasswordRecoveryCode(KeyGenerators.string().generateKey());
        userEntity = userRepository.save(userEntity);

        if (SharerStatus.NEED_CHANGE_PASSWORD.equals(userEntity.getStatus())) {
            //Если пользователь находится в стадии регистрации - высылаем письмо
            // с кодом восстановления проверочного кода
            blagosferaEventPublisher.publishEvent(new SharerEvent(this, SharerEventType.RECOVERY_VERIFICATION_CODE_INIT, userEntity.toDomain()));
        } else {
            //Иначе высылаем письмо с кодом восстановления пароля
            blagosferaEventPublisher.publishEvent(new SharerEvent(this, SharerEventType.RECOVERY_PASSWORD_INIT, userEntity.toDomain()));
        }
    }

    // метод завершения процесса восстановления пароля

    public UserEntity completeRecoveryPassword(String code, String password, String confirm) {
        UserEntity user = userRepository.findFirstByPasswordRecoveryCode(code);

        if (user == null) throw new PasswordRecoveryException("Неверный код восстановления пароля");
        if (StringUtils.isEmpty(password)) throw new PasswordRecoveryException("Пароль не задан");
        if (!password.equals(confirm)) throw new PasswordRecoveryException("Пароль и подтверждение не совпадают");

        String salt = StringUtils.randomString(10);
        user.setSalt(salt);
        user.setPassword(passwordEncoder.encodePassword(password, salt));
        user.setPasswordRecoveryCode(null);
        user = userRepository.save(user);

        blagosferaEventPublisher.publishEvent(new SharerEvent(this, SharerEventType.RECOVERY_PASSWORD_COMPLETE, user.toDomain()));
        return user;
    }

    //метод проверки, можно ли отображать емэйл участника другому участнику

    public boolean checkAllowShowEmail(User requester, User profile) {
        String mode = userSettingsService.get(profile, "profile.show-email.mode", "NOBODY");
        switch (mode) {
            case "ALL":
                return true;
            case "CONTACTS":
                ContactEntity contact = contactDao.getBySharers(profile.getId(), requester.getId());
                ContactEntity mirror = contactDao.getBySharers(requester.getId(), profile.getId());
                return (contact != null && contact.getSharerStatus() == ContactStatus.ACCEPTED && contact.getOtherStatus() == ContactStatus.ACCEPTED)
                        && (mirror != null && mirror.getSharerStatus() == ContactStatus.ACCEPTED && mirror.getOtherStatus() == ContactStatus.ACCEPTED);
           // case "LISTS":
             //   List<Long> ids = sharerSettingDao.getLongsList(profile, "profile.show-email.lists", Collections.EMPTY_LIST);
              //  contact = contactDao.getBySharers(profile, requester);
                //return contact != null && contact.getSharerStatus() == ContactStatus.ACCEPTED && contact.getOtherStatus() == ContactStatus.ACCEPTED && ((contact.getContactsGroup() == null && ids.contains(0L)) || (contact.getContactsGroup() != null && ids.contains(contact.getContactsGroup().getId())));
            case "NOBODY":
                return false;
            default:
                return false;
        }
    }

    /**
     * метод получения сведений о заполнении профиля, подробнее см в классе {@link ProfileFilling}
     */
    public ProfileFilling getProfileFilling(User user) {
        List<FieldsGroupEntity> fieldsGroups = fieldsGroupDao.getByInternalNamePrefix("PERSON_");
        List<FieldEntity> filledFields = new ArrayList<>();
        List<FieldEntity> notFilledFields = new ArrayList<>();
        boolean avatarLoaded = !user.getAvatar().equals(UserEntity.DEFAULT_AVATAR_URL);
        boolean allRequiredFilled = true;
        int filledPoints = 0;
        int totalPoints = 0;
        List<FieldEntity> fields = fieldDao.getByGroups(fieldsGroups);
        List<FieldValueEntity> fieldValues = fieldValueDao.getListByFieldsGroups(fieldsGroups, user.getId(), "SHARER");
        if (fieldValues != null) {
            for (FieldValueEntity value : fieldValues) {
                FieldEntity field = value.getField();

                if (field.getType() == FieldType.SYSTEM || field.getType() == FieldType.SYSTEM_IMAGE) {
                    continue;
                }

                if (StringUtils.isEmpty(value.getStringValue())) {
                    notFilledFields.add(field);

                    if (field.isRequired()) {
                        allRequiredFilled = false;
                    }
                } else {
                    filledFields.add(field);
                    filledPoints += field.getPoints();
                }

               // totalPoints += field.getPoints();
            }
        }
        for (FieldEntity field : fields) {
            if (field.getType() == FieldType.SYSTEM || field.getType() == FieldType.SYSTEM_IMAGE) {
                continue;
            }
            totalPoints += field.getPoints();
        }

        int treshold = settingsManager.getSystemSettingAsInt("profile.filling-treshold", 70);
        int archivationDays = settingsManager.getSystemSettingAsInt("profile.not-filled.archivation-days", 3);
        int deletionDays = settingsManager.getSystemSettingAsInt("profile.not-filled.deletion-days", 30);

        Date now = new Date();
        Date unfilledAt = user.getProfileUnfilledAt();

        Integer hoursBeforeArchivation = unfilledAt != null ? (DateUtils.getDistanceHours(now, DateUtils.add(unfilledAt, Calendar.DAY_OF_YEAR, archivationDays))) : null;

        Date deletionDate = unfilledAt != null ? DateUtils.add(unfilledAt, Calendar.DAY_OF_YEAR, deletionDays) : null;
        Integer hoursBeforeDeletion = unfilledAt != null ? (DateUtils.getDistanceHours(now, deletionDate)) : null;
        return new ProfileFilling((!allRequiredFilled || !avatarLoaded) ? 0 : filledPoints * 100 / totalPoints, filledPoints, totalPoints, avatarLoaded, allRequiredFilled, filledFields, notFilledFields, treshold, hoursBeforeArchivation, hoursBeforeDeletion, user.isArchived(), user.isDeleted(), deletionDate);
    }


    // удаление участника

    public void deleteSharer(User user) {
        user.setDeleted(true);
        user.setEmail(user.getEmail() + String.format(EMAIL_SUFFIX_DELETED, user.getIkp()));
        userDataService.save(user);
        newsSubscribeRepository.deleteNewsSubscribesToUser(user.getId());
        //blagosferaEventPublisher.publishEvent(new SharerEvent(this, SharerEventType.DELETED, user));

    }

    // перенос профиля в архив

    public UserEntity archiveSharer(User user) {
        // TODO Переделать
        /*
        sharer.setArchived(true);
        sharerDao.update(sharer);
        blagosferaEventPublisher.publishEvent(new SharerEvent(this, SharerEventType.ARCHIVED, sharer));
        return sharer;
        */
        return null;
    }

    // метод завершения сертификации участника
    public User setVerified(User user, User verifier) {
        user.setVerificationDate(new Date());
        user.setVerifier(verifier.getId());
        user.setVerified(true);
        userDataService.save(user);
        registrationRequestService.setVerifiedRequest(user);

        blagosferaEventPublisher.publishEvent(new BpmRaiseSignalEvent(this, BPMRabbitSignals.SHARER_VERIFIED,
                MapsUtils.map(Stream.of(MapsUtils.entry("receiver", user), MapsUtils.entry("sender", verifier)))));
        return user;
    }

    // метож сохранения в БД номера карыт участника

    public void setCardNumber(User user, String cardNumber) {
        user.setCardNumber(cardNumber);
        userDataService.save(user);
    }

    // метод проверки может ли один участник сертифицировать другого
    public boolean isAllowSetVerified(String userEmail, String verifierEmail) {
        UserDetailsImpl user = (UserDetailsImpl) userDetailsService.loadUserByUsername(userEmail);
        UserDetailsImpl verifier = (UserDetailsImpl) userDetailsService.loadUserByUsername(verifierEmail);
        return isAllowSetVerified(user, verifier);
    }

    // метод проверки может ли текущий участник сертифицировать другого
    public boolean isAllowSetVerified(String userEmail) {
        UserDetailsImpl user = (UserDetailsImpl) userDetailsService.loadUserByUsername(userEmail);
        UserDetailsImpl verifier = (UserDetailsImpl) userDetailsService.loadUserByUsername(SecurityUtils.getUser().getEmail());
        return isAllowSetVerified(user, verifier);
    }

    // метод проверки может ли один участник сертифицировать другого
    public boolean isAllowSetVerified(UserDetailsImpl user, UserDetailsImpl verifier) {
        boolean isUserRegistrator = user.hasAnyRole(Roles.REGISTRATOR_ROLES);
        boolean isVerifierActiveRegistrator = registratorService.isActiveRegistrator(verifier);

        if ((!isUserRegistrator && isVerifierActiveRegistrator && verifier.hasRole(Roles.ROLE_REGISTRATOR_SHARERS_EDITOR))
                ||
                (
                        isVerifierActiveRegistrator &&
                        (isUserRegistrator && verifier.hasRole(Roles.ROLE_REGISTRATOR_REGISTRATORS_EDITOR)) &&
                        (!user.getUser().getId().equals(verifier.getUser().getId()))
                )
        ) {
            return true;
        }

        return false;
    }

    /**
     * метод проверки может ли один пользователь сохранять изменения в профиле другого
     *
     * @param userEmail
     * @return
     */
    public boolean isAllowSave(String userEmail) {
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(userEmail);
        UserDetailsImpl currentUserDetails = SecurityUtils.getUserDetails();

        return isAllowSave(userDetails, currentUserDetails);
    }

    public boolean isAllowSave(UserDetailsImpl userDetails, UserDetailsImpl viewerUserDetails) {
        boolean isUserRegistrator = userDetails.hasAnyRole(Roles.REGISTRATOR_ROLES);
        boolean isViewerActiveRegistrator = registratorService.isActiveRegistrator(viewerUserDetails);

        if (viewerUserDetails.getUser().getId().equals(userDetails.getUser().getId())) return true;

        if (viewerUserDetails.hasRole(Roles.ROLE_SUPERADMIN)) {
            return !userDetails.hasRole(Roles.ROLE_SUPERADMIN);
        }

        RegistrationRequest registrationRequest = registrationRequestService.getByObject(userDetails.getUser());
        boolean isActiveRegistrationRequest = registrationRequest != null && RegistrationRequestStatus.NEW.equals(registrationRequest.getStatus());

        if (!isUserRegistrator && isActiveRegistrationRequest && isViewerActiveRegistrator && viewerUserDetails.hasRole(Roles.ROLE_REGISTRATOR_SHARERS_EDITOR))
            return true;

        if (isUserRegistrator && isViewerActiveRegistrator && viewerUserDetails.hasRole(Roles.ROLE_REGISTRATOR_REGISTRATORS_EDITOR))
            return true;

        return false;
    }

    /**
     * метод проверки может ли один пользователь записать карту другого
     *
     * @param userDetails
     * @param viewer
     * @return
     */
    public boolean isAllowWriteCard(UserDetailsImpl userDetails, UserDetailsImpl viewer) {
        //UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(userEmail);
        //UserDetailsImpl currentUserDetails = SecurityUtils.getUserDetails();

        //boolean isRegistrator = registratorService.isActiveRegistrator(viewer);
        boolean isUserRegistrator = userDetails.hasAnyRole(Roles.REGISTRATOR_ROLES);
        boolean isViewerActiveRegistrator = registratorService.isActiveRegistrator(viewer);

        if (!isUserRegistrator && isViewerActiveRegistrator && viewer.hasRole(Roles.ROLE_REGISTRATOR_SHARERS_EDITOR)) {
            return true;
        }

        if (isUserRegistrator && isViewerActiveRegistrator && viewer.hasRole(Roles.ROLE_REGISTRATOR_REGISTRATORS_EDITOR)) {
            return true;
        }

        if (viewer.hasRole(Roles.ROLE_SUPERADMIN)) {
            return true;
        }

        return false;
    }

    /**
     * метод проверки может ли один пользователь записывать отпечатки другого
     *
     * @param userDetails
     * @param viewer
     * @return
     */
    public boolean isAllowSaveFinger(UserDetailsImpl userDetails, UserDetailsImpl viewer) {
        return isAllowWriteCard(userDetails, viewer);
    }

    // метод получения документа - сертификационного соглашения для указанных участника и регистратора
    /*
    public Document getRegistrationAgreement(Sharer sharer, Sharer registrator) {
        if (!isAllowSetVerified(sharer, registrator)) {
            throw new ProfileException("У Вас нет прав сертифицировать этого участника");
        }
        Document agreement = documentsService.getRegistrationAgreement(sharer, registrator);
        return agreement;
    }*/

    public void uploadRegistrationAgreement(User user, List<FileItem> files) {
        try {
            String path = settingsManager.getSystemSetting("application.registration.agreements-directory");
            File folder = new File(path);

            for (FileItem file : files) {
                String filename = file.getName().substring(0, file.getName().indexOf(".")).toLowerCase();
                String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();

                if (!EXTENSIONS.contains(extension)) throw new ProfileException("Недопустимый тип файла: " + extension);

                FileOutputStream fileOutputStream = new FileOutputStream(new File(path + File.separator + user.getIkp() + "-" + (folder.listFiles().length + 1) + "-" + filename + "." + extension), false);
                IOUtils.write(file.get(), fileOutputStream);
                fileOutputStream.close();
            }
        } catch (Throwable e) {
            throw new ProfileException("Ошибка сохранения файла соглашения");
        }
    }

    // получить сертификационные файлы для шарера
    public List<String> getCertificationFiles(User user) {
        List<String> filesNames = new ArrayList<>();
        final File folder = new File(settingsManager.getSystemSetting("application.registration.agreements-directory") + File.separator);
        File[] files = folder.listFiles();
        if (files != null) {
            for (final File fileEntry : files) {
                if (!fileEntry.isDirectory() && fileEntry.getName().startsWith(user.getIkp())) {
                    filesNames.add(fileEntry.getName());
                }
            }
        }
        return filesNames;
    }

    // получить сертификационный файлы для шарера с именем name
    public File getCertificationFile(User user, String name) {
        final File folder = new File(settingsManager.getSystemSetting("application.registration.agreements-directory") + File.separator);
        File[] files = folder.listFiles();
        if (files != null) {
            for (final File fileEntry : files) {
                if (!fileEntry.isDirectory() && fileEntry.getName().startsWith(user.getIkp()) && fileEntry.getName().substring(0, fileEntry.getName().indexOf(".")).equals(name)) {
                    return fileEntry;
                }
            }
        }
        return null;
    }

	/*
     *  метод обновления координат и адресов в профилях участников
	 *  обновляются координаты и поля, содержащие адрес в одну строку
	 */

    /*public void updateGeoData() {
        List<Sharer> sharers = sharerDao.getNotDeleted();
        for (Sharer sharer : sharers) {
            logger.info("updating geo data for " + sharer.getFullName());
            fieldsService.updateGeoData(sharer);
        }
    }*/

    private void checkSharerShortLink(User user, String sharerShortLink) throws ProfileException {
        if (sharerShortLink.equals(user.getIkp())) {
            return;
        }

        if (org.apache.commons.lang3.StringUtils.isBlank(sharerShortLink)) {
            throw new ProfileException("Недопустимое имя ссылки");
        }

        if (sharerShortLink.length() < 4) {
            throw new ProfileException("Имя должно состоять минимум из четырёх символов");
        }

        Pattern p = Pattern.compile("^[0-9]+$");
        Matcher m = p.matcher(sharerShortLink);
        if (m.matches()) {
            throw new ProfileException("В имени ссылки запрещается использовать только цифры");
        }

        p = Pattern.compile("[a-zа-я0-9]+");
        m = p.matcher(sharerShortLink);
        if (!m.matches()) {
            throw new ProfileException("Допускаются только русские и латинские строчные символы и цифры без пробелов");
        }

        List<String> disallowedWords = disallowedWordDao.getStringsByType(DisallowedType.SHARER_SHORT_LINK_NAME);
        if (disallowedWords.contains(sharerShortLink)) {
            throw new ProfileException("Недопустимое имя ссылки");
        }

        User s = userDataService.getByIkpOrShortLink(sharerShortLink);
        if (s != null && !s.getId().equals(user.getId())) {
            throw new ProfileException("Такое имя уже есть");
        }
    }

    /**
     * Установить имя короткой ссылки для участника
     *
     * @param user            для кого будет установленна ссылка
     * @param sharerShortLink имя короткой ссылки, например qwerty
     * @return
     * @throws ProfileException в случае недопустимого имени
     */
    public String changeSharerShortLink(User user, String sharerShortLink) throws ProfileException {
        if (org.apache.commons.lang3.StringUtils.isBlank(sharerShortLink) || sharerShortLink.equals(user.getIkp())) {
            sharerShortLink = user.getIkp();
        }
        FieldEntity fieldEntity = fieldDao.getByInternalName("SHARER_SHORT_LINK_NAME");
        assert fieldEntity != null;
        Field field = fieldEntity.toDomain();
        checkSharerShortLink(user, sharerShortLink);
        field.setValue(sharerShortLink);
        List<Field> fields = new ArrayList<>();
        fields.add(field);
        fieldsService.saveFields(user, fields, fieldValidatorBundle);
        return sharerShortLink;
    }

}
