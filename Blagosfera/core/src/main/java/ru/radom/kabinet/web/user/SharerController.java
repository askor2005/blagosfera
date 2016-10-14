package ru.radom.kabinet.web.user;

import com.google.gson.Gson;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.hibernate.criterion.Order;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import padeg.lib.Padeg;
import ru.askor.blagosfera.core.services.contacts.ContactsService;
import ru.askor.blagosfera.core.services.invite.InvitationDataService;
import ru.askor.blagosfera.core.services.security.AuthService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.domain.certification.UserCertificationException;
import ru.askor.blagosfera.domain.certification.UserCertificationSession;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.OkvedDomain;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.invite.Invitation;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.SharerService;
import ru.radom.kabinet.collections.SharersList;
import ru.radom.kabinet.dao.*;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.fields.FieldFileDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.dao.fields.FieldsGroupDao;
import ru.radom.kabinet.dao.news.NewsDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.dao.registration.RegistratorDao;
import ru.radom.kabinet.dao.settings.SharerSettingDao;
import ru.radom.kabinet.dto.*;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.*;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldFileEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.model.registration.RegistrationRequest;
import ru.radom.kabinet.model.registration.RegistratorLevel;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.bio.TokenIkpProtected;
import ru.radom.kabinet.security.bio.TokenProtected;
import ru.radom.kabinet.services.*;
import ru.radom.kabinet.services.field.FieldValidatorBundle;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.registration.RegistrationRequestService;
import ru.radom.kabinet.services.registration.RegistratorService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.services.sharer.UserSettingsService;
import ru.radom.kabinet.utils.*;
import ru.radom.kabinet.web.communities.dto.OkvedDto;
import ru.radom.kabinet.web.invite.dto.InviteCountDto;
import ru.radom.kabinet.web.user.dto.*;
import ru.radom.kabinet.web.utils.Breadcrumb;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class SharerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SharerController.class);
    @Autowired
    private ContactsService contactsService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ContactsGroupDao contactsGroupDao;

    @Autowired
    private FieldsService fieldsService;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private FieldValueDao fieldValueDao;

    @Autowired
    private FieldsGroupDao fieldsGroupDao;

    @Autowired
    private SerializationManager serializationManager;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private OkvedDao okvedDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private NewsDao newsDao;

    @Autowired
    private FieldFileDao fieldFileDao;

    @Autowired
    private RameraTextDao rameraTextDao;

    @Autowired
    private UserCertificationManager userCertificationManager;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private RegistratorDao registratorDao;

    @Autowired
    private RegistrationRequestService registrationRequestService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    @Autowired
    private SharerSettingDao sharerSettingDao;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private InvitationDataService invitationDataService;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private FieldValidatorBundle fieldValidatorBundle;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private SharerService sharerService;

    @Autowired
    private RegistratorService registratorService;

    @RequestMapping("/sharer")
    public View redirectToSharerPage() {
        // редирект сделан таким образом чтобы затереть все лишние параметры при редиректе
        RedirectView redirect = new RedirectView(SecurityUtils.getUser().getLink());
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    @RequestMapping(value = "/instruction", method = RequestMethod.GET)
    public String showInstructionPage(Model model) {
        // TODO Проверить
        /*model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Инструкция по работе с системой", "/instruction"));

        //Получаем текст инструкции
        RameraTextEntity instructionText = rameraTextDao.getByCode("INSTRUCTION");

        if (instructionText != null) {
            model.addAttribute("instructionText", instructionText.getText());
        } else {
            throw new RuntimeException("Отсутствует текст с кодом INSTRUCTION необходимый для формирования страницы!");
        }

        //Достаем из сессии проверочный код, сгенерированный системой.
        String generatedPassword = radomSessionContext.getGeneratedPassword();

        //Добавляем в модель, если он существует
        if (generatedPassword != null) {
            model.addAttribute("generatedPassword", generatedPassword);
        }*/

        return "instruction";
    }

    @RequestMapping(value = "/instruction", method = RequestMethod.POST)
    public String postInstructionPage(Model model, @RequestParam(value = "instruction_showed", defaultValue = "false") boolean instructionShowed) {
        settingsManager.setUserSetting("profile.instruction-showed", Boolean.toString(instructionShowed), SecurityUtils.getUser());
        return "redirect:/sharer#profile-accordion-anchor";
    }

    @RequestMapping("/feed")
    public String showFeedPage(Model model, UserEntity userEntity) {
        /*List<News> list = newsDao.getBySharer(userEntity, null, 20);
        String json = serializationManager.serializeCollection(list).toString();
        model.addAttribute("feedFirstPage", json);*/
        model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Лента новостей", "/feed"));
        return "ramera";
    }

    private FieldsGroupEntity getFieldGroupByInternalName(List<FieldsGroupEntity> fieldsGroups, String internalName) {
        FieldsGroupEntity result = null;
        for (FieldsGroupEntity fieldsGroup : fieldsGroups) {
            if (fieldsGroup.getInternalName().equals(internalName)) {
                result = fieldsGroup;
                break;
            }
        }
        return result;
    }

    @RequestMapping("/sharer/{ikp}")
    public String showSharerPage(@PathVariable("ikp") String ikp, Model model) {
        User user = userDataService.getByIkpOrShortLink(ikp);
        if (user == null) {
            model.addAttribute("profileExists", false);
            return "sharer";
        }
        model.addAttribute("profileExists", true);
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getEmail());
        UserDetailsImpl viewer = (UserDetailsImpl) userDetailsService.loadUserByUsername(SecurityUtils.getUser().getEmail());

        if (viewer != null && viewer.getUser().getId().equals(user.getId())) {
            model.addAttribute("isSelfProfile", true);
        }

        model.addAttribute("currentPageTitle", "Профиль участника " + user.getShortName());
        model.addAttribute("profile", user);
        if (user.getVerifier() != null) {
            User verifier = userDataService.getByIdMinData(user.getVerifier());

            String lastName = verifier.getLastName();
            String firstName = verifier.getFirstName();
            String secondName = verifier.getSecondName();
            model.addAttribute("profileVerifierFullName", Padeg.getFIOPadeg(lastName, firstName, secondName, true, 5));
            model.addAttribute("profileVerifierLink", verifier.getLink());
            String level = registratorDao.getRegistratorLevel(verifier.getId());
            if (level != null && RegistratorLevel.getByMnemo(level) != null) {
                level = Padeg.getFIOPadeg(level.substring(0, level.indexOf(" ")), "", "", true, 5).toLowerCase() + level.substring(level.indexOf(" "), level.length());
                model.addAttribute("profileVerifierLevel", level);
            }
        }

        Invitation invitation = invitationService.findAcceptedInvitationByUserId(user.getId());
        model.addAttribute("registratorLevel", registratorDao.getRegistratorLevel(user.getId()));
        model.addAttribute("activeRegistrator", registratorService.isActiveRegistrator(userDetails));
        if (invitation != null) {
            String lastName = invitation.getUser().getLastName();
            String firstName = invitation.getUser().getFirstName();
            String secondName = invitation.getUser().getSecondName();
            model.addAttribute("inviterFullName", Padeg.getFIOPadeg(lastName, firstName, secondName, invitation.getUser().isSex(), 5));
            model.addAttribute("inviterFirstLastName", Padeg.getFIOPadeg(lastName, firstName, null, invitation.getUser().isSex(), 5));
            model.addAttribute("inviter", invitation.getUser());
        }

        model.addAttribute("profileRoles", userDetails.getAuthorities());

        RegistrationRequest registrationRequest = registrationRequestService.getMy();
        model.addAttribute("registrationRequest", registrationRequest);
        if (registrationRequest != null && registrationRequest.getRegistrator() != null) {
            model.addAttribute("registrationRequest_padeg3", Padeg.getFIOPadegFS(registrationRequest.getRegistrator().getFullName(), registrationRequest.getRegistrator().getSex(), 3));
        }
        model.addAttribute("profileFilling", profileService.getProfileFilling(user));
        model.addAttribute("showEmail", viewer.getUser().getId().equals(user.getId()) || profileService.checkAllowShowEmail(viewer.getUser(), user));
        model.addAttribute("contactsGroups", contactsGroupDao.getBySharer(viewer.getUser().getId()));
        model.addAttribute("contact", contactDao.getBySharers(viewer.getUser().getId(), user.getId()));
        model.addAttribute("groups", contactsGroupDao.getBySharer(viewer.getUser().getId()));
        model.addAttribute("contactsCount", contactsService.getCount(user.getId()));

        List<FieldsGroupEntity> fieldsGroups = fieldsGroupDao.getByInternalNamePrefix("PERSON_");
        if (userDetails.hasAnyRole(Roles.REGISTRATOR_ROLES)) {
            fieldsGroups.addAll(fieldsGroupDao.getByInternalNamePrefix("REGISTRATOR_"));
        }
        model.addAttribute("fieldsGroups", fieldsGroups);
        model.addAttribute("fieldsStates", fieldsService.getFieldsStatesMap(fieldsGroups, userDetails, viewer));
        List<FieldValueEntity> fieldValues = fieldValueDao.getListByFieldsGroups(fieldsGroups, user.getId(), "SHARER");
        Map<FieldEntity, FieldValueEntity> fieldValueMap = new HashMap<>();

        //System.out.println("--- FIELDS ---");

        for (FieldValueEntity fieldValue : fieldValues) {
            fieldValueMap.put(fieldValue.getField(), fieldValue);

            //    System.out.println(fieldValue.getField().getInternalName() + " : " + fieldValue.getStringValue());
        }

        //System.out.println("--- END FIELDS ---");

        model.addAttribute("fieldValueMap", fieldValueMap);

        Map<FieldEntity, Timetable> timetables = new HashMap<>();
        for (FieldsGroupEntity fieldsGroup : fieldsGroups) {
            for (FieldEntity field : fieldsGroup.getFields()) {
                if (field.getType() == FieldType.TIMETABLE) {
                    String timeTableJson = FieldsService.getFieldStringValue(fieldValueMap.get(field));
                    timetables.put(field, new Timetable(timeTableJson));
                }
            }
        }
        model.addAttribute("timetables", timetables);

        // для того, чтобы правильно разбивать группы полей на вкладки
        //--------------------------------------------------------------------------------------------------------------
        // Поля вкладки Регистрационные данные
        //--------------------------------------------------------------------------------------------------------------
        List<FieldsGroupEntity> registrationFieldsGroups = new ArrayList<>();


        registrationFieldsGroups.add(getFieldGroupByInternalName(fieldsGroups, "PERSON_COMMON"));
        registrationFieldsGroups.add(getFieldGroupByInternalName(fieldsGroups, "PERSON_COMMUNICATIONS"));
        model.addAttribute("registrationFieldsGroups", registrationFieldsGroups);
        model.addAttribute("registrationFieldsStates", fieldsService.getFieldsStatesMap(registrationFieldsGroups, userDetails, viewer));
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------------------------------------------------------------------------------------
        // Поля вкладки Сертификационные данные
        //--------------------------------------------------------------------------------------------------------------
        List<FieldsGroupEntity> verificationFieldsGroups = new ArrayList<>();
        verificationFieldsGroups.add(getFieldGroupByInternalName(fieldsGroups, "PERSON_PASSPORT"));
        verificationFieldsGroups.add(getFieldGroupByInternalName(fieldsGroups, "PERSON_REGISTRATION_ADDRESS"));
        model.addAttribute("verificationFieldsGroups", verificationFieldsGroups);
        model.addAttribute("verificationFieldsStates", fieldsService.getFieldsStatesMap(verificationFieldsGroups, userDetails, viewer));
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------------------------------------------------------------------------------------
        // Поля вкладки Реквизиты Регистратора
        //--------------------------------------------------------------------------------------------------------------
        if (userDetails.hasAnyRole(Roles.REGISTRATOR_ROLES)) {
            List<FieldsGroupEntity> registratorFieldsGroups = new ArrayList<>();
            registratorFieldsGroups.add(getFieldGroupByInternalName(fieldsGroups, "REGISTRATOR_OFFICE_ADDRESS"));
            registratorFieldsGroups.add(getFieldGroupByInternalName(fieldsGroups, "REGISTRATOR_CONTACTS"));
            registratorFieldsGroups.add(getFieldGroupByInternalName(fieldsGroups, "REGISTRATOR_ADDITIONAL_SERVICES"));
            model.addAttribute("registratorFieldsGroups", registratorFieldsGroups);
            model.addAttribute("registratorFieldsStates", fieldsService.getFieldsStatesMap(registratorFieldsGroups, userDetails, viewer));
        }
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------------------------------------------------------------------------------------
        // Поля вкладки "Доп. данные"
        //--------------------------------------------------------------------------------------------------------------
        List<FieldsGroupEntity> additionalFieldsGroups = new ArrayList<>();
        additionalFieldsGroups.add(getFieldGroupByInternalName(fieldsGroups, "PERSON_ACTUAL_ADDRESS"));
        model.addAttribute("additionalFieldsGroups", additionalFieldsGroups);
        model.addAttribute("additionalFieldsStates", fieldsService.getFieldsStatesMap(additionalFieldsGroups, userDetails, viewer));
        //--------------------------------------------------------------------------------------------------------------

        model.addAttribute("registratorLevelMnemo", registratorDao.getRegistratorLevelMnemo(user.getId()));

        model.addAttribute("needVerifiedSharersToRegistratorLevel3", settingsManager.getSystemSettingAsInt("registrator.level3.needVerifiedSharers", 100));
        model.addAttribute("needVerifiedCommunitiesToRegistratorLevel2", settingsManager.getSystemSettingAsInt("registrator.level2.needVerifiedCommunities", 20));

        InviteCountDto inviteCountDto = invitationDataService.getInviteCountData(user.getId());
        Long registeredInvitedSharersCount = Long.valueOf(inviteCountDto.getCountRegisterd());
        Long verifiedInvitedSharersCount = Long.valueOf(inviteCountDto.getCountVerified());
        Long registratorsLevel3InvitedSharersCount = inviteCountDto.getRegistratorsLevel3InvitedSharersCount();
        Long registratorsLevel2InvitedSharersCount = inviteCountDto.getRegistratorsLevel2InvitedSharersCount();
        Long registratorsLevel1InvitedSharersCount = inviteCountDto.getRegistratorsLevel1InvitedSharersCount();
        Long createdAndVerifiedCommunitiesInvitedSharersCount = 0L;

        model.addAttribute("registeredInvitedSharersCount", registeredInvitedSharersCount);
        model.addAttribute("verifiedInvitedSharersCount", verifiedInvitedSharersCount);
        model.addAttribute("registratorsLevel3InvitedSharersCount", registratorsLevel3InvitedSharersCount);
        model.addAttribute("registratorsLevel2InvitedSharersCount", registratorsLevel2InvitedSharersCount);
        model.addAttribute("registratorsLevel1InvitedSharersCount", registratorsLevel1InvitedSharersCount);
        model.addAttribute("createdAndVerifiedCommunitiesInvitedSharersCount", createdAndVerifiedCommunitiesInvitedSharersCount);

        model.addAttribute("verifiedByMeSharersCount", sharerDao.getVerifiedSharersCount(user.getId()));
        model.addAttribute("verifiedByMeCommunitiesCount", communityDao.getVerifiedCommunitiesCount(user.getId()));
        model.addAttribute("verifiedByMeRegistratorsCount", registratorDao.getVerifiedRegistratorsCount(user.getId()));

        // Если профиль просматривает регистратор либо администратор - то нужно показывать ему сертификационные файлы
        if (userDetails.hasRole(Roles.ROLE_SUPERADMIN) || user.getVerifier() != null && user.getVerifier().equals(viewer.getUser().getId())) {
            model.addAttribute("showCertificationFiles", true);
            model.addAttribute("certificationFilesNames", profileService.getCertificationFiles(user));
        }

        model.addAttribute("isAllowSave", profileService.isAllowSave(userDetails, viewer));
        model.addAttribute("isAllowSetVerified", profileService.isAllowSetVerified(userDetails, viewer));
        model.addAttribute("isAllowWriteCard", profileService.isAllowWriteCard(userDetails, viewer));
        model.addAttribute("isAllowSaveFinger", profileService.isAllowSaveFinger(userDetails, viewer));

        List<CommunityEntity> entities = communityRepository.findByCreator_Id(user.getId());
        List<Community> communitiesCreated = new ArrayList<>();

        for (CommunityEntity entity : entities) {
            communitiesCreated.add(entity.toDomain());
        }

        if (viewer.getUser().getId().equals(user.getId())) {
            List<Community> communitiesMember = new ArrayList<>();
            entities = communityDao.getByMember(user.getId());

            for (CommunityEntity entity : entities) {
                communitiesMember.add(entity.toDomain());
            }

            model.addAttribute("creatorCommunities", communitiesCreated);
            model.addAttribute("memberCommunities", communitiesMember);
            model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Профиль участника", "/sharer"));
        } else {
            model.addAttribute("communities", communitiesCreated);
            model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Профиль участника " + user.getShortName(), "/sharer/" + user.getIkp()));

            model.addAttribute("randomContacts", contactDao.searchContacts(user.getId(), null, 0, 6, ContactStatus.ACCEPTED, ContactStatus.ACCEPTED, null, "searchString", true));
        }

        String showRegisteregAtFor = settingsManager.getSystemSetting("profile.show-registered-at-for", "ALL");
        if (viewer.getUser().getId().equals(user.getId()) || "ALL".equals(showRegisteregAtFor) || ("ADMINS".equals(showRegisteregAtFor) && viewer.hasRole("ROLE_ADMIN"))) {
            model.addAttribute("showRegisteredAt", true);
        } else {
            model.addAttribute("showRegisteredAt", false);
        }
        model.addAttribute("lastLoginLogEntry", user.getLastLogin());
        model.addAttribute("changesAllowed", profileService.isAllowSave(userDetails, viewer));

        String setting = settingsManager.getUserSetting("profile.filling-info-closed", user.getId());
        boolean show = false;
        if ("true".equals(setting)) show = true;

        model.addAttribute("profileFillingInfoClosed", show);

        //------------------------------------------------------

        // Фактический адрес
        String factCountryListItemIdStr = FieldsService.getFieldStringValue(fieldValueDao.get(user.getId(), "SHARER", FieldConstants.FACT_COUNTRY_SHARER));
        Long factCountryListItemId = VarUtils.getLong(factCountryListItemIdStr, -1l);
        if (factCountryListItemId > -1l) {
            RameraListEditorItem factCountryListItem = rameraListEditorItemDAO.getById(factCountryListItemId);
            if (factCountryListItem != null) {
                model.addAttribute("factCountryListItem", factCountryListItem);
            }
        }
        // Адрес по прописке
        String regCountryListItemIdStr = FieldsService.getFieldStringValue(fieldValueDao.get(user.getId(), "SHARER", FieldConstants.REGISTRATION_COUNTRY_SHARER));
        Long regCountryListItemId = VarUtils.getLong(regCountryListItemIdStr, -1l);
        if (regCountryListItemId > -1l) {
            RameraListEditorItem regCountryListItem = rameraListEditorItemDAO.getById(regCountryListItemId);
            if (regCountryListItem != null) {
                model.addAttribute("regCountryListItem", regCountryListItem);
            }
        }
        // Адрес офиса регистратора
        String registratorCountryListItemIdStr = FieldsService.getFieldStringValue(fieldValueDao.get(user.getId(), "SHARER", FieldConstants.REGISTRATOR_OFFICE_COUNTRY));
        Long registratorCountryListItemId = VarUtils.getLong(registratorCountryListItemIdStr, -1l);
        if (registratorCountryListItemId > -1l) {
            RameraListEditorItem registratorCountryListItem = rameraListEditorItemDAO.getById(registratorCountryListItemId);
            if (registratorCountryListItem != null) {
                model.addAttribute("registratorCountryListItem", registratorCountryListItem);
            }
        }
        model.addAttribute("viewerVerified", viewer.getUser().isVerified());
        model.addAttribute("identifiedWord",user.isSex() ? "Идентифицирован" : "Идентифицирована");
        return "sharer";
    }

    // TODO вероятно эту ссылку тоже выпиливать нужно, ей на смену, как я понял, пришла просто /contacts из ContactsController
    @RequestMapping("/sharer/{ikp}/contacts")
    public String showSharerContactsPage(@PathVariable("ikp") String ikp, Model model) {
        User user = userDataService.getByIkpOrShortLink(ikp);
        if (user == null) {
            throw new RuntimeException("Такого профиля не существует!"); //TODO показывать страницу отсутствующего профиля
        }
        // TODO Переделать
        /*model.addAttribute("currentPageTitle", "Контакты участника " + sharer.getShortName());
        model.addAttribute("profile", sharer);
        model.addAttribute("firstPage", serializationManager.serializeCollection(contactDao.searchContacts(sharer, null, 0, 20, ContactStatus.ACCEPTED, ContactStatus.ACCEPTED, null, "searchString", true)).toString());
        model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Профиль участника " + sharer.getShortName(), "/sharer/" + sharer.getIkp()).add("Контакты участника " + sharer.getShortName(), "/sharer/" + sharer.getIkp() + "/contacts"));
        */
        return "sharerContacts";
    }

    @RequestMapping("/sharer/settings")
    public String showSharerSettingsPage(Model model, HttpServletRequest request) {
        model.addAttribute("activeLoginLogEntries", null/*sessionsService.getActiveLoginLogEntries()*/);
        model.addAttribute("currentSessionId", request.getSession().getId());
        String value = settingsManager.getUserSetting("profile.show-email.mode", SecurityUtils.getUser().getId());
        model.addAttribute("currentShowEmailMode", value != null ? value : "NOBODY");
        model.addAttribute("currentShowEmailListIds", userSettingsService.getLongsList(SecurityUtils.getUser(), "profile.show-email.lists", Collections.EMPTY_LIST));

        List<String> timezoneIds = Arrays.asList(TimeZone.getAvailableIDs());
        Collections.sort(timezoneIds, (o1, o2) -> Integer.compare(TimeZone.getTimeZone(o1).getRawOffset(), TimeZone.getTimeZone(o2).getRawOffset()));
        model.addAttribute("timezones", timezoneIds);

        Map<String, String> timezoneOffsets = new HashMap<>();
        for (String timezoneId : timezoneIds) {
            TimeZone tz = TimeZone.getTimeZone(timezoneId);
            int hours = tz.getRawOffset() / 1000 / 60 / 60;
            String stringHours = "";
            if (hours >= 0) {
                stringHours += "+";
            } else {
                stringHours += "-";
            }
            if (Math.abs(hours) < 10) {
                stringHours += "0";
            }
            stringHours += Math.abs(hours);
            String offset = "UTC " + stringHours + ":00";
            timezoneOffsets.put(timezoneId, offset);
        }
        model.addAttribute("timezoneOffsets", timezoneOffsets);

        UserEntity userEntity = sharerDao.getById(SecurityUtils.getUser().getId());

        String currentTimezoneString = "";
        FieldValueEntity currentTimezoneFieldValue = fieldValueDao.get(userEntity, "TIMEZONE");
        if (currentTimezoneFieldValue != null) {
            currentTimezoneString = FieldsService.getFieldStringValue(currentTimezoneFieldValue);
        }
        model.addAttribute("currentTimezone", currentTimezoneString);

        List<ContactsGroupEntity> contactsGroups = contactsGroupDao.getBySharer(SecurityUtils.getUser().getId());
        ContactsGroupEntity defaultGroup = new ContactsGroupEntity();
        defaultGroup.setId(0L);
        defaultGroup.setName("Список по умолчанию");
        contactsGroups.add(0, defaultGroup);

        model.addAttribute("contactsGroups", contactsGroups);

        String sharerShortLink = userEntity.getShortLink();
        if (org.apache.commons.lang3.StringUtils.isBlank(sharerShortLink)) {
            sharerShortLink = userEntity.getIkp();
        }
        model.addAttribute("sharerShortLink", sharerShortLink);

        model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Профиль участника", "/sharer").add("Настройки", "/sharer/settings"));

        return "sharerSettings";
    }

    @RequestMapping("/certification-agreement-print")
    public String printCertificationAgreementPage(Model model) {
        // TODO Переделать
        RameraTextEntity certificationAgreement = rameraTextDao.getByCode("CERTIFICATION_AGREEMENT");
        if (certificationAgreement == null) {
            throw new RuntimeException("Отсутствует текст с кодом CERTIFICATION_AGREEMENT");
        }
        model.addAttribute("text", certificationAgreement.getText());
        return "printablePage";
    }

    @RequestMapping("/certification-agreement-text")
    @ResponseBody
    public AgreementDto getCertificationAgreementPage() {
        RameraTextEntity certificationAgreement = rameraTextDao.getByCode("CERTIFICATION_AGREEMENT");

        if (certificationAgreement == null) {
            throw new RuntimeException("Отсутствует текст с кодом CERTIFICATION_AGREEMENT");
        }

        AgreementDto result = new AgreementDto();
        result.text = certificationAgreement.getText();
        return result;
    }

    @RequestMapping(value = "/sharer/field/{fieldId}/hidden/{hidden}", method = RequestMethod.POST)
    @ResponseBody
    public String setFieldValueHidden(@PathVariable("fieldId") Long fieldId, @PathVariable("hidden") boolean hidden) {
        // TODO Переделать
        FieldEntity field = fieldDao.getById(fieldId);
        if (field != null) {
            User user = SecurityUtils.getUser();

            fieldsService.setFieldValueHidden(sharerDao.getById(user.getId()), field, hidden);

            String searchString = fieldsService.makeSearchString(user);
            userDataService.saveSearchString(user.getId(), searchString);
            return "{\"result\":\"success\"}";
        } else {
            return "{\"result\":\"error\"}";
        }
    }

    @RequestMapping(value = "/sharer/fieldsgroup/{fieldsGroupId}/hidden/{hidden}", method = RequestMethod.POST)
    @ResponseBody
    public String setFieldValuesGroupHidden(@PathVariable("fieldsGroupId") Long fieldsGroupId, @PathVariable("hidden") boolean hidden) {
        // TODO Переделать
        FieldsGroupEntity fieldsGroup = fieldsGroupDao.getById(fieldsGroupId);
        if (fieldsGroup != null) {
            fieldsService.setFieldValuesGroupHidden(sharerDao.getById(SecurityUtils.getUser().getId()), fieldsGroup, hidden);
            return "{\"result\":\"success\"}";
        } else {
            return "{\"result\":\"error\"}";
        }
    }

    @RequestMapping("/sharer/{ikp}.json")
    @ResponseBody
    public String getByIkp(@PathVariable(value = "ikp") String ikp) {
        // TODO Переделать
        UserEntity userEntity = sharerDao.getByIkp(ikp, null);
        if (userEntity == null) {
            return "{\"result\":\"error\"}";
        } else {
            return serializationManager.serialize(userEntity).toString();
        }
    }

    @RequestMapping(value = "/sharer/me.json", method = {RequestMethod.POST, RequestMethod.GET}, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public UserDataDto getContextUser(@RequestParam(value = "system_option", required = false) String systemOption) {
        UserDataDto result = new UserDataDto(SecurityUtils.getUser());

        if (systemOption != null) {
            result.identificationRequired = "1".equals(settingsManager.getSystemSetting(systemOption));
        }

        result.identificationMode = settingsManager.getUserSetting("identification_mode", SecurityUtils.getUser().getId());

        if (result.identificationMode == null) {
            result.identificationMode = "fingerprint";
        }

        return result;
    }

    @RequestMapping("/sharers.json")
    @ResponseBody
    public String getSharers(@RequestParam("id") SharersList sharers) {
        // TODO Переделать
        return serializationManager.serializeCollection(sharers).toString();
    }

    // возвращает список шареров только с именами и айдишниками(чтобы избежать утечки остальных данных)
    @RequestMapping("/not_deleted_sharers.json")
    @ResponseBody
    public String getNotDeletedSharers() {
        // TODO Переделать
        List<UserEntity> userEntities = sharerDao.getNotDeleted();
        Collections.sort(userEntities, (o1, o2) -> o1.getFullName().compareTo(o2.getFullName()));

        List<StringObjectHashMap> result = new ArrayList<>(userEntities.size());
        for (UserEntity userEntity : userEntities) {
            StringObjectHashMap payload = new StringObjectHashMap();
            payload.put("id", userEntity.getId().toString());
            payload.put("fullName", userEntity.getFullName());
            result.add(payload);
        }

        return serializationManager.serializeCollection(result).toString();
    }

    @TokenProtected
    @RequestMapping("/sharer/init_certification.json")
    @ResponseBody
    public InitCertificationResponseDto initCertification(@RequestParam("sharer_id") Long userId) throws DatatypeConfigurationException, UserCertificationException {
        InitCertificationResponseDto result = new InitCertificationResponseDto();

        User registrator = SecurityUtils.getUser();
        User user = userDataService.getByIdMinData(userId);

        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getEmail());
        UserDetailsImpl registratorDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(SecurityUtils.getUser().getEmail());

        UserCertificationSession userCertificationSession = userCertificationManager.startCertificationSession(registrator, user);

        result.registrator = new UserDataDto(registrator);
        result.sharer = new UserDataDto(user);
        result.sharer_short_name_padeg_2 = Padeg.getCutFIOPadegFS(user.getFullName(), user.isSex(), 2);
        result.sharerProfileFilling = profileService.getProfileFilling(user);
        result.isAllowWriteCard = profileService.isAllowWriteCard(userDetails, registratorDetails);
        result.isAllowSaveFinger = profileService.isAllowSaveFinger(userDetails, registratorDetails);
        result.isAllowSetVerified = !user.isVerified() && profileService.isAllowSetVerified(user.getEmail());
        result.sessionId = userCertificationSession.getSessionId();

        Duration duration = DatatypeFactory.newInstance().newDuration(userCertificationManager.getActiveSessionEndDate(userCertificationSession.getSessionId()).getTime() - new Date().getTime());
        String sessionTimer;

        if (duration.getSign() > 0) {
            sessionTimer = duration.getHours() + "h" + duration.getMinutes() + "m" + duration.getSeconds() + "s";
        } else {
            sessionTimer = "0h0m0s";
        }

        result.sessionTimer = sessionTimer;

        result.docTypes.addAll(userCertificationManager.getDocTypes().stream().map(item -> {
            CertificationDocTypeDto dto = new CertificationDocTypeDto();
            dto.id = item.getId();
            dto.name = item.getName();
            dto.title = item.getTitle();
            dto.minFiles = item.getMinFiles();
            return dto;
        }).collect(Collectors.toList()));

        return result;
    }

    @RequestMapping(value = "/sharer/set_card_number.json", method = RequestMethod.POST)
    @ResponseBody
    public String setCardNumber(@RequestParam("ikp") String ikp,
                                @RequestParam("card_number") String cardNumber,
                                @RequestParam("session_id") String sessionId) throws UserCertificationException {
        try {
            UserCertificationSession userCertificationSession = userCertificationManager.getActiveCertificationSession(sessionId);

            if (userCertificationSession != null) {
                User user = userDataService.getByIkpOrShortLink(ikp);

                UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getEmail());
                UserDetailsImpl registratorDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(SecurityUtils.getUser().getEmail());

                if (!profileService.isAllowWriteCard(userDetails, registratorDetails))
                    throw new ProfileException("Вы не можете записать карту данному участнику");

                profileService.setCardNumber(user, cardNumber);
                return JsonUtils.getSuccessJson().toString();
            }

            throw new UserCertificationException("Сессия идентификации пользователя не найдена или завершена.");
        } catch (ProfileException e) {
            LOGGER.error(e.getMessage(), e);
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping("/sharer/is_allow_set_verified.json")
    @ResponseBody
    public String isAllowSetVerified(@RequestParam("sharer_id") Long userId) {
        User user = userDataService.getByIdMinData(userId);
        return JsonUtils.getJson("allow", Boolean.toString(!user.isVerified() && profileService.isAllowSetVerified(user.getEmail()))).toString();
    }

    private StringObjectHashMap getEditSuccessResult(User user, User editor) {
        StringObjectHashMap map = new StringObjectHashMap();

        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getEmail());
        UserDetailsImpl editorDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(editor.getEmail());

        map.put("isAllowSave", profileService.isAllowSave(userDetails, editorDetails));
        map.put("isAllowWriteCard", profileService.isAllowWriteCard(userDetails, editorDetails));
        map.put("isAllowSaveFinger", profileService.isAllowSaveFinger(userDetails, editorDetails));
        map.put("isAllowSetVerified", !user.isVerified() && profileService.isAllowSetVerified(userDetails, editorDetails));
        map.put("profileFilling", profileService.getProfileFilling(user));
        map.put("success", true);
        return map;
    }

    @RequestMapping(value = "/sharer/save.json", method = RequestMethod.POST)
    @ResponseBody
    public StringObjectHashMap save(HttpServletRequest request, @RequestParam("sharer_id") Long userId) {
        Map<FieldEntity, String> fieldsMap = fieldsService.requestToFieldsMap(request);
        User user = sharerService.saveUserData(userId, fieldsMap, SecurityUtils.getUser().getId());
        return getEditSuccessResult(user, SecurityUtils.getUser());
    }

    @TokenIkpProtected
    @RequestMapping(value = "/sharer/set_agreed.json", method = RequestMethod.POST)
    @ResponseBody
    public String setAgreed(@RequestParam("user_id") Long userId,
                            @RequestParam("session_id") String sessionId) throws UserCertificationException {
        User registrator = SecurityUtils.getUser();
        User user = userDataService.getByIdFullData(userId);
        UserCertificationSession userCertificationSession = userCertificationManager.getActiveCertificationSession(sessionId);

        if (userCertificationSession != null) {
            if (!profileService.isAllowSetVerified(user.getEmail()))
                throw new ProfileException("Вы не можете идентифицировать данного участника");

            if (!userCertificationSession.getUserId().equals(userId))
                throw new ProfileException("Вы не можете идентифицировать данного участника");

            userCertificationManager.setUserAgreed(sessionId, true);
            return serializationManager.serialize(getEditSuccessResult(user, registrator)).toString();
        } else throw new UserCertificationException("Сессия идентификации пользователя не найдена или завершена.");
    }

    @TokenProtected
    @RequestMapping(value = "/sharer/set_verified.json", method = RequestMethod.POST)
    @ResponseBody
    public String setVerified(@RequestParam("sharer_id") Long userId) throws UserCertificationException {
        User registrator = SecurityUtils.getUser();
        User user = userDataService.getByIdFullData(userId);
        UserCertificationSession userCertificationSession = userCertificationManager.finishCertificationSession(registrator, user);

        if (userCertificationSession != null) {
            if (!profileService.isAllowSetVerified(user.getEmail()))
                throw new ProfileException("Вы не можете идентифицировать данного участника");

            user = profileService.setVerified(user, registrator);
            return serializationManager.serialize(getEditSuccessResult(user, registrator)).toString();
        } else throw new UserCertificationException("Сессия идентификации пользователя не найдена или завершена.");
    }

    @RequestMapping(value = "/sharer/okveds.json", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public List<OkvedDto> getOkveds(@RequestParam(value = "id[]", required = true, defaultValue = "") long[] ids) {
        List<OkvedEntity> okvedEntities = okvedDao.getByIds(Order.asc("id"), ids);
        List<OkvedDomain> okveds = OkvedEntity.toDomainList(okvedEntities);
        return OkvedDto.toListDto(okveds);
    }

    @RequestMapping(value = "/sharer/okveds_tree.json")
    @ResponseBody
    public List<OkvedTreeDto> getOkvedsTree(HttpServletRequest request) {
        String query = ServletRequestUtils.getStringParameter(request, "query", "");
        long[] ids = ServletRequestUtils.getLongParameters(request, "id");
        long parentId = ServletRequestUtils.getLongParameter(request, "node", -1L);
        OkvedEntity parent = okvedDao.getById(parentId);
        List<Long> checkedIdsList = new ArrayList<Long>();
        for (long id : ids) {
            checkedIdsList.add(id);
        }
        List<Long> expandedIdsList = new ArrayList<Long>();
        if (ids.length > 0) {
            List<OkvedEntity> checkedOkveds = okvedDao.getByIds(ids);
            for (OkvedEntity okved : checkedOkveds) {
                OkvedEntity parentOkved = okved.getParent();
                while (parentOkved != null) {
                    expandedIdsList.add(parentOkved.getId());
                    parentOkved = parentOkved.getParent();
                }
            }
        }
        List<Long> allowedIds = null;
        if (StringUtils.hasLength(query)) {
            allowedIds = new ArrayList<Long>();
            allowedIds.addAll(checkedIdsList);
            allowedIds.addAll(expandedIdsList);
            for (OkvedEntity okved : okvedDao.getByQuery(query)) {
                int index = 0;
                while (okved != null) {
                    allowedIds.add(okved.getId());
                    if (index > 0) {
                        expandedIdsList.add(okved.getId());
                    }
                    okved = okved.getParent();
                    index++;
                }
            }
        }
        List<OkvedEntity> okveds = okvedDao.getChildren(parent, allowedIds);
        return OkvedTreeDto.toListDto(okveds, checkedIdsList, expandedIdsList);
    }

    @RequestMapping(value = "/sharer/delete_profile.json", method = RequestMethod.POST)
    @ResponseBody
    public String deleteProfile() {
        try {
            profileService.deleteSharer(SecurityUtils.getUser());
            return JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/sharer/delete_other_profile.json", method = RequestMethod.POST)
    @ResponseBody
    public SuccessResponseDto deleteOtherProfile(@RequestParam("sharer_id") Long userId) {
            User user = userDataService.getByIdMinData(userId);
            profileService.deleteSharer(user);
            return SuccessResponseDto.get();

    }

    @RequestMapping(value = "/sharer/set_allow_multiple_sessions.json", method = RequestMethod.POST)
    @ResponseBody
    public String setAllowMultipleSessions(HttpServletRequest request, @RequestParam("allow") boolean allow) {
        userDataService.setAllowMultipleSessions(SecurityUtils.getUser().getId(), allow);
        if (!allow) authService.closeOtherSessions(request.getSession().getId());
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/sharer/close_session.json", method = RequestMethod.POST)
    @ResponseBody
    public String closeSession(@RequestParam("login_log_entry_id") Long loginLogEntryId) {
        //sessionsService.closeSession(loginLogEntry);
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/sharer/close_other_sessions.json", method = RequestMethod.POST)
    @ResponseBody
    public String closeOtherSession(HttpServletRequest request) {
        authService.closeOtherSessions(request.getSession().getId());
        return JsonUtils.getSuccessJson().toString();
    }

    /**
     * Поменять пароль
     *
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequestMapping(value = "/sharer/change_password.json", method = RequestMethod.POST)
    @ResponseBody
    public String changePassword(@RequestParam("old_password") String oldPassword, @RequestParam("new_password") String newPassword) {
        profileService.changePassword(SecurityUtils.getUser(), oldPassword, newPassword);
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/sharer/change_sharer_short_link_name.json", method = RequestMethod.POST)
    @ResponseBody
    public String changeSharerShortLink(@RequestParam("sharer_short_link_name") String sharerShortLink) {
        try {
            String link = profileService.changeSharerShortLink(SecurityUtils.getUser(), sharerShortLink);
            JSONObject result = JsonUtils.getSuccessJson();
            result.put("link", link);
            return result.toString();
        } catch (ProfileException e) {
            LOGGER.error(e.getMessage(), e);
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        } catch (Exception e) {
            return JsonUtils.getErrorJson().toString();
        }
    }

    @RequestMapping(value = "/sharer/init_change_email.json", method = RequestMethod.POST)
    @ResponseBody
    public String initChangeEmail() {
        profileService.initChangeEmail(SecurityUtils.getUser());
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/sharer/complete_change_email.json", method = RequestMethod.POST)
    @ResponseBody
    public String completeChangeEmail(@RequestParam("code") String code, @RequestParam("new_email") String newEmail) {
        try {
            profileService.completeChangeEmail(SecurityUtils.getUser(), code, newEmail);
            return JsonUtils.getSuccessJson().toString();
        } catch (ProfileException e) {
            LOGGER.error(e.getMessage(), e);
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/sharer/setting/set.json", method = RequestMethod.POST)
    @ResponseBody
    public String setSetting(@RequestParam("key") String key, @RequestParam("value") String value) {
        try {
            settingsManager.setUserSetting(key, value, SecurityUtils.getUser());
            return JsonUtils.getSuccessJson().toString();
        } catch (ProfileException e) {
            LOGGER.error(e.getMessage(), e);
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/sharer/setting/get.json", method = RequestMethod.GET)
    @ResponseBody
    public String getSetting(@RequestParam("key") String key, @RequestParam(value = "deafultValue", defaultValue = "") String defaultValue) {
        try {
            String value = settingsManager.getUserSetting(key, SecurityUtils.getUser().getId());
            return JsonUtils.getJson("value", value != null ? value : defaultValue).toString();
        } catch (ProfileException e) {
            LOGGER.error(e.getMessage(), e);
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/sharer/check.json", method = RequestMethod.GET)
    @ResponseBody
    public String check() {
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/sharer/profile_filling.json", method = RequestMethod.GET)
    @ResponseBody
    public ProfileFilling profileFilling() {
        return profileService.getProfileFilling(userDataService.getByIdFullData(SecurityUtils.getUser().getId()));
    }

    @RequestMapping(value = "/sharer/{ikp}/profile_filling.json", method = RequestMethod.GET)
    @ResponseBody
    public ProfileFilling profileFillingByIkp(@PathVariable("ikp") String ikp) {
        return profileService.getProfileFilling(userDataService.getByIkpOrShortLink(ikp));
    }

    @RequestMapping(value = "/sharer/balance.json", method = RequestMethod.GET)
    @ResponseBody
    public String getBalance() {
        JSONObject jsonObject = JsonUtils.getSuccessJson();
        jsonObject.put("balance", StringUtils.formatMoney(sharerDao.getBalance(SecurityUtils.getUser().getId())));
        return jsonObject.toString();
    }

    @RequestMapping(value = "/sharer/upload_registration_agreement.json", method = RequestMethod.POST)
    //@TokenProtected
    @ResponseBody
    public String uploadRegistrationAgreement(HttpServletRequest request) {
        try {
            User user = null;
            String sessionId = null;
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1024 * 1024 * 10);
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> items = upload.parseRequest(request);
            List<FileItem> files = new ArrayList<>();

            for (FileItem item : items) {
                if (!item.isFormField()) {
                    files.add(item);
                } else if (item.getFieldName().equals("sharer_id")) {
                    user = userDataService.getByIdMinData(Long.parseLong(item.getString()));
                } else if (item.getFieldName().equals("session_id")) {
                    sessionId = item.getString();
                }
            }

            if (sessionId != null) {
                UserCertificationSession userCertificationSession = userCertificationManager.getActiveCertificationSession(sessionId);

                if (userCertificationSession != null) {
                    profileService.uploadRegistrationAgreement(user, files);
                    return JsonUtils.getSuccessJson().toString();
                }
            }

            throw new UserCertificationException("Сессия идентификации пользователя не найдена или завершена.");
        } catch (ProfileException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        } catch (UserCertificationException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return JsonUtils.getErrorJson("Ошибка загрузки файла").toString();
        }
    }

    @RequestMapping(value = "/sharer/{id}/{fieldId}/fieldFiles.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public List<UserFieldFileDto> getFieldFiles(@PathVariable("id") UserEntity userEntity, @PathVariable("fieldId") FieldEntity field) {
        FieldValueEntity fieldValue = fieldValueDao.get(userEntity, field);
        List<FieldFileEntity> fieldFiles = fieldFileDao.getByFieldValue(fieldValue);
        return UserFieldFileDto.toDtoList(FieldFileEntity.toDomainList(fieldFiles));
    }

    @RequestMapping(value = "/sharer/{id}/{fieldId}/saveFieldFiles.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto saveFieldFiles(@PathVariable("id") UserEntity userEntity,
                                            @PathVariable("fieldId") FieldEntity field,
                                            @RequestBody List<FieldFileEntity> fieldFilesForm) {
        fieldsService.saveFieldFiles(field, fieldFilesForm, userEntity);
        return SuccessResponseDto.get();
    }

    // Возвращает список сертифицированных шарером пользователей(только нужную инфу)
    @RequestMapping("/verified_sharers_list.json")
    @ResponseBody
    public String getSharerVerifiedList() {
        if (SecurityUtils.getUserDetails() != null) {
            List<VerifiedSharerDto> result = sharerDao.getVerifiedSharers(SecurityUtils.getUser().getId());
            return new Gson().toJson(result);
        } else {
            return JsonUtils.getErrorJson().toString();
        }
    }

    // Возвращает список сертифицированных шарером объединений(только нужную инфу)
    @RequestMapping("/verified_communities_list.json")
    @ResponseBody
    public String getCommunitiesVerifiedList() {
        if (SecurityUtils.getUserDetails() != null) {
            List<CommunityEntity> result = communityDao.getVerifiedCommunities(SecurityUtils.getUser().getId());
//			GsonBuilder b = new GsonBuilder();
//			b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
//			Gson gson = b.create();
//			return gson.toJson(result);
            return serializationManager.serializeCollection(result).toString();
        } else {
            return JsonUtils.getErrorJson().toString();
        }
    }

    @RequestMapping(value = "/sharer/{ikp}/certification-files/{name}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getCertificationFile(@PathVariable("ikp") String ikp, @PathVariable("name") String name) {
        ResponseEntity<byte[]> result = null;
        InputStream inputStream = null;
        File file = null;
        try {
            User sharer = userDataService.getByIkpOrShortLink(ikp);
            if (sharer == null) {
                throw new ProfileException("Запрашиваемый файл отсутствует либо едостаточно прав для его просмотра");
            }

            if (SecurityUtils.getUserDetails() == null) {
                throw new ProfileException("Запрашиваемый файл отсутствует либо едостаточно прав для его просмотра");
            }

            // Если профиль просматривает регистратор либо администратор - то нужно показывать ему сертификационные файлы
            if (SecurityUtils.getUserDetails().hasRole(Roles.ROLE_SUPERADMIN) || sharer.getVerifier() != null && sharer.getVerifier().equals(SecurityUtils.getUser().getId())) {
                file = profileService.getCertificationFile(sharer, name);
            }

            if (file == null) {
                throw new ProfileException("Запрашиваемый файл отсутствует либо едостаточно прав для его просмотра");
            }

            inputStream = new FileInputStream(file);
            byte[] resultData = IOUtils.toByteArray(inputStream);
            HttpHeaders headers = new HttpHeaders();

            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            try {
                //mediaType = MediaType.parseMediaType(fileContentWrapper.getMimeType());
                Magic parser = new Magic();
                MagicMatch match = parser.getMagicMatch(resultData);
                String mimeTypeString = match.getMimeType();
                mediaType = MediaType.parseMediaType(mimeTypeString);
            } catch (Exception e) {
                // do nothing
            }

            headers.setContentType(mediaType);
            headers.set("Content-Disposition", "attachment; filename=" + file.getName());
            headers.setContentLength(resultData.length);

            result = new ResponseEntity<byte[]>(resultData, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            result = new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return result;
    }

    /**
     * Функция поиска сертифицированных участников системы
     *
     * @param firstResult
     * @param maxResult
     * @return
     */
    private Function<String, List<UserEntity>> getFunctionSearchVerifiedSharers(int firstResult, int maxResult, List<Long> excludeIds) {
        /*if (excludeIds == null) {
            excludeIds = new ArrayList<>();
		}
		excludeIds.add(currentSharer.getId());
		final List<Long> ids = excludeIds;*/
        return query -> sharerDao.searchVerified(query, firstResult, maxResult, excludeIds);
    }

    /**
     * Функция поиска активных участников системы
     *
     * @param firstResult
     * @param maxResult
     * @param excludeIds
     * @return
     */
    private Function<String, List<UserEntity>> getFunctionSearchActiveSharers(int firstResult, int maxResult, List<Long> excludeIds) {
		/*ExceptionUtils.check(currentSharer == null, "Не установлен текущий пользователь");
		if (excludeIds == null) {
			excludeIds = new ArrayList<>();
		}
		excludeIds.add(currentSharer.getId());
		final List<Long> ids = excludeIds;*/
        return query -> sharerDao.searchActive(query, firstResult, maxResult, excludeIds);
    }

    private Function<String, List<User>> getFunctionSearchUsersByIds(List<Long> ids) {
        return query -> userDataService.getByIds(ids);
    }

    /**
     * Выполнить поиск участников по функции поиска
     *
     * @param query
     * @param searchFunction
     * @return
     */
    private List<SearchUserDto> getSearchUsersDto(String query, Function<String, List<UserEntity>> searchFunction) {
        List<UserEntity> userEntities = searchFunction.apply(query);
        return SearchUserDto.toDtoList(userEntities);
    }

    private List<SearchUserDto> getSearchUsersDtoFromDomainUsers(String query, Function<String, List<User>> searchFunction) {
        List<User> users = searchFunction.apply(query);
        return SearchUserDto.toDtoListFromDomainUsers(users);
    }

    /**
     * Поиск сертифицированных участников системы
     *
     * @param query
     * @return
     */
    @RequestMapping(value = "/sharer/searchVerified.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<SearchUserDto> searchVerified(@RequestParam(value = "query", required = true) String query,
                                              @RequestParam(value = "id[]", required = false, defaultValue = "") List<Long> ids) {
        return getSearchUsersDto(query, getFunctionSearchVerifiedSharers(0, 20, ids));
    }

    /**
     * Поиск активных участников системы
     *
     * @param query
     * @return
     */
    @RequestMapping(value = "/sharer/searchActive.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<SearchUserDto> searchActive(@RequestParam(value = "query", required = true) String query,
                                            @RequestParam(value = "id[]", required = false, defaultValue = "") List<Long> ids) {
        return getSearchUsersDto(query, getFunctionSearchActiveSharers(0, 20, ids));
    }

    /**
     * Поиск активных участников системы
     *
     * @return
     */
    @RequestMapping(value = "/sharer/searchActiveByIds.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<SearchUserDto> searchActiveByIds(@RequestBody List<Long> ids) {
        return getSearchUsersDtoFromDomainUsers(null, getFunctionSearchUsersByIds(ids));
    }

    /**
     * Текущее время сервера
     *
     * @return
     */
    @RequestMapping(value = "/sharer/server_time.json", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public ServerTimeDto getServerTime() {
        long timeStamp = new Date().getTime();
        TimeZone timezone = TimeZone.getDefault();
        int timeZoneOffset = timezone.getOffset(timeStamp);
        return new ServerTimeDto(timeStamp, timeZoneOffset);
    }


}