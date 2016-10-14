package ru.radom.kabinet.document.dto;

import ru.radom.kabinet.document.model.DocumentParticipantEntity;

import java.util.Date;

/**
 * Created by Otts Alexey on 20.11.2015.<br/>
 * DTO для {@link DocumentParticipantEntity}
 */
public class FlowOfDocumentParticipantDTO {
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
    private Boolean isSigned = false;

    /**
     * Дата подписания
     */
    private Date signDate;

    /**
     * Подпись документа.
     */
    private String signature;

    /**
     * Должен ли этот участник подписывать документы
     */
    private Boolean isNeedSignDocument = false;

    /**
     * @see #sourceParticipantId
     */
    public Long getSourceParticipantId() {
        return sourceParticipantId;
    }

    /**
     * @see #sourceParticipantId
     */
    public void setSourceParticipantId(Long sourceParticipantId) {
        this.sourceParticipantId = sourceParticipantId;
    }

    /**
     * @see #participantTypeName
     */
    public String getParticipantTypeName() {
        return participantTypeName;
    }

    /**
     * @see #participantTypeName
     */
    public void setParticipantTypeName(String participantTypeName) {
        this.participantTypeName = participantTypeName;
    }

    /**
     * @see #participantTemplateTypeName
     */
    public String getParticipantTemplateTypeName() {
        return participantTemplateTypeName;
    }

    /**
     * @see #participantTemplateTypeName
     */
    public void setParticipantTemplateTypeName(String participantTemplateTypeName) {
        this.participantTemplateTypeName = participantTemplateTypeName;
    }

    /**
     * @see #isSigned
     */
    public Boolean getIsSigned() {
        return isSigned;
    }

    /**
     * @see #isSigned
     */
    public void setIsSigned(Boolean isSigned) {
        this.isSigned = isSigned;
    }

    /**
     * @see #signDate
     */
    public Date getSignDate() {
        return signDate;
    }

    /**
     * @see #signDate
     */
    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    /**
     * @see #signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @see #signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * @see #isNeedSignDocument
     */
    public Boolean getIsNeedSignDocument() {
        return isNeedSignDocument;
    }

    /**
     * @see #isNeedSignDocument
     */
    public void setIsNeedSignDocument(Boolean isNeedSignDocument) {
        this.isNeedSignDocument = isNeedSignDocument;
    }


}
