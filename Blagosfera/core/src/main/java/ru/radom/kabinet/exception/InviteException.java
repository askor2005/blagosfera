package ru.radom.kabinet.exception;

/**
 * Класс исключения, выбрасывающегося при ошибках, связанных с приглашениями участников в систему
 */
public class InviteException extends RuntimeException {

    public InviteException() {
        super();
    }

    public InviteException(Throwable cause) {
        super(cause);
    }

    public InviteException(String message) {
        super(message);
    }

    public InviteException(String message, Throwable cause) {
        super(message, cause);
    }
}
