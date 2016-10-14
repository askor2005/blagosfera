package ru.askor.blagosfera.domain.events.user;

/**
 * @author dfilinberg
 */
public enum SharerEventType {

    REGISTER, ACTIVATE, CHANGE_PASSWORD, CHANGE_EMAIL_INIT, CHANGE_EMAIL_COMPLETE,
    RECOVERY_VERIFICATION_CODE_INIT, RECOVERY_PASSWORD_INIT, RECOVERY_PASSWORD_COMPLETE, ARCHIVED, DELETED;

    @Override
    public String toString() {
        switch (this) {
            case REGISTER:
                return "Register";
            case ACTIVATE:
                return "Activate";
            case CHANGE_PASSWORD:
                return "ChangePassword";
            case CHANGE_EMAIL_INIT:
                return "ChangeEmailInit";
            case CHANGE_EMAIL_COMPLETE:
                return "ChangeEmailComplete";
            case RECOVERY_VERIFICATION_CODE_INIT:
                return "RecoveryVerificationCodeInit";
            case RECOVERY_PASSWORD_INIT:
                return "RecoveryPasswordInit";
            case RECOVERY_PASSWORD_COMPLETE:
                return "RecoveryPasswordComplete";
            case ARCHIVED:
                return "Archived";
            case DELETED:
                return "Deleted";
            default:
                throw new IllegalStateException("Unsupported SharerEventType");
        }
    }
}
