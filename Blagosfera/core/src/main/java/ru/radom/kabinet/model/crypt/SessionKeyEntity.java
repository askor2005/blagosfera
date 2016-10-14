package ru.radom.kabinet.model.crypt;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Maxim Nikitin on 08.02.2016.
 */
@Entity
@Table(name = "session_keys")
public class SessionKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "session_keys_id_generator")
    @SequenceGenerator(name = "session_keys_id_generator", sequenceName = "session_keys_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "end_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(name = "session_key")
    private String key;

    public SessionKeyEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionKeyEntity)) return false;

        SessionKeyEntity that = (SessionKeyEntity) o;

        //return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
