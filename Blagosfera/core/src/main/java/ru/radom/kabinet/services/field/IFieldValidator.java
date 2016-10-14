package ru.radom.kabinet.services.field;

import ru.askor.blagosfera.domain.field.Field;

/**
 *
 * Created by vgusev on 28.03.2016.
 */
public interface IFieldValidator {

    FieldValidateResult validate(Field field);
}
