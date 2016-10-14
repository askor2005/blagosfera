package ru.askor.blagosfera.domain.invite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.askor.blagosfera.domain.user.User;

import java.util.Date;
import java.util.List;

/**
 * Created by vtarasenko on 15.04.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invitation {

    private Long id;
    private Date creationDate;
    private Date expireDate;
    private User user;
    private String email;
    private String invitedLastName;
    private String invitedFirstName;
    private String invitedFatherName;
    private String invitedGender;
    private Boolean guarantee;
    private Integer howLongFamiliar;
    private String hashUrl;
    private Integer status;
    private Date lastDateSending;
    private User invitee;
    private List<InviteRelationshipTypeDomain> relationships;
    private Integer invitesCount;
}
