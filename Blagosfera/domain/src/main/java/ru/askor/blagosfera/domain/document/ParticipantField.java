package ru.askor.blagosfera.domain.document;

import lombok.Data;
import ru.askor.blagosfera.domain.field.FieldType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vgusev on 16.06.2015.
 * Класс - контейнер поля участника документа.
 */
@Data
public class ParticipantField {
    /**
     * ИД поля в системе
     */
    private Long id;

    /**
     * Наименование участника документа
     */
    private String participantName;

    /**
     * Дополнительные аттрибуты поля
     */
    private Map<String, String> attributes = new HashMap<>();

    /**
     * Наименование поля
     */
    private String name;

    /**
     * Наименование строкового ИД поля
     */
    private String internalName;

    /**
     * Значение поля
     */
    private String value;

    /**
     * Поле было использовано в документе
     */
    private boolean isUsedInDocument = false;

    /**
     * Тип поля
     */
    private FieldType fieldType;

    private ParticipantField() {
    }

    public ParticipantField(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public ParticipantField(Long id, String participantName, String name, String internalName, String value, FieldType fieldType) {
        this.id = id;
        this.participantName = participantName;
        this.name = name;
        this.internalName = internalName;
        this.value = value;
        this.fieldType = fieldType;
    }


}
