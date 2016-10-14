package ru.radom.kabinet.services.field;

import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.IFieldOwner;

/**
 *
 * Created by vgusev on 28.03.2016.
 */
public interface ICustomFieldValidator {

    boolean isCustomValidate(Field field, IFieldOwner owner);

    FieldValidateResult validate(Field field, IFieldOwner owner);
}
