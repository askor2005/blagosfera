package ru.askor.blagosfera.domain.document.userfields;

import java.util.stream.Stream;

/**
 *
 * Created by vgusev on 06.07.2015.
 */
public enum DocumentUserFieldType {
    STRING("string"), NUMBER("number"), DATE("date"), PARTICIPANT("participant"), CURRENCY("currency"), FIELDS_GROUPS("fieldsGroups"), DOCUMENT("document");

    DocumentUserFieldType(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }

    /**
     * Получить {@link DocumentUserFieldType} по типу
     */
    public static DocumentUserFieldType getByType(String type) {
        return Stream.of(values()).filter(t -> t.getType().toLowerCase().equals(type.toLowerCase()))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Unknown DocumentUserFieldType:" + type));
    }
}
