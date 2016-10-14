package ru.radom.kabinet.web.invite.dto;

/**
 *
 * Created by vgusev on 25.04.2016.
 */
public enum AcceptInvitationResult {

    ACCEPT_SUCCESS,
    NOT_FOUND,
    EXPIRED,
    ACCEPTED,
    REJECTED,
    NEED_AVATAR_SOURCE,
    NEED_AVATAR,
    NEED_PASSWORD,
    PASSWORD_ERROR_LENGTH,
    AUTH_USER
}
