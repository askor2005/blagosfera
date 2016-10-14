package ru.radom.kabinet.web.flowofdocuments.dto;

import lombok.Data;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;

import java.util.Collections;
import java.util.List;

/**
 *
 * Created by vgusev on 08.04.2016.
 */
@Data
public class DocumentTemplateGridItemDto {

    private Long id;

    private String name;

    private String content;

    private String className;

    private List<Object> filters;

    public DocumentTemplateGridItemDto(DocumentTemplateEntity documentTemplate) {
        setId(documentTemplate.getId());
        setName(documentTemplate.getName());
        setContent(documentTemplate.getContent());
        if (documentTemplate.getDocumentType() != null) {
            setClassName(documentTemplate.getDocumentType().getName());
        }
        setFilters(Collections.emptyList());
    }
}
