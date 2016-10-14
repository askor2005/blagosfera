package ru.radom.kabinet.document.generator;

import ru.askor.blagosfera.domain.document.userfields.DocumentUserFieldType;

import java.util.List;

/**
 *
 * Created by vgusev on 06.07.2015.
 */
public class UserFieldValue {

    private DocumentUserFieldType type;

    private String fieldName;

    private List<? extends Object> values;

    public UserFieldValue(){
    }

    public UserFieldValue(DocumentUserFieldType type, String fieldName, List<? extends Object> values) {
        this.type = type;
        this.fieldName = fieldName;
        this.values = values;
    }

    public DocumentUserFieldType getType() {
        return type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public List<? extends Object> getValues() {
        return values;
    }
}
