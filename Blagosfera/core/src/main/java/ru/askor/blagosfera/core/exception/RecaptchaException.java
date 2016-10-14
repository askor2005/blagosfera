package ru.askor.blagosfera.core.exception;

import ru.askor.blagosfera.core.security.RecaptchaResponse;

/**
 * Created by Maxim Nikitin on 05.04.2016.
 */
public class RecaptchaException extends Exception {

    private RecaptchaResponse recaptchaResponse;

    public RecaptchaException(String message) {
        super(message);
    }

    public RecaptchaException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecaptchaException(String message, RecaptchaResponse recaptchaResponse) {
        super(message);
        this.recaptchaResponse = recaptchaResponse;
    }

    public RecaptchaException(String message, Throwable cause, RecaptchaResponse recaptchaResponse) {
        super(message, cause);
        this.recaptchaResponse = recaptchaResponse;
    }
}
