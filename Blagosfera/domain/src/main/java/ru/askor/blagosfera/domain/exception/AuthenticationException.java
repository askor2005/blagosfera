package ru.askor.blagosfera.domain.exception;

/**
 * Created by mnikitin on 06.05.2016.
 */
public class AuthenticationException extends BlagosferaException {

    public static final String LOGIN_FAILED = "login.failed";

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
