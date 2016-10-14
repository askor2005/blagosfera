package ru.radom.kabinet.document.services;

import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentParameter;
import ru.askor.blagosfera.domain.document.DocumentParticipant;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
public interface DocumentDomainService {

    Document getById(Long id);

    Document getByCode(String code);

    Document getByHashCode(String hashCode);

    Document save(Document document);

    DocumentParameter saveDocumentParameter(Long documentId, DocumentParameter documentParameter);

    List<Document> filter(Long documentClassId, Date createDateStart, Date createDateEnd, String name, Map<Long, List<String>> participantsFilters, String content);

    List<Document> findNotSignedDocuments(List<String> participantTypes, Long sourceParticipantId);

    /**
     * Найти документы по параметрам и по участнику
     *
     * @param parameterName
     * @param participantTypeName
     * @param participantId
     * @return
     */
    List<Document> findByParameterAndParticipant(String parameterName, String participantTypeName, Long participantId);

    /**
     * Загрузить документ по ИД участника документа
     *
     * @param participantId
     * @return
     */
    Document getByParticipantId(Long participantId);

    int countByCodePrefix(String codePrefix);
}
