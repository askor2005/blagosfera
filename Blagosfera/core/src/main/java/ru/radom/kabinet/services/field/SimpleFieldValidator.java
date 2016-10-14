package ru.radom.kabinet.services.field;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.model.fields.FieldEntity;

/**
 *
 * Created by vgusev on 28.03.2016.
 */
@Service
public class SimpleFieldValidator implements IFieldValidator {

    @Autowired
    private FieldDao fieldDao;

    @Override
    public FieldValidateResult validate(Field field) {
        FieldValidateResult result;
        FieldEntity fieldEntity = fieldDao.getById(field.getId());
        if (fieldEntity == null) {
            result = new FieldValidateResult(false, field, "Поле не найдено");
        } else {
            if (fieldEntity.isRequired() && StringUtils.isBlank(field.getValue())) {
                result = new FieldValidateResult(false, field, "Необходимо заполнить поле");
            } else if (
                    (fieldEntity.getMinSize() != null || fieldEntity.getMaxSize() != null) &&
                    (
                            FieldType.TEXT.equals(field.getType()) ||
                            FieldType.MULTILINE_TEXT.equals(field.getType()) ||
                            FieldType.HTML_TEXT.equals(field.getType())
                    ) &&
                    (
                            StringUtils.isBlank(field.getValue()) ||
                            (fieldEntity.getMinSize() != null && field.getValue().length() < fieldEntity.getMinSize()) ||
                            (fieldEntity.getMaxSize() != null && field.getValue().length() > fieldEntity.getMaxSize())
                    )
                ) {
                result = new FieldValidateResult(false, field,
                        "Минимальная длина поля: " + (fieldEntity.getMinSize() == null ? 0 : fieldEntity.getMinSize()) + "." +
                        "Максимальная длина поля: " + (fieldEntity.getMaxSize() == null ? 0 : fieldEntity.getMaxSize()) + "."
                );
            } else {
                result = new FieldValidateResult(true, field);
            }
        }
        return result;
    }
}
