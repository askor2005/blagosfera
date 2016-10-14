package ru.radom.kabinet.document.dto.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.document.dto.FlowOfDocumentParticipantDTO;
import ru.radom.kabinet.document.model.DocumentParticipantEntity;

/**
 * Created by Otts Alexey on 20.11.2015.<br/>
 * Конвертирует {@link DocumentParticipantEntity} -> {@link FlowOfDocumentParticipantDTO}
 */
@Component
public class FlowOfDocumentParticipantConverter implements Converter<DocumentParticipantEntity, FlowOfDocumentParticipantDTO> {

    @Override
    public FlowOfDocumentParticipantDTO convert(DocumentParticipantEntity source) {
        FlowOfDocumentParticipantDTO dto = new FlowOfDocumentParticipantDTO();
        dto.setSourceParticipantId(source.getSourceParticipantId());
        dto.setParticipantTypeName(source.getParticipantTypeName());
        dto.setParticipantTemplateTypeName(source.getParticipantTemplateTypeName());
        dto.setIsSigned(source.getIsSigned());
        dto.setSignDate(source.getSignDate());
        dto.setSignature(source.getSignature());
        dto.setIsNeedSignDocument(source.getIsNeedSignDocument());
        dto.setSourceParticipantId(source.getSourceParticipantId());
        return dto;
    }
}
