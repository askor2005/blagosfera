package ru.radom.kabinet.document.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.askor.blagosfera.domain.document.DocumentCreator;
import ru.askor.blagosfera.domain.document.DocumentParticipant;
import ru.askor.blagosfera.domain.document.userfields.DocumentUserField;
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
public class DocumentPageDto {

    private Long documentId;

    private String documentName;

    private boolean isDocumentActive;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date documentCreateDate;

    private String documentCode;

    private String userOfficialAppeal;

    private String userOfficialName;

    private List<DocumentUserFieldDto> allUserFields;

    private List<DocumentParticipantDto> signParticipants;

    private List<DocumentParticipantDto> participants;

    private boolean isSigned;

    private boolean isUserHasDocumentRight;

    private DocumentCreatorDto creator;

    private boolean canUnsignDocument;

    public DocumentPageDto(
            Long documentId,
            String documentName,
            boolean isDocumentActive,
            Date documentCreateDate,
            String documentCode,
            List<DocumentUserField> allUserFields,
            List<DocumentParticipant> signParticipants,
            List<DocumentParticipant> allParticipants,
            Map<Long, String> sourceNames, boolean isSigned, boolean isUserHasDocumentRight,
            DocumentCreator creator,
            boolean canUnsignDocument) {
        setDocumentId(documentId);
        setDocumentName(documentName);
        setDocumentActive(isDocumentActive);
        setDocumentCreateDate(documentCreateDate);
        setDocumentCode(documentCode);
        setAllUserFields(DocumentUserFieldDto.toDtoList(allUserFields));
        setSignParticipants(DocumentParticipantDto.toDtoList(signParticipants, sourceNames));
        setParticipants(DocumentParticipantDto.toDtoList(allParticipants, sourceNames));
        setSigned(isSigned);
        setUserHasDocumentRight(isUserHasDocumentRight);
        if (creator != null) {
            setCreator(new DocumentCreatorDto(creator));
        }
        setCanUnsignDocument(canUnsignDocument);
    }
}
