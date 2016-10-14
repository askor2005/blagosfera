package ru.radom.kabinet.web.invite.dto;

/**
 *
 * Created by vgusev on 25.04.2016.
 */
public enum RejectInvitationResult {

    REJECT_SUCCESS,
    NOT_FOUND,
    NOT_INSTALL_INVITER,
    EXPIRED,
    ACCEPTED,
    REJECTED,
    AUTH_USER
}
