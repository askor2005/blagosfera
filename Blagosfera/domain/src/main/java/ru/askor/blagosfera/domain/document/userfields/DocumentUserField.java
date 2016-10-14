package ru.askor.blagosfera.domain.document.userfields;

import lombok.Data;
import ru.askor.blagosfera.domain.document.DocumentClassDataSource;

import java.util.List;
import java.util.Map;

/**
 * Created by vgusev on 26.06.2015.
 * Кдасс - контейнер пользовательских полей
 */
@Data
public class DocumentUserField {

    private String name;

    private String description;

    private String type;

    private boolean isList = false;

    private int listSize;

    /**
     * {@link DocumentClassDataSource#id}
     */
    private Long participantId;

    // Вид отображения списка полей
    private String listViewType;

    private Map<String, Object> parameters;

    // Массив значений поля
    private List<String> documentFieldValues;

}
