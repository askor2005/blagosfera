package ru.askor.blagosfera.domain.invite;

import lombok.Getter;

/**
 *
 * Created by vgusev on 22.04.2016.
 */
@Getter
public enum InvitationStatus {

    NEW_INVITATION(0),
    ACCEPTED_INVITATION(1),
    REJECTED_INVITATION(2);

    private int code;

    InvitationStatus(int code) {
        this.code = code;
    }

    public static InvitationStatus getByCode(int code) {
        InvitationStatus result = null;
        for (InvitationStatus invitationStatus : values()) {
            if (invitationStatus.getCode() == code) {
                result = invitationStatus;
                break;
            }
        }
        return result;
    }
}
