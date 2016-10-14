package ru.radom.kabinet.web.invite.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.askor.blagosfera.domain.invite.Invitation;
import ru.askor.blagosfera.domain.invite.InvitationStatus;
import ru.radom.kabinet.json.TimeStampDateSerializer;

import java.util.Date;

/**
 *
 * Created by vgusev on 22.04.2016.
 */
@Data
public class AcceptInvitationPageDataDto {

    private String offer;

    private Long inviteId;

    private String hash;

    private InvitationStatus status;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date creationDate;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date expireDate;

    private String email;

    private String invitedLastName;

    private String invitedFirstName;

    private String invitedFatherName;

    private String inviterLastName;

    private String inviterFirstName;

    private String inviterFatherName;

    private String inviterAvatar;

    private boolean inviterSex;

    private boolean isAuthUser;

    public AcceptInvitationPageDataDto(String offerText, Invitation invitation) {
        setOffer(offerText);
        setAuthUser(false);
        if (invitation != null) {
            setInviteId(invitation.getId());
            setHash(invitation.getHashUrl());
            setStatus(InvitationStatus.getByCode(invitation.getStatus()));
            setCreationDate(invitation.getCreationDate());
            setExpireDate(invitation.getExpireDate());
            setEmail(invitation.getEmail());
            setInvitedLastName(invitation.getInvitedLastName());
            setInvitedFatherName(invitation.getInvitedFatherName());
            setInvitedFirstName(invitation.getInvitedFirstName());
            setInviterLastName(invitation.getUser().getLastName());
            setInviterFatherName(invitation.getUser().getSecondName());
            setInviterFirstName(invitation.getUser().getFirstName());
            setInviterAvatar(invitation.getUser().getAvatar());
            setInviterSex(invitation.getUser().isSex());
        }
    }

    public AcceptInvitationPageDataDto(boolean isAuthUser) {
        setAuthUser(isAuthUser);
    }
}
