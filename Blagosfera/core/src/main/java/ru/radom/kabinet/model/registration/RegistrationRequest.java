package ru.radom.kabinet.model.registration;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;
import ru.askor.blagosfera.domain.registration.request.RegistrationRequestDomain;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "registration_requests")
public class RegistrationRequest extends LongIdentifiable {


    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column
    private RegistrationRequestStatus status;

    @Any(metaColumn = @Column(name = "object_type", length = 50), fetch = FetchType.LAZY)
    @AnyMetaDef(idType = "long", metaType = "string", metaValues = {
            @MetaValue(targetEntity = UserEntity.class, value = Discriminators.SHARER),
            @MetaValue(targetEntity = CommunityEntity.class, value = Discriminators.COMMUNITY)})
    @JoinColumn(name = "object_id")
    private LongIdentifiable object;

    //@JoinColumn(name = "user_id", nullable = false)
    //@ManyToOne(fetch = FetchType.LAZY, optional = false)
    //private Sharer user;


    @JoinColumn(name = "registrator_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity registrator;

    @Column(length = 1000)
    private String comment;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public RegistrationRequest() {
    }

    public RegistrationRequest(LongIdentifiable object, UserEntity registrator) {
        this.created = new Date();
        this.status = RegistrationRequestStatus.NEW;
        this.object = object;
        this.registrator = registrator;
    }

    public Date getCreated() {
        return created;
    }

    public RegistrationRequestStatus getStatus() {
        return status;
    }

    public LongIdentifiable getObject() {
        return object;
    }

    public UserEntity getRegistrator() {
        return registrator;
    }

    public String getComment() {
        return comment;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setStatus(RegistrationRequestStatus status) {
        this.status = status;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    public RegistrationRequestDomain toDomain() {
        RegistrationRequestDomain registrationRequestDomain = new RegistrationRequestDomain();
        registrationRequestDomain.setComment(comment);
        registrationRequestDomain.setCreated(created);
        registrationRequestDomain.setId(getId());
        registrationRequestDomain.setRegistrator(registrator.toDomain());
        return registrationRequestDomain;
    }
}
