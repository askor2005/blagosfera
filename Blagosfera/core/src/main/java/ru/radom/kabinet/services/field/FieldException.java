package ru.radom.kabinet.services.field;

import lombok.Getter;

import java.util.List;

/**
 *
 * Created by vgusev on 28.03.2016.
 */
@Getter
public class FieldException extends RuntimeException {

    private List<FieldValidateResult> fieldValidateResults;

    public FieldException(String message) {
        super(message);
    }

    public FieldException(String message, List<FieldValidateResult> fieldValidateResults) {
        super(message);
        this.fieldValidateResults = fieldValidateResults;
    }
}
