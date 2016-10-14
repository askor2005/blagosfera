package ru.radom.kabinet.document.web.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.document.DocumentClassDataSource;
import ru.askor.blagosfera.domain.document.userfields.DocumentUserField;
import ru.askor.blagosfera.domain.document.userfields.DocumentUserFieldType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
@Data
public class DocumentUserFieldDto {

    private String name;

    private String description;

    private DocumentUserFieldType type;

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

    public DocumentUserFieldDto(DocumentUserField documentUserField) {
        setName(documentUserField.getName());
        setDescription(documentUserField.getDescription());
        setType(DocumentUserFieldType.getByType(documentUserField.getType()));
        setList(documentUserField.isList());
        setListSize(documentUserField.getListSize());
        setParticipantId(documentUserField.getParticipantId());
        setListViewType(documentUserField.getListViewType());
        setParameters(documentUserField.getParameters());
        setDocumentFieldValues(documentUserField.getDocumentFieldValues());
    }

    public static List<DocumentUserFieldDto> toDtoList(List<DocumentUserField> userFields) {
        List<DocumentUserFieldDto> result = null;
        if (userFields != null) {
            result = new ArrayList<>();
            for (DocumentUserField documentUserField : userFields) {
                result.add(new DocumentUserFieldDto(documentUserField));
            }
        }
        return result;
    }
}
