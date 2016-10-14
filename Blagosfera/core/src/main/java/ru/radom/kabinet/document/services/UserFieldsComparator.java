package ru.radom.kabinet.document.services;

import ru.askor.blagosfera.domain.document.userfields.DocumentUserField;

import java.util.Comparator;

/**
 * Сортируем пользовательские поля на основе того, как их отсортировали в шаблоне документа
 * Created by vgusev on 09.08.2015.
 */
public class UserFieldsComparator implements Comparator<DocumentUserField> {

    @Override
    public int compare(DocumentUserField a, DocumentUserField b) {
        Long position1 = -1l;
        Long position2 = -1l;
        if (a.getParameters().containsKey("fieldPosition")) {
            position1 = (Long)a.getParameters().get("fieldPosition");
        }
        if (b.getParameters().containsKey("fieldPosition")) {
            position2 = (Long)b.getParameters().get("fieldPosition");
        }
        return position1 > position2 ? 1 : -1;
    }

}
