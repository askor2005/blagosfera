package ru.askor.blagosfera.web.controllers.ng.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.services.notification.SmsService;
import ru.askor.blagosfera.core.services.security.AuthService;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.crypt.domain.HttpException;
import ru.askor.blagosfera.data.jpa.repositories.RameraTextsRepository;
import ru.askor.blagosfera.data.redis.services.UserSessionDataService;
import ru.askor.blagosfera.domain.notification.sms.SmsNotification;
import ru.askor.blagosfera.domain.notification.sms.SmsNotificationType;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.blagosfera.web.controllers.ng.user.dto.*;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.model.RameraTextEntity;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.bio.TokenIkpProtected;
import ru.radom.kabinet.services.ProfileService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.services.sharer.UserSettingsService;
import ru.radom.kabinet.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by vtarasenko on 21.04.2016.
 */
@RestController
@RequestMapping("/api/user/settings")
public class UserSettingsController {

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserSessionDataService userSessionDataService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private RameraTextsRepository rameraTextsRepository;

    @Autowired
    private SmsService smsService;

    @PreAuthorize("hasAnyRole('USER')")
    @RequestMapping(value = "/settings.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SharerSettingsDto getSettings(HttpServletRequest request) {
        SharerSettingsDto sharerSettingsDto = new SharerSettingsDto();

        sharerSettingsDto.userSessions.addAll(userSessionDataService.getUserSessions(SecurityUtils.getUserDetails().getUsername()));
        sharerSettingsDto.currentSessionId = request.getSession().getId();
        sharerSettingsDto.currentShowEmailMode = userSettingsService.get(SecurityUtils.getUser(), "profile.show-email.mode", "NOBODY");
        sharerSettingsDto.currentShowEmailListId = userSettingsService.getLongsList(SecurityUtils.getUser(), "profile.show-email.lists", Collections.EMPTY_LIST);

        List<String> timezoneIds = Arrays.asList(TimeZone.getAvailableIDs());
        Collections.sort(timezoneIds, (o1, o2) -> Integer.compare(TimeZone.getTimeZone(o1).getRawOffset(),TimeZone.getTimeZone(o2).getRawOffset()));
        sharerSettingsDto.timezones.addAll(timezoneIds);
        Map<String, String> timezoneOffsets = new HashMap<>();

        for (String timezoneId : timezoneIds) {
            TimeZone tz = TimeZone.getTimeZone(timezoneId);
            int hours = tz.getRawOffset() / 1000 / 60 / 60;
            String stringHours = "";

            if (hours >= 0) stringHours += "+";
            else stringHours += "-";

            if (Math.abs(hours) < 10) stringHours += "0";

            stringHours += Math.abs(hours);
            String offset = "UTC " + stringHours + ":00";
            timezoneOffsets.put(timezoneId, offset);
        }

        sharerSettingsDto.timezoneOffsets.putAll(timezoneOffsets);
        UserEntity userEntity = sharerDao.getById(SecurityUtils.getUser().getId());
        String timezone = settingsManager.getUserSetting("profile.timezone", SecurityUtils.getUser().getId());
        sharerSettingsDto.currentTimezone = timezone != null ? timezone : "";
        String sharerShortLink = userEntity.getShortLink();

        if (org.apache.commons.lang3.StringUtils.isBlank(sharerShortLink)) {
            sharerShortLink = userEntity.getIkp();
        }

        sharerSettingsDto.sharerShortLink = sharerShortLink;
        sharerSettingsDto.allowMultipleSessions = SecurityUtils.getUser().isAllowMultipleSessions();
        sharerSettingsDto.interfaceTooltipEnable = settingsManager.getUserSettingAsBoolean("interface.tooltip.enable", SecurityUtils.getUser().getId(), true);
        sharerSettingsDto.interfaceTooltipDelayShow = settingsManager.getUserSettingAsObject("interface.tooltip.delay.show", SecurityUtils.getUser().getId(), Long.class, 2L);
        sharerSettingsDto.interfaceTooltipDelayHide = settingsManager.getUserSettingAsObject("interface.tooltip.delay.hide", SecurityUtils.getUser().getId(), Long.class, 0L);

        sharerSettingsDto.phoneVerify = getPhoneVerify();

        sharerSettingsDto.identificationMode = settingsManager.getUserSetting("identification_mode", SecurityUtils.getUser().getId());

        if (sharerSettingsDto.identificationMode == null || !sharerSettingsDto.phoneVerify.verified) {
            sharerSettingsDto.identificationMode = "fingerprint";
            settingsManager.setUserSetting("identification_mode", sharerSettingsDto.identificationMode, SecurityUtils.getUser());
        }

        return sharerSettingsDto;
    }

    @PreAuthorize("hasAnyRole('USER')")
    @RequestMapping(value = "/phoneverify.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PhoneVerifyDto getPhoneVerify() {
        PhoneVerifyDto phoneVerify = new PhoneVerifyDto();
        phoneVerify.verified = settingsManager.getUserSettingAsBoolean("mobile_phone.verified", SecurityUtils.getUser().getId(), false);

        if (phoneVerify.verified) {
            phoneVerify.secondsLeft = 0;
            phoneVerify.canVerify = false;
        } else {
            String codeSentAtString = settingsManager.getUserSetting("mobile_phone.verification_started_at", SecurityUtils.getUser().getId());

            if (codeSentAtString == null) {
                phoneVerify.secondsLeft = 0;
                phoneVerify.canVerify = false;
            } else {
                int timeout = settingsManager.getSystemSettingAsInt("mobile_phone.verification_timeout", 300);
                LocalDateTime codeSentAt = LocalDateTime.parse(codeSentAtString);
                LocalDateTime now = LocalDateTime.now();
                phoneVerify.secondsLeft = timeout - (int) ChronoUnit.SECONDS.between(codeSentAt, now);
                if (phoneVerify.secondsLeft < 0) phoneVerify.secondsLeft = 0;

                String code = settingsManager.getUserSetting("mobile_phone.verification_code", SecurityUtils.getUser().getId());
                phoneVerify.canVerify = true;

                if (code == null || "000000".equals(code)) {
                    phoneVerify.canVerify = false;
                }

                if (phoneVerify.secondsLeft == 0) {
                    phoneVerify.canVerify = false;
                }
            }
        }

        phoneVerify.phoneNumber = settingsManager.getUserSetting("mobile_phone.number", SecurityUtils.getUser().getId());

        if (phoneVerify.phoneNumber == null) {
            User user = userDataService.getByIdFullData(SecurityUtils.getUser().getId());
            phoneVerify.phoneNumber = user.getFieldValueByInternalName("MOB_TEL");
            //sharerSettingsDto.mobilePhoneVerified = false; // TODO set to false when phone changes

            if (phoneVerify.phoneNumber != null) {
                settingsManager.setUserSetting("mobile_phone.number", phoneVerify.phoneNumber, SecurityUtils.getUser());
                settingsManager.setUserSetting("mobile_phone.verified", phoneVerify.verified ? "true" : "false", SecurityUtils.getUser());
            }
        }

        return phoneVerify;
    }

    @PreAuthorize("hasAnyRole('USER')")
    @RequestMapping(value = "/sendphoneverifycode.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PhoneVerifyDto sendPhoneVerifyCode(@RequestBody ActivatePhoneDto activatePhoneDto) {
        PhoneVerifyDto result = getPhoneVerify();
        String oldPhoneNumber = settingsManager.getUserSetting("mobile_phone.number", SecurityUtils.getUser().getId());
        String phoneNumber = activatePhoneDto.getPhoneNumber() != null ? activatePhoneDto.getPhoneNumber() : oldPhoneNumber;
        if ((oldPhoneNumber != null) && (oldPhoneNumber.equals(phoneNumber))) {
            if (result.verified) return result;
            if (result.canVerify) return result;
            if (result.secondsLeft > 0) return result;
        }
        else {
            result.phoneNumber = phoneNumber;
            result.verified = false;
            result.canVerify = false;
            result.secondsLeft = 0;
            settingsManager.setUserSetting("mobile_phone.number", phoneNumber, SecurityUtils.getUser());
            settingsManager.setUserSetting("mobile_phone.verified", "false", SecurityUtils.getUser());
            settingsManager.setUserSetting("identification_mode", "fingerprint", SecurityUtils.getUser());
            settingsManager.deleteUserSetting("mobile_phone.verification_code", SecurityUtils.getUser().getId());
            settingsManager.deleteUserSetting("mobile_phone.verification_started_at",SecurityUtils.getUser().getId());
        }
        RameraTextEntity messageEntity = rameraTextsRepository.findOneByCode("SMS_PHONE_VERIFICATION_CODE");
        if (messageEntity == null) return result;

        String code = StringUtils.randomNumericString();
        String codeSentAtString = LocalDateTime.now().toString();

        settingsManager.setUserSetting("mobile_phone.verification_code", code, SecurityUtils.getUser());
        settingsManager.setUserSetting("mobile_phone.verification_started_at", codeSentAtString, SecurityUtils.getUser());

        result.secondsLeft = settingsManager.getSystemSettingAsInt("mobile_phone.verification_timeout", 300);
        result.canVerify = true;

        String number = result.phoneNumber.trim().replaceAll(" ", "").replaceAll("-", "");
        String numberShort = number.length() > 9 ? number.substring(0, 5) + "*" + number.substring(number.length() - 3) : number;

        String text = messageEntity.getText()
                .replaceAll("%code%", code)
                .replaceAll("%number%", numberShort);

        try {
            text = smsService.send(new SmsNotification(SmsNotificationType.SMS, number, text));
        } catch (JsonProcessingException | NoSuchAlgorithmException | HttpException | UnsupportedEncodingException ignored) {
        }

        return result;
    }

    @PreAuthorize("hasAnyRole('USER')")
    @RequestMapping(value = "/phoneverifycode.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PhoneVerifyDto verifyCode(@RequestParam(name = "c") String code) {
        PhoneVerifyDto result = getPhoneVerify();
        if (result.verified) return result;
        if (!result.canVerify) return result;
        if (result.secondsLeft == 0) return result;

        String realCode = settingsManager.getUserSetting("mobile_phone.verification_code", SecurityUtils.getUser().getId());

        result.verified = realCode.equals(code);
        result.canVerify = false;

        if (!result.verified) {
            settingsManager.setUserSetting("mobile_phone.verification_code", "000000", SecurityUtils.getUser());
        } else {
            settingsManager.setUserSetting("mobile_phone.verified", "true", SecurityUtils.getUser());
            result.secondsLeft = 0;
        }

        return result;
    }

    @TokenIkpProtected
    @PreAuthorize("hasAnyRole('USER')")
    @RequestMapping(value = "/setidentificationmode.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String saveIdentificationMode(@RequestParam(name = "m") String mode) {
        boolean verified = settingsManager.getUserSettingAsBoolean("mobile_phone.verified", SecurityUtils.getUser().getId(), false);

        if (verified) {
            if (!mode.equals("sms") && !mode.equals("fingerprint")) mode = "fingerprint";
        } else {
            mode = "fingerprint";
        }

        settingsManager.setUserSetting("identification_mode", mode, SecurityUtils.getUser());
        return mode;
    }

    @RequestMapping(value = "/set_allow_multiple_sessions.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER')")
    public SuccessResponseDto setAllowMultipleSessions(HttpServletRequest request, @RequestParam("allow") boolean allow) {
        userDataService.setAllowMultipleSessions(SecurityUtils.getUser().getId(), allow);
        if (!allow) authService.closeOtherSessions(request.getSession().getId());
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/close_other_sessions.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER')")
    public SuccessResponseDto closeOtherSessions(HttpServletRequest request) {
        authService.closeOtherSessions(request.getSession().getId());
        return SuccessResponseDto.get();
    }

    @PreAuthorize("hasAnyRole('USER')")
    @RequestMapping(value = "/init_change_email.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessResponseDto initChangeEmail() {
        profileService.initChangeEmail(userDataService.getByIdMinData(SecurityUtils.getUser().getId()));
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/complete_change_email.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER')")
    public SuccessResponseDto completeChangeEmail(@RequestBody ChangeEmailDto changeEmailDto) {
        profileService.completeChangeEmail(userDataService.getByIdMinData(SecurityUtils.getUser().getId()), changeEmailDto.getCode(), changeEmailDto.getEmail());
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/change_password.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER')")
    public SuccessResponseDto changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        profileService.changePassword(userDataService.getByIdMinData(SecurityUtils.getUser().getId()), changePasswordDto.getOldPassword(), changePasswordDto.getNewPassword());
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/change_sharer_short_link_name.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER')")
    public String changeSharerShortLink(@RequestParam("sharer_short_link_name") String sharerShortLink) {
        return profileService.changeSharerShortLink(userDataService.getByIdMinData(SecurityUtils.getUser().getId()), sharerShortLink);
    }

    @RequestMapping(value = "/setting/set.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER')")
    public SuccessResponseDto setSetting(@RequestBody SettingDto settingDto) {
        userSettingsService.set(userDataService.getByIdMinData(SecurityUtils.getUser().getId()), settingDto.getKey(), settingDto.getValue());
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/delete_profile.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER')")
    public SuccessResponseDto deleteProfile() {
        profileService.deleteSharer(userDataService.getByIdMinData(SecurityUtils.getUser().getId()));
        return SuccessResponseDto.get();
    }

    @RequestMapping(value = "/close_session.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER')")
    public SuccessResponseDto closeSession(@RequestParam("session_id") String sessionId) {
        authService.closeSession(sessionId);
        return SuccessResponseDto.get();
    }
}
