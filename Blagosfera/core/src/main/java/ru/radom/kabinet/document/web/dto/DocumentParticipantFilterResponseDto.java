package ru.radom.kabinet.document.web.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.DocumentParticipant;
import ru.askor.blagosfera.domain.document.IDocumentParticipant;

/**
 * Обёртка для данных списка участников документов на странице списка документов
 * Created by vgusev on 08.04.2016.
 */
@Data
public class DocumentParticipantFilterResponseDto {

    // исходный ИД участника документов (ИД пользователя и т.д.)
    private Long id;

    private ParticipantsTypes type;

    // Наименование исходного участника документов
    private String name;

    public DocumentParticipantFilterResponseDto(DocumentParticipant documentParticipant, IDocumentParticipant sourceDocumentParticipant) {
        setId(documentParticipant.getSourceParticipantId());
        setType(ParticipantsTypes.valueOf(documentParticipant.getParticipantTypeName()));
        setName(sourceDocumentParticipant.getName());
    }
}
