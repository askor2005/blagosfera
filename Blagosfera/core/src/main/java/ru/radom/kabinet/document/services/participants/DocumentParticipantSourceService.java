package ru.radom.kabinet.document.services.participants;

import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.*;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.document.dto.DocumentParticipantSourceDto;
import ru.radom.kabinet.document.dto.PossibleSourceParticipantDto;

import java.util.List;

/**
 * Интерфейс сервисов источников данных для документооборота
 * Created by vgusev on 07.04.2016.
 */
public interface DocumentParticipantSourceService {

    /**
     * Тип участника который обрабатывает сервис
     * @return
     */
    ParticipantsTypes getType();

    /**
     * Загрузить все данные участника шаблона документа в обёртку с полями для парсинга документа
     * @param documentParticipant
     * @param participantName
     * @param filteredFieldIds
     * @param excludedFieldTypes
     * @param includedFieldTypes
     * @param needFillSystemFields
     * @param index
     * @return
     */
    DocumentParticipantSourceDto getParticipantSource(
            IDocumentParticipant documentParticipant, String participantName, List<Long> filteredFieldIds,
            List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes, boolean needFillSystemFields,
            int index);

    /**
     * Загрузить все данные участника шаблона документа по ИД в обёртку с полями для парсинга документа
     * @param participantId
     * @param participantName
     * @param filteredFieldIds
     * @param excludedFieldTypes
     * @param includedFieldTypes
     * @param needFillSystemFields
     * @param index
     * @return
     */
    DocumentParticipantSourceDto getParticipantSource(Long participantId, String participantName, List<Long> filteredFieldIds,
            List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes, boolean needFillSystemFields,
            int index);

    /**
     * Загрузить изначального участника документа (Пользователь, объединени и т.д.)
     * @param id
     * @return
     */
    IDocumentParticipant getSourceParticipantById(Long id);

    /**
     * Конвертация изначальных участников для парсинга документа в участники документа
     * Например для объединения - создание участника объединения и дочерних участников - директора и т.п.
     *
     * @param documentTemplate
     * @param sourceParticipant
     */
    DocumentParticipant convertSourceParticipantToDocumentParticipant(DocumentTemplate documentTemplate, DocumentParticipantSourceDto sourceParticipant);

    /**
     * Загрузить список пользователей которые имеют право от лица участника документа заполнять пользовательские поля
     *
     * @param documentParticipant
     * @return
     */
    List<User> getUsersFromParticipantForFillUserFields(DocumentParticipant documentParticipant);

    /**
     * Загрузить список пользователей которые должны подписать документ от имени участника документа
     *
     * @param documentParticipant
     * @return
     */
    List<User> getUsersFromParticipantForSignDocument(DocumentParticipant documentParticipant);

    /**
     * Получить список возможных участников документа по источнику данных
     * @param dataSource
     * @return
     */
    List<PossibleSourceParticipantDto> getPossibleSourceParticipants(DocumentClassDataSource dataSource);

    /**
     * Флаг - источник данных предполагает загрузку нескольких объектов как одного участника документа
     * @return
     */
    boolean isListDataSource();

    /**
     * Получить список участников документа для пользователя
     * @param documentParticipant
     * @param userId
     * @return
     */
    List<DocumentParticipant> getParticipantsOfUser(DocumentParticipant documentParticipant, Long userId);

    /**
     * Получить имя участника документа в системе (название объединения, имя пользователя и т.п.)
     * @param sourceParticipantId
     * @return
     */
    String getSourceName(Long sourceParticipantId);
}
