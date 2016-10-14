package ru.radom.kabinet.services.communities.organization.po;

import lombok.Getter;

/**
 *
 * Created by vgusev on 18.12.2015.
 */
@Getter
public class RegisterPOException extends RuntimeException {

    private String errorField;

    public RegisterPOException(String message, String errorField) {
        super(message);
        this.errorField = errorField;
    }
}
