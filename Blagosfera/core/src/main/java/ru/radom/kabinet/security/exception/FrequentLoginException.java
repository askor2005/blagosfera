package ru.radom.kabinet.security.exception;

import org.springframework.security.authentication.AccountStatusException;

/**
 * Исключение - слишком частый логин в систему
 * Created by vgusev on 24.11.2015.
 */
public class FrequentLoginException extends AccountStatusException {

    public FrequentLoginException(String message) {
        super(message);
    }
}
