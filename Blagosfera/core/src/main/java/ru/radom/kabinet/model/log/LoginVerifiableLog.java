package ru.radom.kabinet.model.log;

import ru.askor.blagosfera.domain.sessions.UserSession;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by ebelyaev on 12.08.2015.
 */
@MappedSuperclass
public class LoginVerifiableLog extends VerifiableLog {
    @Column(name = "login_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date loginDate;

    @Column(name = "ip", length = 15)
    private String ip;

    @Column(name = "referer", length = 1000)
    private String referer;

    @Column(name = "useragent",length = 200)
    private String useragent;

    @Column(name = "session_id", length = 1000)
    private String sessionId;

    @Column(name = "success")
    private boolean success;

    @Column(name = "username",length = 1000)
    private String username;

    @Column(name = "login_type")
    private LoginType loginType;

    public LoginVerifiableLog() {
    }

    public LoginVerifiableLog(Date loginDate, String ip, String referer, String useragent, String sessionId, boolean success, String username, LoginType loginType) {
        this.loginDate = loginDate;
        this.ip = ip;
        this.referer = referer;
        this.useragent = useragent;
        this.sessionId = sessionId;
        this.success = success;
        this.username = username;
        this.loginType = loginType;
    }

    public LoginVerifiableLog(UserSession userSession) {
        this(userSession.getLoginDate(), userSession.getIp(),userSession.getReferer(),userSession.getUseragent(),userSession.getSessionId(),true,userSession.getUsername(),userSession.getLoginType());
    }

    @Override
    public String getStringFromFields() {
        return loginDate.getTime() + ip + referer + useragent + sessionId + success + username + loginType;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }
}
