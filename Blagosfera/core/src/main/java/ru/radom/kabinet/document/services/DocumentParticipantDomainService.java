package ru.radom.kabinet.document.services;

import ru.askor.blagosfera.domain.document.DocumentParticipant;

import java.util.List;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
public interface DocumentParticipantDomainService {

    DocumentParticipant getById(Long id);

    List<DocumentParticipant> getByDocumentId(Long documentId);

    /**
     * Установить флаг и дату, что документ подписан участником документа
     * @param participantId ИД участника документа
     */
    void signDocumentByParticipantId(Long participantId);

    void setSignatureByParticipantId(Long participantId, String base64Signature);

    DocumentParticipant delete(Long id);
}
