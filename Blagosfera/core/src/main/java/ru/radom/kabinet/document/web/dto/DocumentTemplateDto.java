package ru.radom.kabinet.document.web.dto;

import ru.askor.blagosfera.domain.document.DocumentTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 16.06.2016.
 */
public class DocumentTemplateDto {

    public Long id;

    public String name;

    public DocumentTemplateDto(DocumentTemplate documentTemplate) {
        id = documentTemplate.getId();
        name = documentTemplate.getName();
    }

    public static List<DocumentTemplateDto> toDtoList(List<DocumentTemplate> documentTemplates) {
        List<DocumentTemplateDto> result = null;
        if (documentTemplates != null) {
            result = new ArrayList<>();
            for (DocumentTemplate documentTemplate : documentTemplates) {
                result.add(new DocumentTemplateDto(documentTemplate));
            }
        }
        return result;
    }
}
