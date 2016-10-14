package ru.radom.kabinet.model.bio;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "finger_tokens")
public class FingerToken extends LongIdentifiable {

    @JoinColumn(name = "sharer_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @Column(nullable = false)
    private TokenStatus status;

    @Column(name = "init_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date initDate;

    @Column(name = "get_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date getDate;

    @Column(nullable = true, length = 256)
    private String value;

    @Column(nullable = false, length = 256)
    private String requestId;

    @Column(nullable = false, length = 15)
    private String ip;

    @Column(name = "finger", nullable = false)
    private Integer finger;

    @Column(name = "sms_code")
    private String smsCode;

    public FingerToken() {
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public TokenStatus getStatus() {
        return status;
    }

    public void setStatus(TokenStatus status) {
        this.status = status;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    public Date getGetDate() {
        return getDate;
    }

    public void setGetDate(Date getDate) {
        this.getDate = getDate;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getFinger() {
        return finger;
    }

    public void setFinger(Integer finger) {
        this.finger = finger;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }
}
