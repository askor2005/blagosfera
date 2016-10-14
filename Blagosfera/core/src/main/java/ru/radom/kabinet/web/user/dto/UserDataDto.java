package ru.radom.kabinet.web.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.json.TimeStampDateSerializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 22.03.2016.
 */
public class UserDataDto {

    public Long id;
    public String email;
    public String firstName;
    public String shortName;
    public String secondName;
    public String lastName;
    public String fullName;
    public String officialName;
    public boolean deleted;
    public String link;
    public String avatar;
    public String ikp;
    public boolean verified;
    public boolean online;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    public Date verificationDate;
    public boolean sex;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    public Date lastLogin;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    public Date registeredAt;

    public String identificationMode;
    public boolean identificationRequired = true;

    public List<UserRoleDto> roles = new ArrayList<>();

    public UserDataDto(User user) {
        id = user.getId();
        email = user.getEmail();
        firstName = user.getFirstName();
        secondName = user.getSecondName();
        lastName = user.getLastName();
        fullName = user.getFullName();
        officialName = user.getOfficialName();
        shortName = user.getShortName();
        deleted = user.isDeleted();
        link = user.getLink();
        avatar = user.getAvatar();
        ikp = user.getIkp();
        verified = user.isVerified();
        verificationDate = user.getVerificationDate();
        sex = user.isSex();
        lastLogin = user.getLastLogin();
        registeredAt = user.getRegisteredAt();
        roles.addAll(UserRoleDto.toDtoList(user.getRoles()));
    }

    public static List<UserDataDto> toDtoList(List<User> users) {
        List<UserDataDto> result = null;
        if (users != null) {
            result = new ArrayList<>();
            for (User user : users) {
                result.add(new UserDataDto(user));
            }
        }
        return result;
    }
}
