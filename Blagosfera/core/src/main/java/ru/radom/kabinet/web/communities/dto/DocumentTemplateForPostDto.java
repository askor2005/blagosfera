package ru.radom.kabinet.web.communities.dto;

import lombok.Getter;
import ru.askor.blagosfera.domain.document.DocumentTemplate;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 02.05.2016.
 */
@Getter
public class DocumentTemplateForPostDto {

    private Long id;

    private String name;

    public DocumentTemplateForPostDto(DocumentTemplate documentTemplate) {
        this.id = documentTemplate.getId();
        this.name = documentTemplate.getName();
    }

    public static List<DocumentTemplateForPostDto> toListDto(List<DocumentTemplate> documentTemplates) {
        List<DocumentTemplateForPostDto> result = new ArrayList<>();
        if (documentTemplates != null) {
            result.addAll(documentTemplates.stream().map(DocumentTemplateForPostDto::new).collect(Collectors.toList()));
        }
        return result;
    }
}
