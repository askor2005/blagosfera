package ru.radom.kabinet.web.flowofdocuments.dto;

import ru.askor.blagosfera.domain.document.DocumentTemplate;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 31.05.2016.
 */
public class DocumentTemplateDto {

    public Long id;
    public DocumentClassDto documentType;
    public String name;
    public String content;
    public List<DocumentTemplateFilterValueDto> filters;

    public static DocumentTemplateDto toDto(DocumentTemplateEntity documentTemplate) {
        DocumentTemplateDto result = new DocumentTemplateDto();
        result.id = documentTemplate.getId();
        result.name = documentTemplate.getName();
        result.content = documentTemplate.getContent();
        result.documentType = DocumentClassDto.toDto(documentTemplate.getDocumentType());
        result.filters = DocumentTemplateFilterValueDto.toDtoList(documentTemplate.getFilters());
        return result;
    }

    public static DocumentTemplateDto toDto(DocumentTemplate documentTemplate, boolean withContent) {
        DocumentTemplateDto result = new DocumentTemplateDto();
        result.id = documentTemplate.getId();
        result.name = documentTemplate.getName();
        if (withContent) {
            result.content = documentTemplate.getContent();
        }
        result.documentType = DocumentClassDto.toDto(documentTemplate.getDocumentClass());
        //result.filters = DocumentTemplateFilterValueDto.toDtoList(documentTemplate.getF());
        return result;
    }

    public static List<DocumentTemplateDto> toDtoList(List<DocumentTemplate> documentTemplates, boolean withContent) {
        List<DocumentTemplateDto> result = null;
        if (documentTemplates != null) {
            result = new ArrayList<>();
            for (DocumentTemplate documentTemplate : documentTemplates) {
                result.add(toDto(documentTemplate, withContent));
            }
        }
        return result;
    }
}
