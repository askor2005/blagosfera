package ru.radom.kabinet.web.flowofdocuments.dto;

import ru.radom.kabinet.document.model.DocumentTemplateFilterValueEntity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 31.05.2016.
 */
public class DocumentTemplateFilterValueDto {

    public Long id;
    public String value;

/*
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("id", documentTemplateFilterValue.getId());
    if (documentTemplateFilterValue.getDocumentTemplate() != null) {
        JSONObject jsonDocumentTemplate = new JSONObject();
        jsonDocumentTemplate.put("id", documentTemplateFilterValue.getDocumentTemplate().getId());
        jsonDocumentTemplate.put("name", documentTemplateFilterValue.getDocumentTemplate().getName());
        jsonObject.put("documentTemplate", jsonDocumentTemplate);
    }
    if (documentTemplateFilterValue.getFilterField() != null) {
        jsonObject.put("filterField", fieldSerializer.serializeSingleField(documentTemplateFilterValue.getFilterField()));
    }
    jsonObject.put("value", documentTemplateFilterValue.getValue());*/

    public static DocumentTemplateFilterValueDto toDto(DocumentTemplateFilterValueEntity documentTemplateFilterValueEntity) {
        DocumentTemplateFilterValueDto result = null;
        if (documentTemplateFilterValueEntity != null) {
            result = new DocumentTemplateFilterValueDto();
            result.id = documentTemplateFilterValueEntity.getId();
            result.value = documentTemplateFilterValueEntity.getValue();
        }
        return result;
    }

    public static List<DocumentTemplateFilterValueDto> toDtoList(List<DocumentTemplateFilterValueEntity> documentTemplateFilterValueEntities) {
        List<DocumentTemplateFilterValueDto> result = null;
        if (documentTemplateFilterValueEntities != null && !documentTemplateFilterValueEntities.isEmpty()) {
            result = new ArrayList<>();
            for (DocumentTemplateFilterValueEntity documentTemplateFilterValueEntity : documentTemplateFilterValueEntities) {
                result.add(toDto(documentTemplateFilterValueEntity));
            }
        }
        return result;
    }
}
