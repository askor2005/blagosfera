package ru.askor.blagosfera.data.redis.entities;

import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import ru.askor.blagosfera.domain.sessions.UserSession;
import ru.radom.kabinet.model.log.LoginType;
import ru.radom.kabinet.utils.UserAgentUtils;

import java.util.Date;

/**
 * Created by vtarasenko on 28.04.2016.
 */
@EqualsAndHashCode
@RedisHash("userSession")
public class UserSessionEntity {

    @Id
    String sessionId;
    Date loginDate;
    String ip;
    String referer;
    String useragent;
    String hash;

    @Indexed
    String username;
    LoginType loginType;
    String device;
    String os;
    String browser;

    public UserSession toDomain() {
        return new UserSession(getSessionId(), getLoginDate(), getIp(), getReferer(), getUseragent(), getHash(), getUsername(), getLoginType(), getDevice(), getOs(), getBrowser());
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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
        setDevice(UserAgentUtils.getDevice(useragent));
        setOs(UserAgentUtils.getOs(useragent));
        setBrowser(UserAgentUtils.getBrowser(useragent));
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public UserSessionEntity() {
    }

}
