package ru.radom.kabinet.web.flowofdocuments.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.document.DocumentTemplateParticipant;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 07.07.2016.
 */
@Data
public class DocumentTemplateParticipantDto {

    public Long id;

    public String parentParticipantName;

    public String participantName;

    public DocumentTemplateParticipantDto() {}

    public DocumentTemplateParticipantDto(DocumentTemplateParticipant documentTemplateParticipant) {
        id = documentTemplateParticipant.getId();
        parentParticipantName = documentTemplateParticipant.getParentParticipantName();
        participantName = documentTemplateParticipant.getParticipantName();
    }

    public static List<DocumentTemplateParticipantDto> toDtoList(List<DocumentTemplateParticipant> documentTemplateParticipants) {
        List<DocumentTemplateParticipantDto> result = new ArrayList<>();
        if (documentTemplateParticipants != null) {
            for (DocumentTemplateParticipant documentTemplateParticipant : documentTemplateParticipants) {
                result.add(new DocumentTemplateParticipantDto(documentTemplateParticipant));
            }
        }
        return result;
    }
}
