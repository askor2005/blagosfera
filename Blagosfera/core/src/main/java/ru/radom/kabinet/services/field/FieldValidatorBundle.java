package ru.radom.kabinet.services.field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.IFieldOwner;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 28.03.2016.
 */
@Service
@Scope("prototype")
public class FieldValidatorBundle implements IFieldValidatorBundle {

    @Autowired
    private IFieldValidator fieldValidator;

    private ICustomFieldValidator customFieldValidator;

    @Override
    public FieldValidateResult validate(Field field, IFieldOwner owner) {
        FieldValidateResult result;
        if (customFieldValidator != null && customFieldValidator.isCustomValidate(field, owner)) {
            result = customFieldValidator.validate(field, owner);
        } else {
            result = fieldValidator.validate(field);
        }
        return result;
    }

    @Override
    public List<FieldValidateResult> validate(List<Field> fields, IFieldOwner owner) {
        List<FieldValidateResult> result = null;
        if (fields != null) {
            result = new ArrayList<>();
            for (Field field : fields) {
                if (field != null) {
                    result.add(validate(field, owner));
                }
            }
        }
        return result;
    }

    @Override
    public void setCustomFieldValidator(ICustomFieldValidator customFieldValidator) {
        this.customFieldValidator = customFieldValidator;
    }
}
