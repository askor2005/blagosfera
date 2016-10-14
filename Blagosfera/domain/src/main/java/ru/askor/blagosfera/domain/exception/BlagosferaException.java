package ru.askor.blagosfera.domain.exception;

/**
 * Created by mnikitin on 06.05.2016.
 */
public class BlagosferaException extends Exception {

    public BlagosferaException(String message) {
        super(message);
    }

    public BlagosferaException(String message, Throwable cause) {
        super(message, cause);
    }
}
