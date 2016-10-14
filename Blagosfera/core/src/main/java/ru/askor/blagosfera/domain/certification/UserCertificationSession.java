package ru.askor.blagosfera.domain.certification;

import java.util.Date;

/**
 * Created by Maxim Nikitin on 02.02.2016.
 */
public class UserCertificationSession {

    private Long id;
    private Long registratorId;
    private Long userId;
    private Date startDate;
    private Date endDate;
    private String sessionId;
    private boolean success;
    private boolean userAgreed;

    public UserCertificationSession() {
    }

    public UserCertificationSession(Long id, Long registratorId, Long userId, Date startDate, Date endDate,
                                    String sessionId, boolean success, boolean userAgreed) {
        this.id = id;
        this.registratorId = registratorId;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sessionId = sessionId;
        this.success = success;
        this.userAgreed = userAgreed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRegistratorId() {
        return registratorId;
    }

    public void setRegistratorId(Long registratorId) {
        this.registratorId = registratorId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public boolean isUserAgreed() {
        return userAgreed;
    }

    public void setUserAgreed(boolean userAgreed) {
        this.userAgreed = userAgreed;
    }
}
