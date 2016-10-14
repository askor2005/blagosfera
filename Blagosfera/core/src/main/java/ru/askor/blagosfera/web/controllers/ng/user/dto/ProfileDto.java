package ru.askor.blagosfera.web.controllers.ng.user.dto;

import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.askor.blagosfera.domain.contacts.ContactGroup;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.registration.request.RegistrationRequestDomain;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.services.PassportCitizenshipFieldsSettings;
import ru.radom.kabinet.services.PassportCitizenshipSettings;
import ru.radom.kabinet.utils.FieldConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Maxim Nikitin on 01.04.2016.
 */
public class ProfileDto {

    public long id;
    public String email;
    public String ikp;
    public String avatar;
    public String gender;
    public String firstName;
    public String middleName;
    public String lastName;
    public String shortName;
    public String fullName;
    public boolean verified;
    public boolean superadmin;
    public Date verificationDate;
    public String verifierIkp;
    public String verifierGender;
    public String verifierFirstName;
    public String verifierMiddleName;
    public String verifierLastName;
    public String signature;
    public boolean isCurrentUser;
    public List<FieldDto> fields = new ArrayList<>();
    public Long invitedBy;
    public Long verifiedBy;
    public Date registrationDate;
    public Integer registratorLevel;
    public List<PassportCitizenshipFieldsSettings> passportCitizenshipSettings;
    public int profileFillingPercent;
    public RegistrationRequestDomain registrationRequest;

    // бывшие поля

    public String dateOfBirth; // BIRTHDAY
    public String placeOfBirth; // BIRTHPLACE
    public String nationality; // NATIONALITY
    public String nativeLanguage; // LANGUAGE

    public String homePhone; // HOME_TEL
    public String mobilePhone; // MOB_TEL
    public String skype; // SKYPE
    public String www; // WWW

    public String citizenship; // PERSON_CITIZENSHIP
    public String inn; // PERSON_INN
    public String snils; // SNILS
    public String passportSeries; // PASSPORT_SERIAL
    public String passportNumber; // PASSPORT_NUMBER
    public String passportIssueDate; // PASSPORT_DATE
    public String passportIssuedBy; // PASSPORT_DEALER
    public String passportIssuerCode; // PASSPORT_DIVISION;
    public String personSignature; // PERSON_SYSTEM_SIGNATURE
    public String registratorOfficePhone;
    public String mobileRegistratorPhone;
    public String registratorSkype;
    public String registratorWww;
    public String registratorAdditionalService;
    public String timetable;
    public String passportExpirationDate;
    public String passportExpiredDate;
    public String identicalNumber;
    public String kzIdenticalNumber;
    public List<Community> communitiesMember;
    public List<Community> communitiesCreated;
    public Long verifiedCommunitiesCount;
    public Long verifiedUsersCount;
    public Long verifiedRegistratorsCount;
    public Long countInvitedRegistered;
    public Long countInvitedVerified;
    public Long countInvitedReg1Level;
    public Long countInvitedReg2Level;
    public Long countInvitedReg3Level;
    public Contact contact;
    public List<ContactGroup> contactGroups;
    public Map<String, Boolean> basicInfoVisibilityMap;
    public Map<String, Boolean> factAddressVisibilityMap;
    public Map<String, Boolean> regAddressVisibilityMap;
    public Map<String, Boolean> registratorOfficeAddressVisibilityMap;
    public Map<String, Boolean> registratorDataAddressVisibilityMap;

    public ProfileDto() {
    }

    public ProfileDto(User user,RegistrationRequestDomain registrationRequest,Long viewerId) {
        registrationRequest = registrationRequest;
        id = user.getId();
        email = user.getEmail();
        ikp = user.getIkp();
        avatar = user.getAvatar();

        if (user.isSex()) {
            gender = "Мужской";
        } else {
            gender = "Женский";//TODO пока в целях совместимости со старым клиентом
        }

        firstName = user.getFirstName();
        middleName = user.getSecondName();
        lastName = user.getLastName();
        shortName = user.getShortName();
        fullName = user.getFullName();
        verified = user.isVerified();
        verificationDate = user.getVerificationDate();
        verifiedBy = user.getVerifier();

        for (Field field: user.getFields()) {
            if ((field.isHideable()) && (field.isHidden()) && (!user.getId().equals(viewerId))&& ((registrationRequest == null) || (registrationRequest.getRegistrator().getId() != viewerId))){
                field.setValue("");
            }
            fields.add(new FieldDto(field));

            if (field.getInternalName().equals("BIRTHDAY")) {
                dateOfBirth = field.getValue();
            }

            if (field.getInternalName().equals("BIRTHPLACE")) {
                placeOfBirth = field.getValue();
            }

            if (field.getInternalName().equals("NATIONALITY")) {
                nationality = field.getValue();
            }

            if (field.getInternalName().equals("LANGUAGE")) {
                nativeLanguage = field.getValue();
            }

            /////////

            if (field.getInternalName().equals("HOME_TEL")) {
                homePhone = field.getValue();
            }

            if (field.getInternalName().equals("MOB_TEL")) {
                mobilePhone = field.getValue();
            }

            if (field.getInternalName().equals("SKYPE")) {
                skype = field.getValue();
            }

            if (field.getInternalName().equals("WWW")) {
                www = field.getValue();
            }

            /////////

            if (field.getInternalName().equals("PERSON_CITIZENSHIP")) {
                citizenship = field.getValue();
            }

            if (field.getInternalName().equals("PERSON_INN")) {
                inn = field.getValue();
            }

            if (field.getInternalName().equals("SNILS")) {
                snils = field.getValue();
            }

            if (field.getInternalName().equals("PASSPORT_SERIAL")) {
                passportSeries = field.getValue();
            }

            if (field.getInternalName().equals("PASSPORT_NUMBER")) {
                passportNumber = field.getValue();
            }

            if (field.getInternalName().equals("PASSPORT_DATE")) {
                passportIssueDate = field.getValue();
            }

            if (field.getInternalName().equals("PASSPORT_DEALER")) {
                passportIssuedBy = field.getValue();
            }

            if (field.getInternalName().equals("PASSPORT_DIVISION")) {
                passportIssuerCode = field.getValue();
            }
            if (field.getInternalName().equals(FieldConstants.SHARER_REGISTRATOR_OFFICE_PHONE)) {
                registratorOfficePhone = field.getValue();
            }
            if (field.getInternalName().equals(FieldConstants.SHARER_REGISTRATOR_MOBILE_PHONE)) {
                mobileRegistratorPhone = field.getValue();
            }
            if (field.getInternalName().equals(FieldConstants.REGISTRATOR_SKYPE)) {
                registratorSkype = field.getValue();
            }
            if (field.getInternalName().equals("REGISTRATOR_WEBSITE")) {
                registratorWww = field.getValue();
            }
            if (field.getInternalName().equals("REGISTRATOR_ADDITIONAL_SERVICES_DESCRIPTION")) {
                registratorAdditionalService = field.getValue();
            }
            if (field.getInternalName().equals(FieldConstants.SHARER_REGISTRATOR_OFFICE_TIMETABLE)) {
                timetable = field.getValue();
            }
            if (field.getInternalName().equals("PASSPORT_EXPIRATION_DATE")) {
                passportExpirationDate = field.getValue();
            }
            if (field.getInternalName().equals("EXPIRED_PASSPORT_DATE")) {
                passportExpiredDate = field.getValue();
            }
            if (field.getInternalName().equals("BY_IDENTIFICATION_NUMBER")) {
                identicalNumber = field.getValue();
            }
            if (field.getInternalName().equals("KZ_INDIVIDUAL_IDENTIFICATION_NUMBER")) {
                kzIdenticalNumber = field.getValue();
            }
            if (field.getInternalName().equals(FieldConstants.PERSON_SYSTEM_SIGNATURE_FIELD_NAME)) {
                if ((field.getFieldFiles() != null) && (field.getFieldFiles().size() > 0)) {
                    signature = field.getFieldFiles().get(0).getUrl();
                }
            }
        }

        invitedBy = user.getInvitedBy();
        registrationDate = user.getRegisteredAt();
    }

    public void setCommunitiesMember(List<Community> communitiesMember) {
        this.communitiesMember = communitiesMember;
    }

    public List<Community> getCommunitiesMember() {
        return communitiesMember;
    }

    public void setCommunitiesCreated(List<Community> communitiesCreated) {
        this.communitiesCreated = communitiesCreated;
    }

    public List<Community> getCommunitiesCreated() {
        return communitiesCreated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getVerifiedRegistratorsCount() {
        return verifiedRegistratorsCount;
    }

    public void setVerifiedRegistratorsCount(Long verifiedRegistratorsCount) {
        this.verifiedRegistratorsCount = verifiedRegistratorsCount;
    }

    public Long getVerifiedUsersCount() {
        return verifiedUsersCount;
    }

    public void setVerifiedUsersCount(Long verifiedUsersCount) {
        this.verifiedUsersCount = verifiedUsersCount;
    }

    public Long getVerifiedCommunitiesCount() {
        return verifiedCommunitiesCount;
    }

    public void setVerifiedCommunitiesCount(Long verifiedCommunitiesCount) {
        this.verifiedCommunitiesCount = verifiedCommunitiesCount;
    }
}
