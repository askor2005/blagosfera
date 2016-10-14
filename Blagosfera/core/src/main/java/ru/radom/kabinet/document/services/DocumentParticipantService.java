package ru.radom.kabinet.document.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.*;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.document.dto.DocumentParticipantSourceDto;
import ru.radom.kabinet.document.dto.PossibleSourceParticipantDto;
import ru.radom.kabinet.document.dto.PossibleSourceParticipantsDto;
import ru.radom.kabinet.document.services.participants.DocumentParticipantSourceService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервисный класс для обработки действий над участникаим документа
 * Created by vgusev on 06.08.2015.
 */
@Service
public class DocumentParticipantService {

    private List<DocumentParticipantSourceService> documentParticipantSourceServices;

    @Autowired
    public void setDocumentParticipantSourceServices(List<DocumentParticipantSourceService> documentParticipantSourceServices) {
        this.documentParticipantSourceServices = documentParticipantSourceServices;
    }

    private DocumentParticipantSourceService getParticipantSourceServiceByType(ParticipantsTypes participantsTypes) {
        DocumentParticipantSourceService result = null;
        if (this.documentParticipantSourceServices != null) {
            for (DocumentParticipantSourceService documentParticipantSourceService : this.documentParticipantSourceServices) {
                if (documentParticipantSourceService.getType().equals(participantsTypes)) {
                    result = documentParticipantSourceService;
                    break;
                }
            }
        }
        return result;
    }

    public DocumentParticipantSourceDto createFlowOfDocumentParticipant(
            ParticipantsTypes participantType, IDocumentParticipant documentParticipant, String participantName, List<Long> filteredFieldIds,
            List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes, boolean needFillSystemFields,
            int index) {
        DocumentParticipantSourceService documentParticipantSourceService = getParticipantSourceServiceByType(participantType);
        ExceptionUtils.check(participantType == null, "Не передан тип участника документа");
        ExceptionUtils.check(documentParticipantSourceService == null, "Не найден сервис обработки участника документа с типом " + participantType.getName());
        return documentParticipantSourceService.getParticipantSource(
                documentParticipant, participantName, filteredFieldIds, excludedFieldTypes, includedFieldTypes, needFillSystemFields, index
        );
    }

    public DocumentParticipantSourceDto createFlowOfDocumentParticipant(
            ParticipantsTypes participantType, Long documentParticipantId, String participantName, List<Long> filteredFieldIds,
            List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes, boolean needFillSystemFields,
            int index) {
        DocumentParticipantSourceService documentParticipantSourceService = getParticipantSourceServiceByType(participantType);
        ExceptionUtils.check(participantType == null, "Не передан тип участника документа");
        ExceptionUtils.check(documentParticipantSourceService == null, "Не найден сервис обработки участника документа с типом " + participantType.getName());
        DocumentParticipantSourceDto documentParticipantSourceDto = documentParticipantSourceService.getParticipantSource(
                documentParticipantId, participantName, filteredFieldIds, excludedFieldTypes, includedFieldTypes, needFillSystemFields, index
        );
        documentParticipantSourceDto.setType(participantType);
        return documentParticipantSourceDto;
    }


    /**
     * Получить участника документа с заполненными полями, со значениями загруженными для тех полей которые переданы параметров filteredFields
     *
     * @param participantType
     * @param documentParticipant
     * @param participantName
     * @param filteredFieldIds
     * @param excludedFieldTypes
     * @param includedFieldTypes
     * @param needFillSystemFields
     * @return
     */
    public DocumentParticipantSourceDto createFlowOfDocumentParticipant(
            String participantType, IDocumentParticipant documentParticipant, String participantName, List<Long> filteredFieldIds,
            List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes, boolean needFillSystemFields,
            int index) {
        return createFlowOfDocumentParticipant(getType(participantType), documentParticipant, participantName, filteredFieldIds, excludedFieldTypes, includedFieldTypes, needFillSystemFields, index);
    }

    /**
     *
     * @param participantType
     * @param documentParticipantId
     * @param participantName
     * @param filteredFieldIds
     * @param excludedFieldTypes
     * @param includedFieldTypes
     * @param needFillSystemFields
     * @param index
     * @return
     */
    public DocumentParticipantSourceDto createFlowOfDocumentParticipant(
            String participantType, Long documentParticipantId, String participantName, List<Long> filteredFieldIds,
            List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes, boolean needFillSystemFields,
            int index) {
        return createFlowOfDocumentParticipant(getType(participantType), documentParticipantId, participantName, filteredFieldIds, excludedFieldTypes, includedFieldTypes, needFillSystemFields, index);
    }

    public DocumentParticipantSourceDto createFlowOfDocumentParticipant(String participantType, long participantId, int index) {
        return createFlowOfDocumentParticipant(participantType, participantId, null, null, null, null, true, index);
    }

    public DocumentParticipantSourceDto createFlowOfDocumentParticipant(ParticipantsTypes type, Long participantId, int index) {
        return createFlowOfDocumentParticipant(type, participantId, null, null, null, null, true, index);
    }

    public IDocumentParticipant getDocumentParticipantById(String participantsType, Long id) {
        DocumentParticipantSourceService documentParticipantSourceService = getParticipantSourceServiceByType(getType(participantsType));
        return documentParticipantSourceService.getSourceParticipantById(id);
    }

    public IDocumentParticipant getDocumentParticipantById(ParticipantsTypes type, Long id) {
        ExceptionUtils.check(type == null, "Не передан тип участника документа");
        DocumentParticipantSourceService documentParticipantSourceService = getParticipantSourceServiceByType(type);
        return documentParticipantSourceService.getSourceParticipantById(id);
    }

    /**
     * Инициализация участника документа перед созданием документа
     * @param documentTemplate
     * @param sourceParticipant
     */
    public DocumentParticipant convertSourceParticipantToDocumentParticipant(DocumentTemplate documentTemplate, DocumentParticipantSourceDto sourceParticipant) {
        DocumentParticipantSourceService documentParticipantSourceService = getParticipantSourceServiceByType(sourceParticipant.getType());
        return documentParticipantSourceService.convertSourceParticipantToDocumentParticipant(documentTemplate, sourceParticipant);
    }

    private ParticipantsTypes getType(String participantTypeName) {
        ParticipantsTypes type = ParticipantsTypes.valueOf(participantTypeName);
        ExceptionUtils.check(type == null, "Не передан тип участника документа");
        return type;
    }

    /**
     * Получить пользователей системы из участника документа для заполнения пользоватлельских полей
     *
     * @param documentParticipant
     * @return
     */
    public List<User> getUsersFromParticipantForFillUserFields(DocumentParticipant documentParticipant) {
        DocumentParticipantSourceService documentParticipantSourceService = getParticipantSourceServiceByType(getType(documentParticipant.getParticipantTypeName()));
        return documentParticipantSourceService.getUsersFromParticipantForFillUserFields(documentParticipant);
    }

    public List<User> getUsersFromParticipantForSignDocument(DocumentParticipant documentParticipant) {
        DocumentParticipantSourceService documentParticipantSourceService = getParticipantSourceServiceByType(getType(documentParticipant.getParticipantTypeName()));
        return documentParticipantSourceService.getUsersFromParticipantForSignDocument(documentParticipant);
    }

    /**
     * Загрузить возможных участников для документа
     * @param documentClassDataSource
     * @return
     */
    public PossibleSourceParticipantsDto getPossibleSourceParticipants(DocumentClassDataSource documentClassDataSource) {
        long start = System.currentTimeMillis();
        PossibleSourceParticipantsDto result = new PossibleSourceParticipantsDto();
        DocumentParticipantSourceService documentParticipantSourceService = getParticipantSourceServiceByType(documentClassDataSource.getType());
        ExceptionUtils.check(documentParticipantSourceService == null, "Не найден сервис обработки участника документа с типом " + documentClassDataSource.getName());
        List<PossibleSourceParticipantDto> possibleSourceParticipants = documentParticipantSourceService.getPossibleSourceParticipants(documentClassDataSource);
        result.setPossibleSourceParticipants(possibleSourceParticipants);
        result.setDocumentClassDataSourceId(documentClassDataSource.getId());
        result.setDocumentClassDataSourceType(documentClassDataSource.getType());
        result.setDocumentTypeDataSourceName(documentClassDataSource.getName());
        result.setListParticipant(documentParticipantSourceService.isListDataSource());
        long end = System.currentTimeMillis();
        System.err.println(((end - start) / 1000) + " сек. " + documentClassDataSource.getType());
        return result;
    }

    /**
     * Получить участников документа по пользователлю
     * @param document
     * @param userId
     * @return
     */
    public List<DocumentParticipant> getParticipantsOfUser(Document document, Long userId) {
        //DocumentParticipantSourceService documentParticipantSourceService = getParticipantSourceServiceByType(documentClassDataSource.getType());
        ExceptionUtils.check(document == null, "Не передалн документ в метод получения участников по пользователю");
        ExceptionUtils.check(document.getParticipants() == null, "Не установлены участники документа в методе получения участников по пользователю");
        List<DocumentParticipant> result = new ArrayList<>();
        for (DocumentParticipant documentParticipant : document.getParticipants()) {
            DocumentParticipantSourceService documentParticipantSourceService = getParticipantSourceServiceByType(getType(documentParticipant.getParticipantTypeName()));
            result.addAll(documentParticipantSourceService.getParticipantsOfUser(documentParticipant, userId));
        }
        return result;
    }

    /**
     * Получить мапу с именами участников документа
     * @param document
     * @return
     */
    public Map<Long, String> getSourceNames(Document document) {
        ExceptionUtils.check(document == null, "Не передалн документ в метод получения участников по пользователю");
        return getSourceNames(document.getParticipants());
    }

    public Map<Long, String> getSourceNames(List<DocumentParticipant> documentParticipants) {
        ExceptionUtils.check(documentParticipants == null, "Не установлены участники документа в методе получения участников по пользователю");
        Map<Long, String> result = new HashMap<>();
        for (DocumentParticipant documentParticipant : documentParticipants) {
            DocumentParticipantSourceService documentParticipantSourceService = getParticipantSourceServiceByType(getType(documentParticipant.getParticipantTypeName()));
            result.put(
                    documentParticipant.getId(),
                    documentParticipantSourceService.getSourceName(documentParticipant.getSourceParticipantId())
            );
            if (documentParticipant.getChildren() != null) {
                result.putAll(getSourceNames(documentParticipant.getChildren()));
            }
        }
        return result;
    }

    public boolean isDocumentSignedByUser(Document document, User user) {
        boolean isSigned = true;
        List<DocumentParticipant> participantsOfUser = getParticipantsOfUser(document, user.getId());
        for (DocumentParticipant participant : participantsOfUser) {
            isSigned = isSigned && participant.isSigned();
        }
        return isSigned;
    }
}
