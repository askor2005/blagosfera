package ru.radom.kabinet.document.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.radom.kabinet.document.web.dto.DocumentUserFieldDto;

import java.util.List;

/**
 * Обёртка с данными для создания страницы предварительного просмотра документа
 * Created by vgusev on 11.04.2016.
 */
@Data
public class PossibleSourceParticipantsDto {

    // ИД источника данных
    private Long documentClassDataSourceId;

    // Тип источника данных
    private ParticipantsTypes documentClassDataSourceType;

    // Наименование источника данных
    private String documentTypeDataSourceName;

    // Список возможных участников системы в истоничнике данных
    private List<PossibleSourceParticipantDto> possibleSourceParticipants;

    // Возможный участник является списком участников
    private boolean listParticipant;

    // Список пользовательских полей истоничка данных
    private List<DocumentUserFieldDto> userFields;
}
