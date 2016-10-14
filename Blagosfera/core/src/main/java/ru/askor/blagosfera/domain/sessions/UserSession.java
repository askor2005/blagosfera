package ru.askor.blagosfera.domain.sessions;

import lombok.Data;
import ru.radom.kabinet.model.log.LoginType;
import ru.radom.kabinet.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by vtarasenko on 28.04.2016.
 */
@Data
public class UserSession {

    private String sessionId;
    private Date loginDate;
    private String ip;
    private String referer;
    private String useragent;
    private String hash;
    private String username;
    private LoginType loginType;
    private String device;
    private String os;
    private String browser;

    public UserSession() {
    }

    public UserSession(HttpServletRequest request, String username, LoginType loginType) {
        setSessionId(request.getSession().getId());
        setUsername(username);
        Date now = new Date();
        setLoginDate(now);
        setIp(WebUtils.getClientIpAddress(request));
        setUseragent(request.getHeader("user-agent"));
        setReferer(request.getParameter("referer"));
        setHash("");
        setLoginType(loginType);
    }

    public UserSession(String sessionId, Date loginDate, String ip, String referer, String useragent, String hash, String username, LoginType loginType, String device, String os, String browser) {
        this.sessionId = sessionId;
        this.loginDate = loginDate;
        this.ip = ip;
        this.referer = referer;
        this.useragent = useragent;
        this.hash = hash;
        this.username = username;
        this.loginType = loginType;
        this.device = device;
        this.os = os;
        this.browser = browser;
    }
}

