package ru.askor.blagosfera.domain.user;

import org.springframework.util.StringUtils;
import ru.askor.blagosfera.domain.Address;
import ru.askor.blagosfera.domain.Verifiable;
import ru.askor.blagosfera.domain.document.DocumentCreator;
import ru.askor.blagosfera.domain.document.IDocumentParticipant;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.IFieldOwner;
import ru.askor.blagosfera.domain.loa.LetterOfAuthorityScope;
import ru.askor.blagosfera.domain.notification.NotificationSender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * класс пользователя
 */
public class User implements Serializable, IDocumentParticipant, Verifiable, LetterOfAuthorityScope, IFieldOwner, DocumentCreator, NotificationSender {

    public static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String firstName;
    private String secondName;
    private String lastName;
    private boolean deleted;
    private boolean archived;
    private String link;
    private String avatarSrc;
    private String avatar;
    private String ikp;
    private boolean isVerified;
    private Date verificationDate;
    private Long verifier;
    private boolean sex;
    private String cardNumber;
    private String passwordRecoveryCode;
    private Date registeredAt;
    private Date lastLogin;
    private String activateCode;
    private Date profileUnfilledAt;

    @Deprecated
    private String salt;

    private SharerStatus status;
    private boolean allowMultipleSessions;

    //Список полей пользователя
    private List<Field> fields = new ArrayList<>();

    //Фактический адрес
    private Address actualAddress;

    //Адрес регистрации
    private Address registrationAddress;

    //адрес офиса
    private Address officeAddress;

    private Long invitedBy;

    // Пользователь, который пригласил в систему
    private User inviter;

    private List<UserRole> roles = new ArrayList<>();

    public User() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (getId() != null ? !getId().equals(user.getId()) : user.getId() != null) return false;
        if (!getEmail().equals(user.getEmail())) return false;
        return getIkp().equals(user.getIkp());

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        if (getEmail() != null) {
            result = 31 * result + getEmail().hashCode();
        }
        if (getIkp() != null) {
            result = 31 * result + getIkp().hashCode();
        }
        return result;
    }

    public Field getFieldByInternalName(String internalName) {
        Field result = null;
        if (fields != null) {
            for (Field field : fields) {
                if (field.getInternalName().equals(internalName)) {
                    result = field;
                    break;
                }
            }
        }
        return result;
    }

    public String getFieldValueByInternalName(String internalName) {
        Field fieldDomain = getFieldByInternalName(internalName);
        String value = null;
        if (fieldDomain != null) {
            value = fieldDomain.getValue();
        }
        return value;
    }

    /**
     * Фамилия И.О.
     * @return
     */
    public String getShortName() {
        String nameFormat = (StringUtils.hasLength(getLastName()) ? getLastName() : "") + (StringUtils.hasLength(getFirstName()) ? (" " + getFirstName().charAt(0) + ". ") : "") + (StringUtils.hasLength(getSecondName()) ? (getSecondName().charAt(0) + ".") : "");
        return generateName(getLastName(), nameFormat);
    }

    /**
     * Фамилия Имя
     * @return
     */

    public String getMediumName() {
        String nameFormat = (StringUtils.hasLength(getLastName()) ? getLastName() : "") + (StringUtils.hasLength(getFirstName()) ? (" " + getFirstName()) : "");
        return generateName(getLastName(), nameFormat);
    }

    /**
     * Фамилия Имя Отчество
     * @return
     */

    public String getFullName() {
        String nameFormat = (StringUtils.hasLength(getLastName()) ? getLastName() : "") + (StringUtils.hasLength(getFirstName()) ? (" " + getFirstName()) : "") + (StringUtils.hasLength(getSecondName()) ? (" " + getSecondName()) : "");
        return generateName(getLastName(), nameFormat);
    }

    public String getName() {
        return getFullName();
    }

    /**
     * Имя Отчество
     * @return
     */
    public String getOfficialName() {
        String nameFormat = (StringUtils.hasLength(getFirstName()) ? getFirstName() : "") + (StringUtils.hasLength(getSecondName()) ? (" " + getSecondName()) : "");
        return generateName(getFirstName(), nameFormat);
    }

    /**
     * Сгенерировать имя
     * @param checkName
     * @param nameFormat
     * @return
     */
    private String generateName(String checkName, String nameFormat) {
        if (StringUtils.isEmpty(checkName)) {
            return (isDeleted() ? "[ПРОФИЛЬ УДАЛЕН] " : "") + getEmail();
        } else {
            return (isDeleted() ? "[ПРОФИЛЬ УДАЛЕН] " : "") + nameFormat;
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAvatarSrc() {
        return avatarSrc;
    }

    public void setAvatarSrc(String avatarSrc) {
        this.avatarSrc = avatarSrc;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIkp() {
        return ikp;
    }

    public void setIkp(String ikp) {
        this.ikp = ikp;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public Date getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(Date verificationDate) {
        this.verificationDate = verificationDate;
    }

    public Long getVerifier() {
        return verifier;
    }

    public void setVerifier(Long verifier) {
        this.verifier = verifier;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Date getProfileUnfilledAt() {
        return profileUnfilledAt;
    }

    public void setProfileUnfilledAt(Date profileUnfilledAt) {
        this.profileUnfilledAt = profileUnfilledAt;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean isAllowMultipleSessions() {
        return allowMultipleSessions;
    }

    public void setAllowMultipleSessions(boolean allowMultipleSessions) {
        this.allowMultipleSessions = allowMultipleSessions;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Address getActualAddress() {
        return actualAddress;
    }

    public void setActualAddress(Address actualAddress) {
        this.actualAddress = actualAddress;
    }

    public Address getRegistrationAddress() {
        return registrationAddress;
    }

    public void setRegistrationAddress(Address registrationAddress) {
        this.registrationAddress = registrationAddress;
    }

    public Address getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(Address officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getPasswordRecoveryCode() {
        return passwordRecoveryCode;
    }

    public void setPasswordRecoveryCode(String passwordRecoveryCode) {
        this.passwordRecoveryCode = passwordRecoveryCode;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public SharerStatus getStatus() {
        return status;
    }

    public void setStatus(SharerStatus status) {
        this.status = status;
    }

    public Long getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(Long invitedBy) {
        this.invitedBy = invitedBy;
    }

    public String getActivateCode() {
        return activateCode;
    }

    public void setActivateCode(String activateCode) {
        this.activateCode = activateCode;
    }

    public User getInviter() {
        return inviter;
    }

    public void setInviter(User inviter) {
        this.inviter = inviter;
    }

    public List<UserRole> getRoles() {
        return roles;
    }
}


