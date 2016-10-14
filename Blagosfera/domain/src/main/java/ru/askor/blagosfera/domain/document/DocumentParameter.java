package ru.askor.blagosfera.domain.document;

import lombok.Data;

/**
 * Параметр документа
 * Created by vgusev on 08.04.2016.
 */
@Data
public class DocumentParameter {

    public static final String EVENT_TYPE = "eventType"; // Наименование параметра - имя события
    public static final String CONTEXT = "context";

    private Long id;

    private String name;

    private String value;
}
