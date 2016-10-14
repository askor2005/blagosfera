package ru.askor.blagosfera.domain.document.userfields.fieldsgroups;

import ru.askor.blagosfera.domain.document.userfields.DocumentUserField;

import java.util.List;

/**
 * Обёртка групповых полей
 * Created by vgusev on 06.08.2015.
 */
public class UserFieldsGroup {

    private String name;

    private List<DocumentUserField> userFields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DocumentUserField> getUserFields() {
        return userFields;
    }

    public void setUserFields(List<DocumentUserField> userFields) {
        this.userFields = userFields;
    }
}
