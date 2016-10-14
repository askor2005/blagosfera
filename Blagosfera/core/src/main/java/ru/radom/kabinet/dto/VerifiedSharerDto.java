package ru.radom.kabinet.dto;

/**
 * Created by ebelyaev on 11.11.2015.
 */
public class VerifiedSharerDto {
    private Long id;

    private String avatarUrlSrc;

    private String email;
    private String name;

    private Long inviterId;

    private String registrationDate;
    private String inviterName;

    private String verificationDate;
    private String verificationType;

    private Integer memberWithOrganizationCount;
    private Integer memberWithoutOrganizationCount;

    private Integer creatorWithOrganizationCount;
    private Integer creatorWithoutOrganizationCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatarUrlSrc() {
        return avatarUrlSrc;
    }

    public void setAvatarUrlSrc(String avatarUrlSrc) {
        this.avatarUrlSrc = avatarUrlSrc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getInviterId() {
        return inviterId;
    }

    public void setInviterId(Long inviterId) {
        this.inviterId = inviterId;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getInviterName() {
        return inviterName;
    }

    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public String getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(String verificationDate) {
        this.verificationDate = verificationDate;
    }

    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }

    public Integer getMemberWithOrganizationCount() {
        return memberWithOrganizationCount;
    }

    public void setMemberWithOrganizationCount(Integer memberWithOrganizationCount) {
        this.memberWithOrganizationCount = memberWithOrganizationCount;
    }

    public Integer getMemberWithoutOrganizationCount() {
        return memberWithoutOrganizationCount;
    }

    public void setMemberWithoutOrganizationCount(Integer memberWithoutOrganizationCount) {
        this.memberWithoutOrganizationCount = memberWithoutOrganizationCount;
    }

    public Integer getCreatorWithOrganizationCount() {
        return creatorWithOrganizationCount;
    }

    public void setCreatorWithOrganizationCount(Integer creatorWithOrganizationCount) {
        this.creatorWithOrganizationCount = creatorWithOrganizationCount;
    }

    public Integer getCreatorWithoutOrganizationCount() {
        return creatorWithoutOrganizationCount;
    }

    public void setCreatorWithoutOrganizationCount(Integer creatorWithoutOrganizationCount) {
        this.creatorWithoutOrganizationCount = creatorWithoutOrganizationCount;
    }
}
