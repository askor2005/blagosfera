package ru.radom.kabinet.services.field;

import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.IFieldOwner;

import java.util.List;

/**
 *
 * Created by vgusev on 28.03.2016.
 */
public interface IFieldValidatorBundle {

    /**
     * Проверка валидности поля
     * @param field
     * @return
     */
    FieldValidateResult validate(Field field, IFieldOwner owner);

    /**
     * Проверка валидности полей
     * @param fields
     * @return
     */
    List<FieldValidateResult> validate(List<Field> fields, IFieldOwner owner);

    /**
     * Установить кастомный валидатор для полей
     * @param customFieldValidator
     */
    void setCustomFieldValidator(ICustomFieldValidator customFieldValidator);

}
