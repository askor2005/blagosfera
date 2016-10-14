package ru.radom.kabinet.web.flowofdocuments.dto;

import lombok.Data;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 09.04.2016.
 */
@Data
public class DocumentTemplatesGridDto {

    private boolean isSuccess;

    private int total;

    private List<DocumentTemplateGridItemDto> items;

    private DocumentTemplatesGridDto(){}

    public DocumentTemplatesGridDto(List<DocumentTemplateEntity> documentTemplates, int totalCount) {
        if (documentTemplates != null) {
            items = new ArrayList<>();
            for (DocumentTemplateEntity documentTemplate : documentTemplates) {
                items.add(new DocumentTemplateGridItemDto(documentTemplate));
            }
        }
        setTotal(totalCount);
        setSuccess(true);
    }

    public static DocumentTemplatesGridDto toError() {
        DocumentTemplatesGridDto result = new DocumentTemplatesGridDto();
        result.setSuccess(false);
        return result;
    }

}
