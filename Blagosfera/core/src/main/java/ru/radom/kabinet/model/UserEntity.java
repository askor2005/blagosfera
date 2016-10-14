package ru.radom.kabinet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.springframework.util.StringUtils;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.askor.blagosfera.domain.user.SharerStatus;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.chat.DialogEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.invite.InvitationEntity;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityEntity;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.utils.FieldConstants;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "sharers")
public class UserEntity extends LongIdentifiable implements RadomAccount {

    public static final String DEFAULT_AVATAR_URL = "https://images.blagosfera.su/images/VGHF3HUFH5J/UYGEHYHQQN.png";

    // Мужское значение поля пол
    public static final String MALE_GENDER_STRING_VALUE = "Мужской";

    // Женское значение поля пол
    public static final String FEMALE_GENDER_STRING_VALUE = "Женский";

    /**
     * ИКП пользователя
     */
    @Column(nullable = false, unique = true, length = 20)
    private String ikp;

    /**
     * Статус пользователя: активирован не активирован
     */
    @JsonIgnore
    @Column
    private SharerStatus status;

    /**
     * код активации учетной записи
      */
    @JsonIgnore
    @Column(name = "activate_code")
    private String activateCode;

    /**
     * дата выдачи кода активации
     */
    @JsonIgnore
    @Column(name = "activate_code_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date activateCodeAt;

    /**
     * списки контактов, созданные пользователем
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @OrderBy("id")
    private List<ContactsGroupEntity> contactsGroups = new ArrayList<>();

    /**
     * все контакты пользователя
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @OrderBy("id")
    private List<ContactEntity> contacts = new ArrayList<>();

    /**
     * группа - мигрировано со старого проекта, у всех одно и то же значение
     */
    @JsonIgnore
    @JoinColumn(name = "group_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private SharersGroup group;

    /**
     * флаг является ли профиль профилем индивидуального предпинимателя,
     * устанавливается при регистрации, нигде не используется
     */
    @JsonIgnore
    @Column(name = "individual_entrepreneur")
    private boolean individualEntrepreneur;

    /**
     * пользователь, пригласивший данного пользователя
     */
    @JsonIgnore
    @JoinColumn(name = "inviter_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity inviter;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invitedUser")
    private List<InvitationEntity> invitations = new ArrayList<>();

    /**
     * адрес электронной почты
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * хэш пароля
     */
    @JsonIgnore
    @Column(nullable = false)
    private String password;


    @JsonIgnore
    @Column(name = "bcrypt_pass")
    private String bcryptPassword;

    /**
     * соль пароля
     */
    @JsonIgnore
    @Column
    private String salt;

    /**
     * строка для поиска пользователей, включает фио и емэйл
     */
    @JsonIgnore
    @Column(name = "search_string")
    @Type(type="text")
    private String searchString;

    /**
     * флаг является ли пользователь верифициронным (сертифицированным)
     */
    @Column
    private Boolean verified;

    /**
     * Если это поле не пусто, то всегда перенаправлять пользователя по url'у,
     * который хранится в этом поле (url записывается относительно контекстного пути)
     */
    @JsonIgnore
    @Column(name = "chroot_url", length = 255)
    private String chRootUrl;

    /**
     * список участников объединений, связанных с пользователем
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @OrderBy("id")
    private List<CommunityMemberEntity> members;

    /**
     * ссылка на аватар(обрезанное avatar_photo_src)
     */
    @JsonIgnore
    @Column(name = "avatar_src", length = 100, nullable = true)
    private String avatarSrc;

    /**
     * ссылка на оригинальное(не обрезанное) фото, из которого сделан avatar_src
     */
    @JsonIgnore
    @Column(name = "avatar_photo_src", length = 100, nullable = true)
    private String avatarPhotoSrc;

    /**
     * флаг разрешены ли параллельные сессии данному пользователю
     */
    @JsonIgnore
    @Column(name = "allow_multiple_sessions")
    private boolean allowMultipleSessions;

    /**
     * код для восстановления пароля
     */
    @JsonIgnore
    @Column(name = "password_recovery_code", length = 100)
    private String passwordRecoveryCode;

    /**
     * дата когда профиль стал заполненным менее чем на 70%
     */
    @JsonIgnore
    @Column(name = "profile_unfilled_at", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date profileUnfilledAt;

    /**
     * флаг перемещен ли профиль в архив, в архив переносятся недозаполненные
     * профили до удаления
     */
    @JsonIgnore
    @Column(nullable = true)
    private boolean archived;

    /**
     * флаг удален ли профиль
     */
    @JsonIgnore
    @Column(nullable = true)
    private boolean deleted;

    /**
     * список ролей
     */
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sharers_roles", joinColumns = { @JoinColumn(name = "sharer_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "role_id", nullable = false, updatable = false) })
    private List<Role> roles = new ArrayList<>();

    /**
     * дата регистрации
     */
    @Column(name = "registered_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;

    /**
     * пользователь, осуществивший сертификацию
     */
    @JsonIgnore
    @JoinColumn(name = "verifier_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private UserEntity verifier;

    /**
     * дата сертификации
     */
    @JsonIgnore
    @Column(name = "verification_date", nullable = true)
    private Date verificationDate;

    /**
     * номер карты, той которая записывается в процессе сертификации
     */
    @JsonIgnore
    @Column(name = "card_number", length = 100, nullable = true)
    private String cardNumber;

    /**
     * Список диалогов участника
     */
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinTable(name = "dialogs_sharers",
            joinColumns = {
                    @JoinColumn(name = "sharer_id", nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "dialog_id", nullable = false, updatable = false)})
    private Set<DialogEntity> dialogs = new HashSet<>();

    /**
     * Список диалогов участника, в которых он может читать историю
     */
    @JsonIgnore
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="sharers_visible_dialogs", joinColumns = @JoinColumn(name = "sharer_id"))
    @Column(name="dialog_id")
    private Set<Long> visibleDialogs = new HashSet<>();

    /**
     * todo comment me
     */
    @JsonIgnore
    @Column (name = "notified_about_unread", nullable = false)
    private boolean notifiedAboutUnreadMessages = false;

    /**
     * todo comment me
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = {}, orphanRemoval = true)
    private List<LetterOfAuthorityEntity> createdLettersOfAuthority;

    /**
     * todo comment me
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "delegate", cascade = {})
    private List<LetterOfAuthorityEntity> lettersOfAuthority;

    @Column(name = "logout_date")
    private Date logoutDate;

    /**
     * Поля пользователя, которые нужны для формирования необходимых данных
     */
    @JsonIgnore
    @OneToMany()
    @JoinColumn(name="object_id", insertable=false, updatable=false)
    @Where(clause="object_type='" + Discriminators.SHARER + "' and " +
            "field_id in (select f.id from fields as f where f.internal_name in (" +
            "'" + FieldConstants.SHARER_LASTNAME + "',"+ // Фамилия
            "'" + FieldConstants.SHARER_FIRSTNAME + "',"+ // Имя
            "'" + FieldConstants.SHARER_SECONDNAME + "',"+ // Отчество
            "'" + FieldConstants.SHARER_SHORT_LINK_NAME + "',"+ // Ссылка на учетку
            "'" + FieldConstants.SHARER_GENDER + "'"+ // Пол участника
            "))")
    private List<FieldValueEntity> fieldValues = new ArrayList<>();
    @JsonIgnore
    @OneToMany()
    @JoinColumn(name="object_id", insertable=false, updatable=false)
    @Where(clause="object_type='" + Discriminators.SHARER + "' and " +
            "field_id in (select f.id from fields as f where f.internal_name in (" +
            "'" + FieldConstants.FACT_COUNTRY_SHARER + "',"+
            "'" + FieldConstants.REGISTRATION_COUNTRY_SHARER + "',"+
            "'" + FieldConstants.REGISTRATOR_OFFICE_COUNTRY+ "'"+
            "))")
    private List<FieldValueEntity> fieldValuesCountry = new ArrayList<>();

    @JsonIgnore
    @OneToMany()
    @JoinColumn(name="object_id", insertable=false, updatable=false)
    @Where(clause="object_type='" + Discriminators.SHARER + "' and " +
            "field_id in (select f.id from fields as f where f.internal_name in (" +
            "'" + FieldConstants.REGISTRATION_CITY_SHARER + "',"+
            "'" + FieldConstants.REGISTRATOR_OFFICE_CITY + "',"+
            "'" + FieldConstants.FACT_CITY_SHARER+ "'"+
            "))")
    private Set<FieldValueEntity> fieldValuesCityAndRegion = new HashSet<>();
    @JsonIgnore
    @OneToMany()
    @JoinColumn(name="object_id", insertable=false, updatable=false)
    @Where(clause="object_type='" + Discriminators.SHARER + "' and " +
            "field_id in (select f.id from fields as f where f.internal_name in (" +
            "'" + FieldConstants.SHARER_BIRTHDAY + "'"+
            "))")
    private Set<FieldValueEntity> fieldValuesBirthday = new HashSet<>();
    @JsonIgnore
    @OneToMany()
    @JoinColumn(name="object_id", insertable=false, updatable=false)
    @Where(clause="object_type='" + Discriminators.SHARER + "' and " +
            "field_id in (select f.id from fields as f where f.internal_name in (" +
            "'" + FieldConstants.SHARER_GENDER + "'"+
            "))")
    private Set<FieldValueEntity> fieldValuesGender = new HashSet<>();

    public Set<FieldValueEntity> getFieldValuesGender() {
        return fieldValuesGender;
    }

    public void setFieldValuesGender(Set<FieldValueEntity> fieldValuesGender) {
        this.fieldValuesGender = fieldValuesGender;
    }

    public Set<FieldValueEntity> getFieldValuesBirthday() {
        return fieldValuesBirthday;
    }

    public void setFieldValuesBirthday(Set<FieldValueEntity> fieldValuesBirthday) {
        this.fieldValuesBirthday = fieldValuesBirthday;
    }

    public Set<FieldValueEntity> getFieldValuesCityAndRegion() {
        return fieldValuesCityAndRegion;
    }

    public void setFieldValuesCityAndRegion(Set<FieldValueEntity> fieldValuesCityAndRegion) {
        this.fieldValuesCityAndRegion = fieldValuesCityAndRegion;
    }

    public List<FieldValueEntity> getFieldValuesCountry() {
        return fieldValuesCountry;
    }

    public void setFieldValuesCountry(List<FieldValueEntity> fieldValuesCountry) {
        this.fieldValuesCountry = fieldValuesCountry;
    }

    public List<FieldValueEntity> getFieldValues() {
        return fieldValues;
    }

    public Date getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(Date logoutDate) {
        this.logoutDate = logoutDate;
    }

    public void setIkp(String ikp) {
        this.ikp = ikp;
    }

    public List<ContactsGroupEntity> getContactsGroups() {
        return contactsGroups;
    }

    public List<ContactEntity> getContacts() {
        return contacts;
    }

    public boolean isIndividualEntrepreneur() {
        return individualEntrepreneur;
    }

    public void setIndividualEntrepreneur(boolean individualEntrepreneur) {
        this.individualEntrepreneur = individualEntrepreneur;
    }

    /**
     * Возвращает true - если пол мужской
     * Возвращает false - если пол женский
     * @return
     */
    public boolean getSex() {
        String genderString = getFieldValueByFieldName(FieldConstants.SHARER_GENDER);
        return MALE_GENDER_STRING_VALUE.equals(genderString);
    }

    /**
     * Официальное обращение
     * @return
     */
    public String getOfficialAppeal() {
        return getSex() ? "Уважаемый" : "Уважаемая";
    }

    public SharersGroup getGroup() {
        return group;
    }

    public void setGroup(SharersGroup group) {
        this.group = group;
    }

    public UserEntity getInviter() {
        return inviter;
    }

    public void setInviter(UserEntity inviter) {
        this.inviter = inviter;
    }

    public List<InvitationEntity> getInvitations() {
        return invitations;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getActivateCode() {
        return activateCode;
    }

    public void setActivateCode(String activateCode) {
        this.activateCode = activateCode;
    }

    public Date getActivateCodeAt() {
        return activateCodeAt;
    }

    public void setActivateCodeAt(Date activateCodeAt) {
        this.activateCodeAt = activateCodeAt;
    }

    public SharerStatus getStatus() {
        return status;
    }

    public void setStatus(SharerStatus status) {
        this.status = status;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;

        if (verified) status = SharerStatus.CONFIRM;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public List<CommunityMemberEntity> getMembers() {
        return members;
    }

    public void setMembers(List<CommunityMemberEntity> members) {
        this.members = members;
    }

    public String getAvatarSrc() {
        return avatarSrc;
    }

    public void setAvatarSrc(String avatarSrc) {
        this.avatarSrc = avatarSrc;
    }

    public String getAvatarPhotoSrc() {
        return avatarPhotoSrc;
    }

    public void setAvatarPhotoSrc(String avatarPhotoSrc) {
        this.avatarPhotoSrc = avatarPhotoSrc;
    }

    public boolean isAllowMultipleSessions() {
        return allowMultipleSessions;
    }

    public void setAllowMultipleSessions(boolean allowMultipleSessions) {
        this.allowMultipleSessions = allowMultipleSessions;
    }

    public String getPasswordRecoveryCode() {
        return passwordRecoveryCode;
    }

    public void setPasswordRecoveryCode(String passwordRecoveryCode) {
        this.passwordRecoveryCode = passwordRecoveryCode;
    }

    public Date getProfileUnfilledAt() {
        return profileUnfilledAt;
    }

    public void setProfileUnfilledAt(Date profileUnfilledAt) {
        this.profileUnfilledAt = profileUnfilledAt;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }

    public UserEntity getVerifier() {
        return verifier;
    }

    public void setVerifier(UserEntity verifier) {
        this.verifier = verifier;
    }

    public Date getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(Date verificationDate) {
        this.verificationDate = verificationDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Set<DialogEntity> getDialogs() {
        return dialogs;
    }

    public Set<Long> getVisibleDialogs() {
        return visibleDialogs;
    }

    public boolean isNotifiedAboutUnreadMessages() {
        return notifiedAboutUnreadMessages;
    }

    public void setNotifiedAboutUnreadMessages(boolean notifiedAboutUnreadMessages) {
        this.notifiedAboutUnreadMessages = notifiedAboutUnreadMessages;
    }

    public List<LetterOfAuthorityEntity> getCreatedLettersOfAuthority() {
        return createdLettersOfAuthority;
    }

    public List<LetterOfAuthorityEntity> getLettersOfAuthority() {
        return lettersOfAuthority;
    }

    public String getChRootUrl() {
        return chRootUrl;
    }

    public void setChRootUrl(String chRootUrl) {
        this.chRootUrl = chRootUrl;
    }

    /**
     * Имя
     * @return
     */
    @JsonIgnore
    public String getFirstName() {
        return getFieldValueByFieldName(FieldConstants.SHARER_FIRSTNAME);
    }

    /**
     * Отчество
     * @return
     */
    @JsonIgnore
    public String getSecondName() {
        return getFieldValueByFieldName(FieldConstants.SHARER_SECONDNAME);
    }

    /**
     * Фамилия
     * @return
     */
    @JsonIgnore
    public String getLastName() {
        return getFieldValueByFieldName(FieldConstants.SHARER_LASTNAME);
    }

    /**
     * Сгенерировать имя
     * @param checkName
     * @param nameFormat
     * @return
     */
    private String generateName(String checkName, String nameFormat) {
        if (StringUtils.isEmpty(checkName)) {
            return (deleted ? "[ПРОФИЛЬ УДАЛЕН] " : "") + email;
        } else {
            return (deleted ? "[ПРОФИЛЬ УДАЛЕН] " : "") + nameFormat;
        }
    }

    /**
     * Фамилия И.О.
     * @return
     */
    public String getShortName() {
        String lastName = getLastName();
        String firstName = getFirstName();
        String secondName = getSecondName();
        String nameFormat = (StringUtils.hasLength(lastName) ? lastName : "") + (StringUtils.hasLength(firstName) ? (" " + firstName.charAt(0) + ". ") : "") + (StringUtils.hasLength(secondName) ? (secondName.charAt(0) + ".") : "");
        return generateName(lastName, nameFormat);
    }

    /**
     * Фамилия Имя
     * @return
     */
    public String getMediumName() {
        String lastName = getLastName();
        String firstName = getFirstName();
        String nameFormat = (StringUtils.hasLength(lastName) ? lastName : "") + (StringUtils.hasLength(firstName) ? (" " + firstName) : "");
        return generateName(lastName, nameFormat);
    }

    /**
     * Фамилия Имя Отчество
     * @return
     */
    public String getFullName() {
        String lastName = getLastName();
        String firstName = getFirstName();
        String secondName = getSecondName();
        String nameFormat = (StringUtils.hasLength(lastName) ? lastName : "") + (StringUtils.hasLength(firstName) ? (" " + firstName) : "") + (StringUtils.hasLength(secondName) ? (" " + secondName) : "");
        return generateName(lastName, nameFormat);
    }

    /**
     * Имя Отчество
     * @return
     */
    @JsonIgnore
    public String getOfficialName() {
        String firstName = getFirstName();
        String secondName = getSecondName();
        String nameFormat = (StringUtils.hasLength(firstName) ? firstName : "") + (StringUtils.hasLength(secondName) ? (" " + secondName) : "");
        return generateName(firstName, nameFormat);
    }

    /**
     * Ссылка на учетку участника
     * @return
     */
    @JsonIgnore
    public String getShortLink() {
        return getFieldValueByFieldName(FieldConstants.SHARER_SHORT_LINK_NAME);
    }

    //------------------------------------------------------------------------------------------------------------------
    // Реализация интерфейса RadomAccount
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public String getObjectType() {
        return Discriminators.SHARER;
    }

    @JsonIgnore
    @Override
    public String getName() {
        return getFullName();
    }

    @Override
    public String getAvatar() {
        return avatarSrc != null ? avatarSrc : DEFAULT_AVATAR_URL;
    }

    @Override
    public String getLink() {
        String shortLink = getFieldValueByFieldName(FieldConstants.SHARER_SHORT_LINK_NAME);
        if(!org.apache.commons.lang3.StringUtils.isBlank(shortLink)) {
            try {
                return "/sharer/" + URLEncoder.encode(shortLink,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return "/sharer/" + getIkp();
    }

    @Override
    public String getIkp() {
        return ikp;
    }
    //------------------------------------------------------------------------------------------------------------------

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public String getBcryptPassword() {
        return bcryptPassword;
    }

    public void setBcryptPassword(String bcryptPassword) {
        this.bcryptPassword = bcryptPassword;
    }

    /**
     * Получить значение поля по названию
     * @param name
     * @return
     */
    private String getFieldValueByFieldName(String name) {
        List<FieldValueEntity> values = getFieldValues();
        FieldValueEntity findValue = null;
        if (values != null) {
            for (FieldValueEntity fieldValue : values) {
                if (fieldValue.getField().getInternalName().equals(name)) {
                    findValue = fieldValue;
                    break;
                }
            }
        }
        return FieldsService.getFieldStringValue(findValue);
    }

    public User toDomain() {
        User user = new User();
        user.setStatus(getStatus());
        user.setId(getId());
        user.setEmail(getEmail());
        user.setFirstName(getFirstName());
        user.setSecondName(getSecondName());
        user.setLastName(getLastName());
        user.setDeleted(isDeleted());
        user.setArchived(isArchived());
        user.setLink(getLink());
        user.setAvatar(getAvatar());
        user.setAvatarSrc(getAvatarSrc());
        user.setIkp(getIkp());

        user.setVerified(BooleanUtils.toBooleanDefaultIfNull(getVerified(), false));
        user.setVerificationDate(getVerificationDate());
        user.setVerifier(getVerifier() == null ? null : getVerifier().getId());

        user.setCardNumber(getCardNumber());
        user.setAllowMultipleSessions(isAllowMultipleSessions());
        user.setSalt(getSalt());
        user.setSex(getSex());
        user.setPasswordRecoveryCode(getPasswordRecoveryCode());
        user.setRegisteredAt(getRegisteredAt());
        user.setInvitedBy(getInviter() == null ? null : getInviter().getId());
        user.setActivateCode(getActivateCode());
        user.setLastLogin(getLogoutDate());
        user.setProfileUnfilledAt(getProfileUnfilledAt());
        user.getRoles().addAll(Role.toDomainList(getRoles()));
        return user;
    }

    public static List<User> toDomainList(List<UserEntity> userEntities) {
        List<User> result = null;
        if (userEntities != null) {
            result = new ArrayList<>();
            for (UserEntity userEntity : userEntities) {
                result.add(userEntity.toDomain());
            }
        }
        return result;
    }
}