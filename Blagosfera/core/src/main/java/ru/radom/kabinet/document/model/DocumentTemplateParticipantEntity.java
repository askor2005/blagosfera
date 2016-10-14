package ru.radom.kabinet.document.model;

import ru.askor.blagosfera.domain.document.DocumentTemplateParticipant;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 16.07.2015.
 */
@Entity
@Table(name = DocumentTemplateParticipantEntity.TABLE_NAME)
public class DocumentTemplateParticipantEntity extends LongIdentifiable {
    public static final String TABLE_NAME = "documents_template_participants";

    public static class Columns {
        public static final String DOCUMENT_TEMPLATE_ID = "document_template_id";
        public static final String PARTICIPANT_NAME = "participant_name";
        public static final String PARENT_PARTICIPANT_NAME = "parent_participant_name";
    }

    //идентификатор на шаблон
    @JoinColumn(name = Columns.DOCUMENT_TEMPLATE_ID)
    @ManyToOne(fetch = FetchType.LAZY)
    private DocumentTemplateEntity documentTemplate;

    @Column(name = Columns.PARTICIPANT_NAME)
    private String participantName;

    @Column(name = Columns.PARENT_PARTICIPANT_NAME)
    private String parentParticipantName;

    public DocumentTemplateEntity getDocumentTemplate() {
        return documentTemplate;
    }

    public void setDocumentTemplate(DocumentTemplateEntity documentTemplate) {
        this.documentTemplate = documentTemplate;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParentParticipantName() {
        return parentParticipantName;
    }

    public void setParentParticipantName(String parentParticipantName) {
        this.parentParticipantName = parentParticipantName;
    }

    // TODO реализовать
    public DocumentTemplateParticipant toDomain() {
        DocumentTemplateParticipant result = new DocumentTemplateParticipant();
        result.setId(getId());
        result.setParentParticipantName(getParentParticipantName());
        result.setParticipantName(getParticipantName());
        return result;
    }

    public static DocumentTemplateParticipant toDomainSafe(DocumentTemplateParticipantEntity entity) {
        DocumentTemplateParticipant result = null;
        if (entity != null) {
            result = entity.toDomain();
        }
        return result;
    }

    public static List<DocumentTemplateParticipant> toDomainList(List<DocumentTemplateParticipantEntity> entities) {
        List<DocumentTemplateParticipant> result = null;
        if (entities != null) {
            result = new ArrayList<>();
            for (DocumentTemplateParticipantEntity entity : entities) {
                result.add(toDomainSafe(entity));
            }
        }
        return result;
    }
}
