package ru.askor.blagosfera.web.controllers.ng.user;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.exception.RecaptchaException;
import ru.askor.blagosfera.core.services.contacts.ContactsDataService;
import ru.askor.blagosfera.core.services.contacts.ContactsGroupDataService;
import ru.askor.blagosfera.core.services.contacts.ContactsService;
import ru.askor.blagosfera.core.services.invite.InvitationDataService;
import ru.askor.blagosfera.core.services.jivosite.JivositeService;
import ru.askor.blagosfera.core.services.registrator.RegistratorDataService;
import ru.askor.blagosfera.core.services.security.AuthService;
import ru.askor.blagosfera.core.services.user.UserService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.field.FieldRepository;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.askor.blagosfera.domain.exception.AuthenticationException;
import ru.askor.blagosfera.domain.invite.Invitation;
import ru.askor.blagosfera.domain.jivosite.JivositeInfo;
import ru.askor.blagosfera.domain.listEditor.ListEditor;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.registrator.RegistratorDomain;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.askor.blagosfera.domain.user.UserRole;
import ru.askor.blagosfera.web.controllers.ng.user.dto.*;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.model.ImageType;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldFileEntity;
import ru.radom.kabinet.model.registration.RegistrationRequest;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorDomainService;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.bio.TokenIkpProtected;
import ru.radom.kabinet.services.InvitationService;
import ru.radom.kabinet.services.PassportCitizenshipFieldsSettings;
import ru.radom.kabinet.services.PassportCitizenshipSettings;
import ru.radom.kabinet.services.ProfileService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.image.ImagesService;
import ru.radom.kabinet.services.registration.RegistrationRequestService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.Roles;
import ru.radom.kabinet.web.admin.dto.CreateSupportRequestDto;
import ru.radom.kabinet.web.admin.dto.SaveAvatarResultDto;
import ru.radom.kabinet.web.admin.dto.SaveFeedbackStatusDto;
import ru.radom.kabinet.web.admin.dto.SaveUserAvatarDto;
import ru.radom.kabinet.web.contacts.dto.ContactDto;
import ru.radom.kabinet.web.invite.dto.InviteCountDto;
import ru.radom.kabinet.web.user.dto.UserRoleDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Maxim Nikitin on 01.04.2016.
 */
@PreAuthorize("permitAll")
@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger logger = LoggerFactory.createLogger(UserController.class);
    @Autowired
    private ContactsDataService contactsDataService;
    @Autowired
    ProfileService profileService;
    @Autowired
    private ContactsService contactsService;
    @Autowired
    private ContactsGroupDataService contactsGroupDataService;
    @Autowired
    private ListEditorDomainService listEditorDomainService;
    @Autowired
    private CommunityDataService communityDataService;
    @Autowired
    private FieldsService fieldsService;
    @Autowired
    private FieldRepository fieldRepository;
    @Autowired
    private RegistrationRequestService registrationRequestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegistratorDataService registratorDataService;
    @Autowired
    private JivositeService jivositeService;
    @Autowired
    private ImagesService imagesService;
    @Autowired
    private UserService userService;
    @Autowired
    private InvitationDataService invitationDataService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private AuthService authService;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private UserDataService userDataService;

    @RequestMapping(value = "/identify.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto identify(@RequestParam(name = "ikp", required = false) String ikp) {
        UserDto userDto = new UserDto();
        UserDetailsImpl userDetails = SecurityUtils.getUserDetails();

        if (userDetails != null) {
            User user;

            if (ikp == null) {
                userDto.authorised = true;
                user = userDataService.getByIdFullData(userDetails.getUser().getId());
                //user = userDetails.getUser();
            } else {
                user = userService.getUserByIkp(ikp);
            }

            if (user != null) {
                userDto.username = user.getEmail();
                userDto.ikp = user.getIkp();
                userDto.avatar = user.getAvatar();
                userDto.shortName = user.getShortName();
                userDto.verified = user.isVerified();
                userDto.firstName = user.getFirstName();
                userDto.secondName = user.getSecondName();
                userDto.lastName = user.getLastName();
                userDto.sex = user.isSex();
                userDto.roles.addAll(UserRoleDto.toDtoList(user.getRoles()));

                String mode = settingsManager.getUserSetting("identification_mode", user.getId());
                userDto.identificationMode = mode != null ? mode : "fingerprint";

                String phone = settingsManager.getUserSetting("mobile_phone.number", SecurityUtils.getUser().getId());
                if (phone != null) {
                    phone = phone.trim().replaceAll(" ", "").replaceAll("-", "");
                    userDto.phone = phone.length() > 9 ? phone.substring(0, 5) + "*" + phone.substring(phone.length() - 3) : phone;
                }
            }
        }

        return userDto;
    }

    @RequestMapping(value = "/login.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam(name = "u") String username,
                        @RequestParam(name = "p") String password,
                        @RequestParam(name = "r", required = false, defaultValue = "false") Boolean rememberMe,
                        @RequestParam(name = "c") String captchaResponse) {

        try {
            authService.login(username, password, rememberMe, captchaResponse, request, response);
        } catch (RecaptchaException e) {
            return "C";
        } catch (AuthenticationException e) {
            return "P";
        }

        return "OK";
    }

    @TokenIkpProtected
    @RequestMapping(value = "/loginWithFingerprint.json", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String loginWithFingerprint(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam(name = "u") String username,
                        @RequestParam(name = "r", required = false, defaultValue = "false") Boolean rememberMe) {
        try {
            authService.login(username, rememberMe, request, response);
        } catch (RecaptchaException e) {
            return "C";
        } catch (AuthenticationException e) {
            return "P";
        }

        return "OK";
    }

    @RequestMapping(value = "/logout.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return "OK";
    }

    @RequestMapping(value = "/restorePassword.json", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String restorePassword(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam(name = "u") String username,
                        @RequestParam(name = "c", required = false) String captchaResponse) {
        try {
            authService.restorePassword(username, captchaResponse, request, response);
        } catch (RecaptchaException e) {
            return "C";
        }

        return "OK";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/profile.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProfileDto getUserProfile(@RequestParam(name = "ikp") String ikp) throws Exception {
        User user = userService.getUserByIkp(ikp);

        if (user == null) throw new Exception("USER_NOT_FOUND");
        RegistrationRequest registrationRequest = null;
        if (!user.isVerified()) {
            registrationRequest = registrationRequestService.getByObject(user);
        }

        ProfileDto profileDto = new ProfileDto(user,registrationRequest != null ? registrationRequest.toDomain() : null,SecurityUtils.getUser().getId());

        if (SecurityUtils.getUser().getIkp().equals(ikp)) {
            profileDto.isCurrentUser = true;
            profileDto.basicInfoVisibilityMap = userService.getBasicInfoVisibilityMap(user);
            profileDto.factAddressVisibilityMap = userService.getFactAddressVisibilityMap(user);
            profileDto.regAddressVisibilityMap = userService.getRegAddressVisibilityMap(user);
            profileDto.registratorOfficeAddressVisibilityMap = userService.getRegistratorOfficeAddressVisibilityMap(user);
            profileDto.registratorDataAddressVisibilityMap = userService.getRegistratorDataAddressVisibilityMap(user);


        } else {
            profileDto.email = null;
        }

        if (profileDto.verified) {
            User verifier = userService.getUserById(profileDto.verifiedBy);
            Assert.notNull(verifier);
            profileDto.verifierIkp = verifier.getIkp();
            profileDto.verifierGender = verifier.isSex() ? "male" : "female";
            profileDto.verifierFirstName = verifier.getFirstName();
            profileDto.verifierMiddleName = verifier.getSecondName();
            profileDto.verifierLastName = verifier.getLastName();
            profileDto.registratorLevel =  registratorDataService.getRegistratorLevelById(user.getId());
            profileDto.setVerifiedCommunitiesCount(userDataService.getVerifiedCommunitiesCount(user.getId()));
            profileDto.setVerifiedRegistratorsCount(userDataService.getVerifiedRegistratorsCount(user.getId()));
            profileDto.setVerifiedUsersCount(userDataService.getVerifiedUsersCount(user.getId()));
            InviteCountDto inviteCountDto = invitationDataService.getInviteCountData(user.getId());
            profileDto.countInvitedRegistered = Long.valueOf(inviteCountDto.getCountRegisterd());
            profileDto.countInvitedVerified = Long.valueOf(inviteCountDto.getCountVerified());
            profileDto.countInvitedReg1Level = inviteCountDto.getRegistratorsLevel1InvitedSharersCount();
            profileDto.countInvitedReg2Level = inviteCountDto.getRegistratorsLevel2InvitedSharersCount();
            profileDto.countInvitedReg3Level = inviteCountDto.getRegistratorsLevel3InvitedSharersCount();
        }
        if (SecurityUtils.getUser().getId() != user.getId()) {
            profileDto.contact = contactsDataService.getByUserAndOther(SecurityUtils.getUser().getId(),user.getId());
            profileDto.contactGroups = contactsGroupDataService.getByUser(SecurityUtils.getUser().getId());
        }
        profileDto.passportCitizenshipSettings = PassportCitizenshipSettings.getInstance().getSettings();
        profileDto.setCommunitiesCreated(communityDataService.getCommunitiesCreated(user.getId()));
        profileDto.setCommunitiesMember(communityDataService.getCommunitiesMember(user.getId()));
        profileDto.superadmin = SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN);
        profileDto.profileFillingPercent = profileService.getProfileFilling(user).getPercent();

        return profileDto;
    }
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/profile/save.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessResponseDto saveUserProfile(@RequestBody SaveProfileDto saveProfileDto) throws Exception {
        userService.saveUserProfile(saveProfileDto.getBasicInformation(),saveProfileDto.getRegAddress(),saveProfileDto.getFactAddress(),
                saveProfileDto.getRegistratorOfficeAddress(),saveProfileDto.getRegistratorData());
        return SuccessResponseDto.get();
    }
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/profile/deleteRequest.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessResponseDto deleteRegistrationRequest(@RequestParam("request_id") Long requestId) throws Exception {
        registrationRequestService.deleteRequest(requestId, SecurityUtils.getUser());
        return SuccessResponseDto.get();
    }
    @RequestMapping(value = "/contacts/add.json", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    @ResponseBody
    public Contact addContactJson(@RequestBody ContactsAddDto contactsAddDto) {
        return contactsService.addContact(SecurityUtils.getUser().getId(), contactsAddDto.getOtherId(), contactsAddDto.getGroupIds());
    }
    @RequestMapping(value = "/contacts/delete.json", produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
    @ResponseBody
    public Contact deleteContactJson(@RequestParam(value = "other_id", defaultValue = "-1") Long otherId) {
        return contactsService.deleteContact(SecurityUtils.getUser().getId(), otherId);
    }
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/profile/signature/save.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String saveUserProfile(@RequestBody SaveSignatureDto saveSignatureDto) throws Exception {
        return userService.saveUserSignature(saveSignatureDto.getSignature(), SecurityUtils.getUser().getId());
    }
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/avatar/save.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    SaveAvatarResultDto save(@RequestBody SaveUserAvatarDto form) {
        SaveAvatarResultDto saveAvatarResultDto = new SaveAvatarResultDto();
        try {
            User user = userService.saveUserAvatar(form.getUrl(), form.getCroppedUrl(), SecurityUtils.getUser().getId());
            saveAvatarResultDto.setUrl(user.getAvatarSrc());
            saveAvatarResultDto.setCroppedUrl(user.getAvatar());
        } catch (Throwable e) {
            String message = e.getCause() != null && e.getCause().getMessage() != null ? e.getCause().getMessage() : e.getMessage();
            saveAvatarResultDto.setError(message);
            saveAvatarResultDto.setStatus("error");
            return saveAvatarResultDto;
        }
        saveAvatarResultDto.setStatus("ok");
        return saveAvatarResultDto;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/invitation.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public InvitationDto getInvitation(@RequestParam(name = "user_id") Long userId) {
        Invitation invitation = invitationService.findAcceptedInvitationByUserId(userId);
        return invitation != null ? new InvitationDto(invitation) : null;
    }
    @RequestMapping(value = "/jivosite/info.json",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll")
    public @ResponseBody
    JivositeInfo getInfo() {
        JivositeInfo jivositeInfo = new JivositeInfo();
        if (SecurityUtils.getUser() != null) {
            jivositeInfo.setUserInfo(jivositeService.getUserInfo(SecurityUtils.getUser().getId()));
            jivositeInfo.setUserToken(jivositeService.getUserToken(SecurityUtils.getUser().getId()));
            jivositeInfo.setAuthorized(true);
        }
        else {
            jivositeInfo.setAuthorized(false);
        }
        return jivositeInfo;
    }
    @RequestMapping(value = "/country_codes_mapping.json",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll")
    public @ResponseBody CountryCodeMappingDto getCountryCodesMappingAccordingToOld(){
        CountryCodeMappingDto countryCodeMappingDto = new CountryCodeMappingDto();
        ListEditor listEditor = listEditorDomainService.getByName("country_id");
        Map<String,String> idToCountryMapping = new HashMap<>();
        Map<String,String> countryToIdMapping = new HashMap<>();
        for (ListEditorItem listEditorItem : listEditor.getItems()) {
           idToCountryMapping.put(listEditorItem.getId().toString(), listEditorItem.getText());
           countryToIdMapping.put(listEditorItem.getText(),listEditorItem.getId().toString());
        }
        countryCodeMappingDto.setCountryToIdMapping(countryToIdMapping);
        countryCodeMappingDto.setIdToCountryMapping(idToCountryMapping);
        return countryCodeMappingDto;

    }
    @RequestMapping(value = "/fields/visibility/basic_information.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Boolean changeFieldVisibiltyBasicInformation(@RequestBody FiedVisibilityDto fieldVisibilityDto) {
        userService.setFieldVisibilityBasicInformation(fieldVisibilityDto.getName(), fieldVisibilityDto.isValue());
        return fieldVisibilityDto.isValue();
    }
    @RequestMapping(value = "/fields/visibility/registration_address.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Boolean changeFieldVisibiltyRegistrationAddress(@RequestBody FiedVisibilityDto fieldVisibilityDto) {
        userService.setFieldVisibilityRegistratrationAddress(fieldVisibilityDto.getName(), fieldVisibilityDto.isValue());
        return fieldVisibilityDto.isValue();
    }
    @RequestMapping(value = "/fields/visibility/fact_address.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Boolean changeFieldVisibiltyFactAddress(@RequestBody FiedVisibilityDto fieldVisibilityDto) {
        userService.setFieldVisibilityFactAddress(fieldVisibilityDto.getName(), fieldVisibilityDto.isValue());
        return fieldVisibilityDto.isValue();
    }
    @RequestMapping(value = "/fields/visibility/registrator_office_address.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Boolean changeFieldVisibiltyRegistratorOfficeAddress(@RequestBody FiedVisibilityDto fieldVisibilityDto) {
        userService.setFieldVisibilityRegistratorOfficeAddress(fieldVisibilityDto.getName(), fieldVisibilityDto.isValue());
        return fieldVisibilityDto.isValue();
    }
    @RequestMapping(value = "/fields/visibility/registrator_data.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Boolean changeFieldVisibiltyRegistratorInfo(@RequestBody FiedVisibilityDto fieldVisibilityDto) {
        userService.setFieldVisibilityRegistratorInfo(fieldVisibilityDto.getName(), fieldVisibilityDto.isValue());
        return fieldVisibilityDto.isValue();
    }
}
