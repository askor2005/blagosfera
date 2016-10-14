package ru.askor.blagosfera.web.controllers.ng.user.dto;

import org.springframework.util.Assert;
import ru.askor.blagosfera.domain.invite.Invitation;

import java.util.Date;

/**
 * Created by Maxim Nikitin on 01.04.2016.
 */
public class InvitationDto {

    public Date date;
    public String ikp;
    public String firstName;
    public String middleName;
    public String lastName;
    public String gender;

    public InvitationDto() {
    }

    public InvitationDto(Invitation invitation) {
        Assert.notNull(invitation);

        date = invitation.getCreationDate();
        ikp = invitation.getUser().getIkp();
        firstName = invitation.getUser().getFirstName();
        middleName = invitation.getUser().getSecondName();
        lastName = invitation.getUser().getLastName();
        gender = invitation.getUser().isSex() ? "male" : "female";
    }
}
