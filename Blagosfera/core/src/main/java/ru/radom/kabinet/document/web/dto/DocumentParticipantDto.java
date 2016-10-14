package ru.radom.kabinet.document.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.DocumentParticipant;
import ru.radom.kabinet.json.FullDateSerializer;
import ru.radom.kabinet.json.TimeStampDateSerializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
@Data
public class DocumentParticipantDto {

    // ИД участника дкоумента
    private Long id;

    // ИД пользователя системы
    private Long sourceParticipantId;

    // Имя участника документа
    private String name;

    // Имя пользователя системы
    private String sourceName;

    // Название родительского участника документа
    private String parentName;

    // Название родиетльского объекта в системе
    private String parentSourceName;

    private boolean isNeedSignDocument;

    @JsonSerialize(using = FullDateSerializer.class)
    private Date signDate;

    private ParticipantsTypes type;

    public DocumentParticipantDto(DocumentParticipant documentParticipant, String participantSourceName, String parentName, String parentSourceName) {
        setId(documentParticipant.getId());
        setSourceParticipantId(documentParticipant.getSourceParticipantId());
        setName(documentParticipant.getParticipantTemplateTypeName());
        setSourceName(participantSourceName);
        setParentName(parentName);
        setParentSourceName(parentSourceName);
        setNeedSignDocument(documentParticipant.isNeedSignDocument());
        setSignDate(documentParticipant.getSignDate());
        setType(ParticipantsTypes.valueOf(documentParticipant.getParticipantTypeName()));
    }

    public static List<DocumentParticipantDto> toDtoList(List<DocumentParticipant> participants, Map<Long, String> sourceNames) {
        List<DocumentParticipantDto> result = null;
        if (participants != null) {
            result = new ArrayList<>();
            for (DocumentParticipant documentParticipant : participants) {
                if (documentParticipant.getChildren() != null && documentParticipant.getChildren().size() > 0) {
                    for (DocumentParticipant childParticipant : documentParticipant.getChildren()) {
                        DocumentParticipantDto documentParticipantDto = new DocumentParticipantDto(
                                childParticipant,
                                sourceNames.get(childParticipant.getId()),
                                documentParticipant.getParticipantTemplateTypeName(),
                                sourceNames.get(documentParticipant.getId())
                        );
                        result.add(documentParticipantDto);
                    }
                } else {
                    DocumentParticipantDto documentParticipantDto = new DocumentParticipantDto(
                            documentParticipant,
                            sourceNames.get(documentParticipant.getId()),
                            null,
                            null
                    );
                    result.add(documentParticipantDto);
                }
            }
        }
        return result;
    }


}
