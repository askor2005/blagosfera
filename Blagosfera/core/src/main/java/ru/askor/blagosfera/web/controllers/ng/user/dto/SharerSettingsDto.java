package ru.askor.blagosfera.web.controllers.ng.user.dto;

import ru.askor.blagosfera.domain.sessions.UserSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vtarasenko on 21.04.2016.
 */
public class SharerSettingsDto {

    public List<UserSession> userSessions = new ArrayList<>();
    public List<Long> currentShowEmailListId = new ArrayList<>();
    public String currentSessionId;
    public String currentShowEmailMode;
    public List<String> timezones = new ArrayList<>();
    public Map<String, String> timezoneOffsets = new HashMap<>();
    public String currentTimezone;
    public String sharerShortLink;
    public boolean allowMultipleSessions;
    public boolean interfaceTooltipEnable;
    public long interfaceTooltipDelayShow;
    public long interfaceTooltipDelayHide;
    public String identificationMode;
    public PhoneVerifyDto phoneVerify;
}
