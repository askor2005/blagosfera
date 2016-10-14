package ru.askor.blagosfera.data.jpa.entities.community;

import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.log.CommunityVisitLog;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by vtarasenko on 14.07.2016.
 */
@Entity
@Table(name = "community_visit_logs")
public class CommunityVisitLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_visit_logs_id_generator")
    @SequenceGenerator(name = "community_visit_logs_id_generator", sequenceName = "community_visit_logs_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "community_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityEntity community;
    @JoinColumn(name = "user_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;
    @Column(name = "visit_time",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date visitTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CommunityEntity getCommunity() {
        return community;
    }

    public void setCommunity(CommunityEntity community) {
        this.community = community;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommunityVisitLogEntity)) return false;

        CommunityVisitLogEntity that = (CommunityVisitLogEntity) o;

        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
    public CommunityVisitLog toDomain() {
        CommunityVisitLog communityVisitLog = new CommunityVisitLog();
        communityVisitLog.setCommunity(getCommunity().toDomain());
        communityVisitLog.setUser(getUser().toDomain());
        communityVisitLog.setVisitTime(getVisitTime());
        communityVisitLog.setId(getId());
        return communityVisitLog;
    }
}
