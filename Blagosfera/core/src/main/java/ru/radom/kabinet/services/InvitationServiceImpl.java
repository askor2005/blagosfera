package ru.radom.kabinet.services;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import padeg.lib.Padeg;
import ru.askor.blagosfera.core.services.invite.InvitationDataService;
import ru.askor.blagosfera.core.services.invite.InviteRelationShipTypeDataService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.user.InviteEvent;
import ru.askor.blagosfera.domain.events.user.InviteEventType;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.invite.Invitation;
import ru.askor.blagosfera.domain.user.SharerStatus;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.exception.InviteException;
import ru.radom.kabinet.model.ImageType;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.invite.InvitationEntity;
import ru.radom.kabinet.security.RadomPasswordEncoder;
import ru.radom.kabinet.services.image.ImagesService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.*;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.web.invite.dto.AcceptInvitationResult;
import ru.radom.kabinet.web.invite.dto.RejectInvitationResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис работы с приглашениями
 */
@Service("inviteService")
@Transactional
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationDataService invitationDataService;

    @Autowired
    private InviteRelationShipTypeDataService inviteRelationShipTypeDataService;

    @Autowired
    private RadomPasswordEncoder passwordEncoder;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private ImagesService imagesService;

    /**
     * Получить информацию о приглашениях пользователя, ожидаем объект с полями
     * <ul>
     *     <li><b>sharer</b>, в котором лежит id, ikp или объект у которого есть поле id</li>
     * </ul>
     */
    // ?TODO Переделать на BPMHandler
    /*@Transactional(readOnly = true)
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "core.social.get.sharer.invite.statistics", durable = "true"),
            exchange = @Exchange(value = "task-exchange", durable = "true"),
            key = "core.social.get.sharer.invite.statistics"
    ))
    private void getSharerInviteStatistics(Message message) {
        BPMBlagosferaUtils.commonRabbitTaskExecutorWithConverter(rabbitTemplate, message, (Map<String, Object> data) -> {
            Object sharerObj = data.get("sharer");
            User user = sharerService.tryGetUser(sharerObj);
            if(user == null) {
                return null;
            }
            List<InvitationEntity> invites = inviteDao.findAcceptedInvitesBySharer(user);
            int verified = 0;
            for (InvitationEntity invite : invites) {
                if(Boolean.TRUE.equals(invite.getInvitedSharer().getVerified())) {
                    verified++;
                }
            }
            Map<String, Object> answer = new HashMap<>();
            answer.put("verified", verified);
            answer.put("invited", invites.size());
            return answer;
        });
    }*/

    /**
     * Создать приглашение
     * @return InvitationEntity
     */
    @Override
    public Invitation createInvite(String email, String invitedLastName, String invitedFirstName, String invitedFatherName,
                                   String invitedGender, Boolean guarantee, Integer howLongFamiliar, List<Long> relationships,
                                   Long userId) {
        ExceptionUtils.check(userId == null || userId < 1, "Не передан ИД пользователя, который приглашает в систему");
        User user = userDataService.getByIdMinData(userId);
        ExceptionUtils.check(user == null, "Не найден пользователь, который приглашает в систему");
        ExceptionUtils.check(!user.isVerified(), "Для создания приглашений в систему необходимо пройти идентификацию");

        if (email == null || email.isEmpty()) {
            throw new InviteException("Укажите E-Mail нового участника.");
        } else if (invitedFirstName == null || invitedFirstName.isEmpty()) {
            throw new InviteException("Укажите имя нового участника.");
        } else if (invitedLastName == null || invitedLastName.isEmpty()) {
            throw new InviteException("Укажите фамилию нового участника.");
        } else if (invitedGender == null || invitedGender.isEmpty()) {
            throw new InviteException("Укажите пол нового участника.");
        } else if (relationships == null || relationships.isEmpty()) {
            //На этом этапе известно ФИО и пол участника, поэтому подготавливаем детальное сообщение
            boolean sex = org.apache.commons.lang3.StringUtils.equals(invitedGender, "М");
            String fioInPadegT;
            if (invitedFatherName == null || invitedFatherName.isEmpty()) {
                fioInPadegT = Padeg.getIFPadeg(invitedFirstName, invitedLastName,
                        sex, PadegConstants.PADEG_T);
            } else {
                fioInPadegT = Padeg.getFIOPadeg(invitedLastName, invitedFirstName,
                        invitedFatherName, sex, PadegConstants.PADEG_T);
            }
            throw new InviteException("Укажите ваше отношение с " + fioInPadegT);
        }

        Invitation invite = new Invitation();
        invite.setCreationDate(new Date(System.currentTimeMillis()));
        Integer expireDate = Integer.valueOf(settingsManager.getSystemSetting("invite.days-expire"));
        invite.setExpireDate(DateUtils.add(invite.getCreationDate(), Calendar.DAY_OF_YEAR, expireDate));
        invite.setUser(user);
        invite.setEmail(email);
        invite.setInvitedLastName(invitedLastName);
        invite.setInvitedFirstName(invitedFirstName);
        invite.setInvitedFatherName(invitedFatherName);
        invite.setInvitedGender(invitedGender);
        invite.setGuarantee(guarantee);
        invite.setHowLongFamiliar(howLongFamiliar);
        invite.setRelationships(relationships.stream().map(id -> inviteRelationShipTypeDataService.getById(id)).filter(inv -> inv != null).collect(Collectors.toList()));
        invite.setHashUrl(StringUtils.randomString(50));
        invite.setStatus(0);
        invite.setInvitesCount(0);
        invite = invitationDataService.save(invite);
        sendToEmail(invite, userId);
        return invite;
    }

    /**
     * Отправка приглашения на электронную почту
     */
    @Override
    public void sendToEmail(Invitation invite, Long userId) {
        Integer expireDate = Integer.valueOf(settingsManager.getSystemSetting("invite.days-expire"));
        invite.setExpireDate(DateUtils.add(new Date(System.currentTimeMillis()), Calendar.DAY_OF_YEAR, expireDate));
        invite.setLastDateSending(new Date(System.currentTimeMillis()));
        invite.setInvitesCount(invite.getInvitesCount() + 1);
        invitationDataService.save(invite);

        //Создаем фейкового юзера для участия в документообороте
        User receiver = new User();

        //Почта
        receiver.setEmail(invite.getEmail());

        //Фейковые ФИО
        FieldEntity firstNameFieldEntity = fieldDao.getByInternalName(FieldConstants.SHARER_FIRSTNAME);
        FieldValueEntity firstNameFieldValue = new FieldValueEntity();
        firstNameFieldValue.setField(firstNameFieldEntity);
        firstNameFieldValue.setStringValue(invite.getInvitedFirstName());

        FieldEntity secondNameFieldEntity = fieldDao.getByInternalName(FieldConstants.SHARER_SECONDNAME);
        FieldValueEntity secondNameFieldValue = new FieldValueEntity();
        secondNameFieldValue.setField(secondNameFieldEntity);
        secondNameFieldValue.setStringValue(invite.getInvitedFatherName());

        FieldEntity lastNameFieldEntity = fieldDao.getByInternalName(FieldConstants.SHARER_LASTNAME);
        FieldValueEntity lastNameFieldValue = new FieldValueEntity();
        lastNameFieldValue.setField(lastNameFieldEntity);
        lastNameFieldValue.setStringValue(invite.getInvitedLastName());

        Field firstNameField = new Field(
                firstNameFieldEntity.getId(), firstNameFieldValue.getStringValue(),  firstNameFieldEntity.getInternalName(),
                firstNameFieldEntity.getName(), firstNameFieldValue.isHidden(), firstNameFieldEntity.isHideable(),
                firstNameFieldEntity.getComment(), firstNameFieldEntity.getType(), firstNameFieldEntity.getExample(),
                firstNameFieldEntity.getPoints(), firstNameFieldEntity.isRequired(), firstNameFieldEntity.getPosition(),
                BooleanUtils.toBooleanDefaultIfNull(firstNameFieldEntity.getAttachedFile(), false), null,
                firstNameFieldEntity.getFieldsGroup().getInternalName(),
                firstNameFieldEntity.getMask(), firstNameFieldEntity.getPlaceholder());
        Field secondNameField = new Field(
                secondNameFieldEntity.getId(), secondNameFieldValue.getStringValue(),  secondNameFieldEntity.getInternalName(),
                secondNameFieldEntity.getName(), secondNameFieldValue.isHidden(), secondNameFieldEntity.isHideable(),
                secondNameFieldEntity.getComment(), secondNameFieldEntity.getType(), secondNameFieldEntity.getExample(),
                secondNameFieldEntity.getPoints(), secondNameFieldEntity.isRequired(), secondNameFieldEntity.getPosition(),
                BooleanUtils.toBooleanDefaultIfNull(secondNameFieldEntity.getAttachedFile(), false), null,
                secondNameFieldEntity.getFieldsGroup().getInternalName(),
                secondNameFieldEntity.getMask(), secondNameFieldEntity.getPlaceholder());
        Field lastNameField = new Field(
                lastNameFieldEntity.getId(), lastNameFieldValue.getStringValue(), lastNameFieldEntity.getInternalName(),
                lastNameFieldEntity.getName(), lastNameFieldValue.isHidden(), lastNameFieldEntity.isHideable(),
                lastNameFieldEntity.getComment(), lastNameFieldEntity.getType(), lastNameFieldEntity.getExample(),
                lastNameFieldEntity.getPoints(), lastNameFieldEntity.isRequired(), lastNameFieldEntity.getPosition(),
                BooleanUtils.toBooleanDefaultIfNull(lastNameFieldEntity.getAttachedFile(), false), null,
                lastNameFieldEntity.getFieldsGroup().getInternalName(),
                lastNameFieldEntity.getMask(), lastNameFieldEntity.getPlaceholder());

        receiver.getFields().addAll(Arrays.asList(firstNameField,secondNameField,lastNameField));

        User sender = userDataService.getByIdMinData(userId);

        //Публикуем событие
        blagosferaEventPublisher.publishEvent(
                new InviteEvent(this, InviteEventType.INVITE, sender, receiver, invite)
        );

    }

    /**
     * Принять приглашение
     */
    @Override
    public AcceptInvitationResult acceptInvitation(String hash, String password, String base64AvatarSrc, String base64Avatar, boolean needSendPassword) {
        AcceptInvitationResult result = AcceptInvitationResult.ACCEPT_SUCCESS;
        Invitation invitation = invitationDataService.getByHashUrl(hash);

        String avatarSrc = imagesService.uploadFromBase64(base64AvatarSrc, ImageType.PHOTO);
        String avatar = imagesService.uploadFromBase64(base64Avatar, ImageType.PHOTO);

        if (StringUtils.isEmpty(avatarSrc)) {
            result = AcceptInvitationResult.NEED_AVATAR_SOURCE;
        } else if (StringUtils.isEmpty(avatar)) {
            result = AcceptInvitationResult.NEED_AVATAR;
        } else if (StringUtils.isEmpty(password)) {
            result = AcceptInvitationResult.NEED_PASSWORD;
        } else if (password.length() < 8) {
            result = AcceptInvitationResult.PASSWORD_ERROR_LENGTH;
        } else if (invitation == null) {
            result = AcceptInvitationResult.NOT_FOUND;
        } else {
            Date currentDate = new Date(System.currentTimeMillis());
            if (currentDate.getTime() < invitation.getExpireDate().getTime()) {
                if (invitation.getStatus() == 0) {
                    //регистрация в системе
                    User invitee = registerInvitedUser(invitation, password, avatarSrc, avatar, needSendPassword);

                    invitation.setStatus(1);
                    invitation.setInvitee(invitee);
                    invitationDataService.save(invitation);
                } else if (invitation.getStatus() == 1) {
                    result = AcceptInvitationResult.ACCEPTED;
                } else {
                    result = AcceptInvitationResult.REJECTED;
                }
            } else {
                result = AcceptInvitationResult.EXPIRED;
            }
        }

        return result;
    }

    /**
     * Отклонить приглашение
     */
    @Override
    public RejectInvitationResult rejectInvitation(String hash) {
        RejectInvitationResult result = RejectInvitationResult.REJECT_SUCCESS;
        Invitation invite = invitationDataService.getByHashUrl(hash);
        Date now = new Date();
        if (invite == null) {
            result = RejectInvitationResult.NOT_FOUND;
        } else if (invite.getUser() == null) {
            result = RejectInvitationResult.NOT_INSTALL_INVITER;
        } else if (now.getTime() < invite.getExpireDate().getTime()) {
            if (invite.getStatus() == 0) {
                invite.setStatus(2);
                invitationDataService.save(invite);
            } else if (invite.getStatus() == 1) {
                result = RejectInvitationResult.ACCEPTED;
            } else if (invite.getStatus() == 2) {
                result = RejectInvitationResult.REJECTED;
            }
        } else {
            result = RejectInvitationResult.EXPIRED;
        }
        return result;
    }

    @Override
    public Map<String, String> fillInvitationMap(String hash) {
        Invitation invite = invitationDataService.getByHashUrl(hash);

        if (invite == null) {
            return null;
        }

        Map<String, String> map = new HashMap<>();

        if (invite.getInvitee() != null) {
            if (!SharerStatus.NEED_CHANGE_PASSWORD.equals(invite.getInvitee().getStatus())) {
                map.put("deprecated", "true");
                return map;
            } else if (!org.apache.commons.lang3.StringUtils.isBlank(invite.getInvitee().getEmail())) {
                map.put("inviteEmail", invite.getInvitee().getEmail());
            }
        }

        map.put("inviteIsAccept", "1");

        Date currentDate = new Date(System.currentTimeMillis());
        if (currentDate.getTime() < invite.getExpireDate().getTime()) {
            map.put("inviteIsExpire", "0");

            if (invite.getStatus() == 0) {
                map.put("inviteStatus", "0");
            } else {
                map.put("inviteStatus", invite.getStatus().toString());
            }
        } else {
            map.put("inviteIsExpire", "1");
        }

        return map;
    }

    private User registerInvitedUser(Invitation invite, String password, String avatarSrc, String avatar, boolean needSendPassword) {
        String salt = KeyGenerators.string().generateKey();

        User user = new User();
        user.setEmail(invite.getEmail());
        user.setSalt(salt);
        //user.setPassword(passwordEncoder.encodePassword(password, user.getSalt()));
        user.setStatus(SharerStatus.CONFIRM);//user.setStatus(SharerStatus.NEED_CHANGE_PASSWORD);
        //user.setChRootUrl(SharerChRootUrl.NEED_CHANGE_PASSWORD.getUrl());
        user.setActivateCode(KeyGenerators.string().generateKey());

        user.setIkp("SHARER");
        user.setVerified(false);
        user.setAvatarSrc(avatarSrc);
        user.setAvatar(avatar);
        user.setLastName(invite.getInvitedLastName());
        user.setFirstName(invite.getInvitedFirstName());
        user.setSecondName(invite.getInvitedFatherName());
        Map<FieldEntity, String> fieldMap = new HashMap<>();
        fieldMap.put(fieldDao.getByInternalName(FieldConstants.SHARER_LASTNAME), invite.getInvitedLastName());
        fieldMap.put(fieldDao.getByInternalName(FieldConstants.SHARER_FIRSTNAME), invite.getInvitedFirstName());
        fieldMap.put(fieldDao.getByInternalName(FieldConstants.SHARER_SECONDNAME), invite.getInvitedFatherName());
        if (invite.getInvitedGender().equals("М")) {
            fieldMap.put(fieldDao.getByInternalName(FieldConstants.SHARER_GENDER), "Мужской");
        } else {
            fieldMap.put(fieldDao.getByInternalName(FieldConstants.SHARER_GENDER), "Женский");
        }
        List<Field> fields = new ArrayList<>();
        for (FieldEntity fieldEntity : fieldMap.keySet()) {
            String value = fieldMap.get(fieldEntity);
            Field field = fieldEntity.toDomain();
            field.setValue(value);
            fields.add(field);
        }
        user.getFields().addAll(fields);

        user = userDataService.create(user, passwordEncoder.encodePassword(password, salt));

        if (needSendPassword) {
            //отправка на почту логина и пароля для принявшего приглашение пользователя
            sendToEmailLoginPasswordOfInvited(user, password, invite);
        }

        return user;
    }

    /**
     * Обновление пароля приглашенного пользователя и отправка новых данных на почту
     * (метод нужен при восстановлении пароля)
     * @param user пользователь
     * @param password пароль
     * @param invite приглашение
     */
    @Override
    public void changeAuthDataOfInvited(User user, String password, Invitation invite) {
        // TODO Переделать
        /*user.setSalt(KeyGenerators.string().generateKey());
        user.setPassword(passwordEncoder.encodePassword(password, user.getSalt()));
        user.setPasswordRecoveryCode(null);
        sharerDao.update(user);*/

        sendToEmailLoginPasswordOfInvited(user, password, invite);
    }

    @Override
    public Invitation getById(Long id) {
     return invitationDataService.getById(id);
    }

    @Override
    public boolean existsInvites(String email) {
        return invitationDataService.existsInvites(email);
    }

    @Override
    public Invitation getByHashUrl(String hash) {
        return invitationDataService.getByHashUrl(hash);
    }

    @Override
    public Invitation findAcceptedInvitationByUserId(Long userId) {
        InvitationEntity invitation = invitationDataService.findAcceptedInvitationByUserId(userId);
        return invitation == null ? null : invitation.toDomain();
    }

    /**
     * Отправка логина и пароля на электронную почту
     */
    private void sendToEmailLoginPasswordOfInvited(User user, String password, Invitation invite) {
        /*Map<String, Object> variables = new HashMap<>();
        variables.put("login", sharer.getEmail());
        variables.put("password", password);
        variables.put("inviteLogin", "invite/" + invite.getHashUrl() + "/login?password=" + password);
        emailService.sendTo(Arrays.asList(sharer), emailTemplateDao.findByTitle("invite.email.on-invite-accept"), variables);*/

        //Публикуем событие
        blagosferaEventPublisher.publishEvent(
                new InviteEvent(this, InviteEventType.ACCEPT_INVITE, user, invite, password)
        );
    }
}