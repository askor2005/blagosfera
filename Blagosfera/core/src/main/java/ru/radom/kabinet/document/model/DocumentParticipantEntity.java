package ru.radom.kabinet.document.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.BooleanUtils;
import ru.askor.blagosfera.domain.document.DocumentParticipant;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vgusev on 15.06.2015.
 * Класс - сущность участника документа.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "flowOfDocumentParticipant")
public class DocumentParticipantEntity extends LongIdentifiable {

    /**
     * ИД участника документа из системы в которую внедрён документооборот.
     */
    @Column(name = "source_participant_id", nullable = false)
    private Long sourceParticipantId;

    /**
     * Наименование типа участника документа.
     * Значение как в enum ParticipantTypes
     */
    @Column(name = "participant_type_name", nullable = false)
    private String participantTypeName;

    /**
     * Наименование типа участника шаблона.
     *
     */
    @Column(name = "participant_template_type_name")
    private String participantTemplateTypeName;

    /**
     * Документ.
     */
    @JoinColumn(name = "document_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private DocumentEntity document;

    /**
     * Флаг - пользователь подписал\не подписал документ.
     * Добавление самой подписи происходит после того, как все поля документа будут внесены.
     */
    @Column(name = "is_signed")
    private Boolean isSigned = false;

    /**
     * Дата подписания
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sign_date")
    private Date signDate;

    /**
     * Подпись документа.
     */
    @Column(name = "signature", length = 1000000)
    private String signature;

    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private DocumentParticipantEntity parent;

    // Дочерние участники шаблона документа (например руководство или гл. бухгалтер у юр лица)
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<DocumentParticipantEntity> children = new ArrayList<>();

    @Column(name = "need_sign_document")
    private Boolean isNeedSignDocument = false;

    public Long getSourceParticipantId() {
        return sourceParticipantId;
    }

    public void setSourceParticipantId(Long sourceParticipantId) {
        this.sourceParticipantId = sourceParticipantId;
    }

    public String getParticipantTypeName() {
        return participantTypeName;
    }

    public void setParticipantTypeName(String participantTypeName) {
        this.participantTypeName = participantTypeName;
    }

    public String getParticipantTemplateTypeName() {
        return participantTemplateTypeName;
    }

    public void setParticipantTemplateTypeName(String participantTemplateTypeName) {
        this.participantTemplateTypeName = participantTemplateTypeName;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public Boolean getIsSigned() {
        return isSigned;
    }

    public void setIsSigned(Boolean isSigned) {
        this.isSigned = isSigned;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public DocumentParticipantEntity getParent() {
        return parent;
    }

    public void setParent(DocumentParticipantEntity parent) {
        this.parent = parent;
    }

    public List<DocumentParticipantEntity> getChildren() {
        return children;
    }

    public void setChildren(List<DocumentParticipantEntity> children) {
        this.children = children;
    }

    public Boolean getIsNeedSignDocument() {
        return isNeedSignDocument;
    }

    public void setIsNeedSignDocument(Boolean isNeedSignDocument) {
        this.isNeedSignDocument = isNeedSignDocument;
    }

    public DocumentParticipant toDomain(boolean withParent, boolean withChild) {
        DocumentParticipant result = new DocumentParticipant();
        result.setId(getId());
        result.setSourceParticipantId(getSourceParticipantId());
        result.setParticipantTypeName(getParticipantTypeName());
        result.setParticipantTemplateTypeName(getParticipantTemplateTypeName());
        result.setSigned(BooleanUtils.toBooleanDefaultIfNull(getIsSigned(), false));
        result.setSignDate(getSignDate());
        result.setSignature(getSignature());
        if (withParent && getParent() != null) {
            result.setParent(getParent().toDomain(false, false));
        }
        if (withChild && getChildren() != null) {
            result.setChildren(toDomainList(getChildren(), false, false));
        }
        result.setNeedSignDocument(BooleanUtils.toBooleanDefaultIfNull(getIsNeedSignDocument(), false));
        return result;
    }

    public static DocumentParticipant toDomainSafe(DocumentParticipantEntity entity, boolean withParent, boolean withChild) {
        DocumentParticipant result = null;
        if (entity != null) {
            result = entity.toDomain(withParent, withChild);
        }
        return result;
    }

    public static List<DocumentParticipant> toDomainList(List<DocumentParticipantEntity> entities, boolean withParent, boolean withChild) {
        List<DocumentParticipant> result = null;
        if (entities != null) {
            result = new ArrayList<>();
            for (DocumentParticipantEntity entity : entities) {
                result.add(toDomainSafe(entity, withParent, withChild));
            }
        }
        return result;
    }

}
