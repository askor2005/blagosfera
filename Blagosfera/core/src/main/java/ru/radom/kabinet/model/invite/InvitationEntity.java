package ru.radom.kabinet.model.invite;

import ru.askor.blagosfera.domain.invite.Invitation;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Модель приглашенного пользователя в систему
 */
@Entity
@Table(name = "invites")
public class InvitationEntity extends LongIdentifiable {

    //дата создания записи
    @Column(name = "creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    //дата истечения приглашения
    @Column(name = "expire_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireDate;

    //пользователь добавивший приглашение
    @JoinColumn(name = "sharer_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    // адрес электронной почты приглашенного
    @Column(name = "invited_email")
    private String email;

    //фамилия приглашенного
    @Column(name = "invited_last_name")
    private String invitedLastName;

    //имя приглашенного
    @Column(name = "invited_first_name")
    private String invitedFirstName;

    //отчество приглашенного
    @Column(name = "invited_father_name")
    private String invitedFatherName;

    //пол приглашенного (М / Ж)
    @Column(name = "invited_gender", length = 1)
    private String invitedGender;

    //признак ручаюсь за него или не ручаюсь
    @Column(name = "guarantee")
    private Boolean guarantee;

    //сколько лет знаком
    @Column(name = "how_long_familiar")
    private Integer howLongFamiliar;

    //хеш URL используется для приглашения в письме
    @Column(name = "hash_url")
    private String hashUrl;

    //было ли принято приглашение
    // 0 - еще не принято
    // 1 - принято
    // 2 - отклонено
    @Column(name = "status")
    private Integer status;

    //дата последней отправки приглашения на почту
    @Column(name = "last_sending")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastDateSending;

    //ссылкка на созданного пользователя если пользователь принял приглашение
    @JoinColumn(name = "invited_sharer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity invitedUser;

    //отношения (кто он для вас)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "invites_relationships", joinColumns = {@JoinColumn(name = "invite_id", nullable = false, updatable = false)}, inverseJoinColumns = {@JoinColumn(name = "invite_relationship_type_id", nullable = false, updatable = false)})
    private List<InviteRelationshipType> relationships;

    /**
     * сколько раз этому участнику было отправлено приглашение
     */
    @Column(name = "invites_count")
    private Integer invitesCount;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInvitedLastName() {
        return invitedLastName;
    }

    public void setInvitedLastName(String invitedLastName) {
        this.invitedLastName = invitedLastName;
    }

    public String getInvitedFirstName() {
        return invitedFirstName;
    }

    public void setInvitedFirstName(String invitedFirstName) {
        this.invitedFirstName = invitedFirstName;
    }

    public String getInvitedFatherName() {
        return invitedFatherName;
    }

    public void setInvitedFatherName(String invitedFatherName) {
        this.invitedFatherName = invitedFatherName;
    }

    public String getInvitedGender() {
        return invitedGender;
    }

    public void setInvitedGender(String invitedGender) {
        this.invitedGender = invitedGender;
    }

    public Boolean getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(Boolean guarantee) {
        this.guarantee = guarantee;
    }

    public Integer getHowLongFamiliar() {
        return howLongFamiliar;
    }

    public void setHowLongFamiliar(Integer howLongFamiliar) {
        this.howLongFamiliar = howLongFamiliar;
    }

    public String getHashUrl() {
        return hashUrl;
    }

    public void setHashUrl(String hashUrl) {
        this.hashUrl = hashUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getLastDateSending() {
        return lastDateSending;
    }

    public void setLastDateSending(Date lastDateSending) {
        this.lastDateSending = lastDateSending;
    }

    public UserEntity getInvitedSharer() {
        return invitedUser;
    }

    public void setInvitedSharer(UserEntity invitedUserEntity) {
        this.invitedUser = invitedUserEntity;
    }

    public List<InviteRelationshipType> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<InviteRelationshipType> relationships) {
        this.relationships = relationships;
    }

    public Integer getInvitesCount() {
        return invitesCount;
    }

    public void setInvitesCount(Integer invitesCount) {
        this.invitesCount = invitesCount;
    }

    public Invitation toDomain(){
        return new Invitation(getId(),getCreationDate(),getExpireDate(),getUser().toDomain(),getEmail(),getInvitedLastName(),
                getInvitedFirstName(),getInvitedFatherName(),getInvitedGender(),getGuarantee(),getHowLongFamiliar(),
                getHashUrl(),getStatus(),getLastDateSending(),getInvitedSharer() != null ? getInvitedSharer().toDomain() : null,getRelationships().stream().map(relationship -> relationship.toDomain()).collect(Collectors.toList()),getInvitesCount());
    }
}