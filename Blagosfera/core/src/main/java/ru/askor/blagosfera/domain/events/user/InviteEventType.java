package ru.askor.blagosfera.domain.events.user;

/**
 * Возможные типы для событий, связанных с приглашением в систему
 */
public enum InviteEventType {

    INVITE,
    ACCEPT_INVITE;

    @Override
    public String toString() {
        switch (this) {
            case INVITE:
                return "invite";
            case ACCEPT_INVITE:
                return "accept_invite";
            default:
                throw new IllegalStateException("Unsupported SharerEventType");
        }
    }

}
