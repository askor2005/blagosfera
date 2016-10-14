package ru.radom.kabinet.web.emailsender.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.document.DocumentTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 07.05.2016.
 */
@Data
public class FindTemplateResponseDto {

    private Long id;

    private String name;

    private String code;

    public FindTemplateResponseDto(DocumentTemplate documentTemplate) {
        setId(documentTemplate.getId());
        setName(documentTemplate.getName());
        setCode(documentTemplate.getCode());
    }

    public static List<FindTemplateResponseDto> toDtoList(List<DocumentTemplate> documentTemplates) {
        List<FindTemplateResponseDto> result = null;
        if (documentTemplates != null && !documentTemplates.isEmpty()) {
            result = new ArrayList<>();
            for (DocumentTemplate documentTemplate : documentTemplates) {
                result.add(new FindTemplateResponseDto(documentTemplate));
            }
        }
        return result;
    }
}
