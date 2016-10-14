package ru.radom.kabinet.document.services;

import ru.askor.blagosfera.domain.document.*;
import ru.askor.blagosfera.domain.document.userfields.DocumentUserField;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEvent;
import ru.radom.kabinet.document.dto.FlowOfDocumentDTO;
import ru.radom.kabinet.document.dto.PossibleSourceParticipantsDto;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.model.*;
import ru.radom.kabinet.document.web.dto.DocumentClassParticipantDto;
import ru.radom.kabinet.document.web.dto.DocumentPageDto;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;

import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by Maxim Nikitin on 28.03.2016.
 */
public interface DocumentService {

    /**
     * Получить список источников данных шаблона, которые используются в нём
     * @param id ИД шаблона
     * @return
     */
    List<DocumentClassDataSource> getUsedInTemplateClassParticipants(Long id);

    /**
     * Документ имеет не заполненные пользовательские поля участника
     *
     * @param document
     * @param participant
     * @return
     */
    boolean isDocumentHasUserFields(Document document, DocumentParticipant participant);

    /**
     * Документ имеет незаполненные пользовательские поля
     *
     * @param document
     * @return
     */
    boolean isDocumentHasUserFields(Document document);

    /**
     * Получить список пользовательских полей документа для текущего пользователя по участникам пользователя.
     *
     * @param documentId
     * @param userId
     * @return
     */
    Map<DocumentParticipant, List<DocumentUserField>> getDocumentUserFields(Long documentId, Long userId);

    /**
     * Получить список всех пользовательских полей в шаблоне документа
     *
     * @param templateId
     * @return
     */
    List<DocumentUserField> getUserFieldsByTemplateId(Long templateId);

    /**
     * Сохранить пользовательские поля в документ
     * @param documentUserFields
     * @param userId
     */
    void saveUserFieldsInDocument(Long documentId, List<DocumentUserField> documentUserFields, Long userId);

    /**
     * Права доступа к документу у текущего пользователя
     *
     * @return
     */
    Map<DocumentParticipant, List<ParticipantRight>> getRightToDocument(Long documentId, Long userId);

    /**
     * Подписать документы текущим пользователем
     *
     * @param documentIds
     * @param userId
     */
    void signDocuments(List<Long> documentIds, Long userId);

    /**
     * Подписать документ заданным пользователем
     *
     * @param documentId
     * @param userId
     */
    void signDocument(Long documentId, Long userId);

    void signDocument(Document document, Long userId);

    /**
     * Отказаться от подписи документа
     *
     * @param documentId ИД документа
     * @param userId     ИД пользователя
     */
    void unSignDocument(Long documentId, Long userId);

    /**
     * Проверить подпись участника документа
     *
     * @param participantId ИД участника
     */
    void checkParticipantSignature(Long participantId);

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param stateEvents              список событий, которые выполнятся при их наступлении
     * @return
     */
    @Deprecated
    DocumentEntity createDocument(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents);

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param stateEvents              список событий, которые выполнятся при их наступлении
     * @return
     */
    Document createDocumentDomain(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents);

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param stateEvents              список событий, которые выполнятся при их наступлении
     * @param notifySignEvent          отправлять или нет уведомление о необходимости подписания документа
     * @return
     */
    @Deprecated
    DocumentEntity createDocument(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, boolean notifySignEvent);

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param stateEvents              список событий, которые выполнятся при их наступлении
     * @param notifySignEvent          отправлять или нет уведомление о необходимости подписания документа
     * @return
     */
    Document createDocumentDomain(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, boolean notifySignEvent);

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     */
    @Deprecated
    DocumentEntity createDocument(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId);

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param documentOwnerId
     * @param stateEvents
     * @param expiredDate
     * @return
     */
    @Deprecated
    DocumentEntity createDocument(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate);

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param documentOwnerId
     * @param stateEvents
     * @param expiredDate
     * @param notifySignEvent          отправлять или нет уведомления о необходимости подписания документа
     * @return
     */
    @Deprecated
    DocumentEntity createDocument(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate, boolean notifySignEvent);

    /**
     * Создать и заполнить документ полями пользователей.
     *
     * @param templateCode
     * @param createDocumentParameters
     * @param documentOwnerId
     * @param stateEvents
     * @param expiredDate
     * @param notifySignEvent
     * @param scriptVars
     * @return
     */
    @Deprecated
    DocumentEntity createDocument(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate, boolean notifySignEvent, Map<String, Object> scriptVars);


    Document createDocumentDomain(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate, boolean notifySignEvent, Map<String, Object> scriptVars);

    Document createDocumentDomain(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate, boolean notifySignEvent, Map<String, Object> scriptVars, boolean canUnsignDocument);

    Document createDocumentDomain(Long templateId, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate, boolean notifySignEvent, Map<String, Object> scriptVars, boolean canUnsignDocument, boolean needSignByEDS);

    Document createDocumentDomain(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Long documentOwnerId, List<FlowOfDocumentStateEvent> stateEvents, Date expiredDate, boolean notifySignEvent, Map<String, Object> scriptVars, boolean canUnsignDocument, boolean needSignByEDS);

    /**
     * Установить активность документа
     *
     * @param documentId
     * @param isActive
     */
    void setActiveDocument(Long documentId, boolean isActive);

    /**
     * Создать обёртку документа по параметрам
     *
     * @param templateCode
     * @param createDocumentParameters
     * @return
     */
    FlowOfDocumentDTO generateDocumentDTO(String templateCode, List<CreateDocumentParameter> createDocumentParameters);

    /**
     * Создать обёртку документа по параметрам
     *
     * @param templateCode
     * @param createDocumentParameters
     * @return
     * @paran scriptVars
     */
    FlowOfDocumentDTO generateDocumentDTO(String templateCode, List<CreateDocumentParameter> createDocumentParameters, Map<String, Object> scriptVars);

    /**
     * Создать обёртку документа по параметрам
     *
     * @param templateId
     * @param createDocumentParameters
     * @return
     */
    FlowOfDocumentDTO generateDocumentDTO(Long templateId, List<CreateDocumentParameter> createDocumentParameters);

    /**
     * Создать обёртку документа по параметрам
     *
     * @param templateId
     * @param createDocumentParameters
     * @param scriptVars
     * @return
     */
    FlowOfDocumentDTO generateDocumentDTO(Long templateId, List<CreateDocumentParameter> createDocumentParameters, Map<String, Object> scriptVars);

    /**
     * Сгенерировать обёртку документа при помощи скрипта
     *
     * @param script
     * @param resultVarName
     * @param scriptVariables
     * @return
     * @throws ScriptException
     */
    FlowOfDocumentDTO generateDocumentDTOByScript(String script, String resultVarName, Map<String, Object> scriptVariables) throws ScriptException;

    /**
     * Загузить возможных участников шаблона документа
     * @param templateId ИД шаблона документа
     * @return список возможных участников
     */
    List<PossibleSourceParticipantsDto> getPossibleParticipants(Long templateId);

    /**
     * Получить данные для страницы документа
     * @param documentHashCode
     * @param userId
     * @return
     */
    DocumentPageDto getDocumentPageDto(String documentHashCode, Long userId);

    DocumentEntity createSharerToCommunityMoveDocument(UserEntity fromUserEntity, CommunityEntity toCommunity, BigDecimal amount, List<FlowOfDocumentStateEvent> stateEvents, Long userId);
    DocumentEntity createCommunityToSharerMoveDocument(CommunityEntity fromCommunity, UserEntity toUserEntity, BigDecimal amount, List<FlowOfDocumentStateEvent> stateEvents, Long userId);
    DocumentEntity createCommunityToCommunityMoveDocument(CommunityEntity fromCommunity, CommunityEntity toCommunity, BigDecimal amount, List<FlowOfDocumentStateEvent> stateEvents, Long userId);


    boolean isSignedDocument(Long documentId);
    boolean isSignedDocument(Document document);
}
