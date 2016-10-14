package ru.radom.kabinet.services.field;

import lombok.Getter;
import ru.askor.blagosfera.domain.field.Field;

/**
 *
 * Created by vgusev on 28.03.2016.
 */
@Getter
public class FieldValidateResult {

    private boolean isSuccess;

    private String message;

    private Field field;

    public FieldValidateResult(boolean isSuccess, Field field, String message) {
        this.isSuccess = isSuccess;
        this.field = field;
        this.message = message;
    }

    public FieldValidateResult(boolean isSuccess, Field field) {
        this.isSuccess = isSuccess;
        this.field = field;
    }
}
