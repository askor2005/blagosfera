package ru.radom.kabinet.model;

import ru.askor.blagosfera.domain.certification.UserCertificationSession;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_certification_sessions")
public class UserCertificationSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_certification_sessions_id_generator")
    @SequenceGenerator(name = "user_certification_sessions_id_generator", sequenceName = "user_certification_sessions_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "registrator_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity registrator;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "user_agreed", nullable = false)
    private boolean userAgreed;

    public UserCertificationSessionEntity() {
    }

    public UserCertificationSession toDomain() {
        return new UserCertificationSession(getId(), getRegistrator().getId(), getUser().getId(),
                getStartDate(), getEndDate(), getSessionId(), isSuccess(), isUserAgreed());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCertificationSessionEntity)) return false;

        UserCertificationSessionEntity that = (UserCertificationSessionEntity) o;

        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getRegistrator() {
        return registrator;
    }

    public void setRegistrator(UserEntity registrator) {
        this.registrator = registrator;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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
