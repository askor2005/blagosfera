package ru.radom.kabinet.model.document;

import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateParticipantSetting;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateParticipantType;
import ru.radom.kabinet.document.model.DocumentClassDataSourceEntity;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 15.07.2016.
 */
@Entity
@Table(name = "document_template_participant_settings")
public class DocumentTemplateParticipantSettingEntity extends LongIdentifiable {

    @JoinColumn(name = "data_source_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private DocumentClassDataSourceEntity documentClassDataSource;

    @Column(name="type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentTemplateParticipantType documentTemplateParticipantType;

    @JoinColumn(name = "setting_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private DocumentTemplateSettingEntity documentTemplateSetting;

    @Column(name = "source_name", length = 1000)
    private String sourceName;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "participant_source_name", length = 10000)
    private String participantSourceName;

    public DocumentClassDataSourceEntity getDocumentClassDataSource() {
        return documentClassDataSource;
    }

    public void setDocumentClassDataSource(DocumentClassDataSourceEntity documentClassDataSource) {
        this.documentClassDataSource = documentClassDataSource;
    }

    public DocumentTemplateParticipantType getDocumentTemplateParticipantType() {
        return documentTemplateParticipantType;
    }

    public void setDocumentTemplateParticipantType(DocumentTemplateParticipantType documentTemplateParticipantType) {
        this.documentTemplateParticipantType = documentTemplateParticipantType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getParticipantSourceName() {
        return participantSourceName;
    }

    public void setParticipantSourceName(String participantSourceName) {
        this.participantSourceName = participantSourceName;
    }

    public DocumentTemplateSettingEntity getDocumentTemplateSetting() {
        return documentTemplateSetting;
    }

    public void setDocumentTemplateSetting(DocumentTemplateSettingEntity documentTemplateSetting) {
        this.documentTemplateSetting = documentTemplateSetting;
    }

    public DocumentTemplateParticipantSetting toDomain() {
        DocumentTemplateParticipantSetting result = new DocumentTemplateParticipantSetting();
        result.setId(getId());
        result.setDocumentClassDataSource(documentClassDataSource.toDomain());
        result.setDocumentTemplateParticipantType(documentTemplateParticipantType);
        result.setSourceId(sourceId);
        result.setSourceName(sourceName);
        result.setParticipantSourceName(participantSourceName);
        return result;
    }

    public static List<DocumentTemplateParticipantSetting> toDomainList(List<DocumentTemplateParticipantSettingEntity> documentTemplateParticipantSettings) {
        List<DocumentTemplateParticipantSetting> result = null;
        if (documentTemplateParticipantSettings != null && !documentTemplateParticipantSettings.isEmpty()) {
            result = new ArrayList<>();
            for (DocumentTemplateParticipantSettingEntity entity : documentTemplateParticipantSettings) {
                result.add(entity.toDomain());
            }
        }
        return result;
    }
}
