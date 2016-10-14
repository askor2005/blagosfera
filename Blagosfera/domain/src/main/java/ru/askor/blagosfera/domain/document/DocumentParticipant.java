package ru.askor.blagosfera.domain.document;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
@Data
public class DocumentParticipant {

    private Long id;

    /**
     * ИД участника документа из системы в которую внедрён документооборот.
     */
    private Long sourceParticipantId;

    /**
     * Наименование типа участника документа.
     */
    private String participantTypeName;

    /**
     * Наименование типа участника шаблона.
     */
    private String participantTemplateTypeName;

    /**
     * Флаг - пользователь подписал\не подписал документ.
     * Добавление самой подписи происходит после того, как все поля документа будут внесены.
     */
    private boolean isSigned = false;

    /**
     * Дата подписания
     */
    private Date signDate;

    /**
     * Подпись документа.
     */
    private String signature;

    private DocumentParticipant parent;

    // Дочерние участники шаблона документа (например руководство или гл. бухгалтер у юр лица)
    private List<DocumentParticipant> children;

    private boolean isNeedSignDocument = false;

}
