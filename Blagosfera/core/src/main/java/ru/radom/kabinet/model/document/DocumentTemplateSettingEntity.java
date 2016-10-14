package ru.radom.kabinet.model.document;

import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность настроек создания документа на основе шаблона
 * Created by vgusev on 15.07.2016.
 */
@Entity
@Table(name = "document_template_settings")
public class DocumentTemplateSettingEntity extends LongIdentifiable {

    @JoinColumn(name = "template_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private DocumentTemplateEntity documentTemplate;

    @OneToMany(mappedBy = "documentTemplateSetting", fetch = FetchType.LAZY)
    private List<DocumentTemplateParticipantSettingEntity> documentTemplateParticipantSettings;

    public List<DocumentTemplateParticipantSettingEntity> getDocumentTemplateParticipantSettings() {
        return documentTemplateParticipantSettings;
    }

    public void setDocumentTemplateParticipantSettings(List<DocumentTemplateParticipantSettingEntity> documentTemplateParticipantSettings) {
        this.documentTemplateParticipantSettings = documentTemplateParticipantSettings;
    }

    public DocumentTemplateEntity getDocumentTemplate() {
        return documentTemplate;
    }

    public void setDocumentTemplate(DocumentTemplateEntity documentTemplate) {
        this.documentTemplate = documentTemplate;
    }

    public DocumentTemplateSetting toDomain() {
        DocumentTemplateSetting result = new DocumentTemplateSetting();
        result.setId(getId());
        result.setDocumentTemplate(documentTemplate.toDomain(false));
        result.getDocumentTemplateParticipantSettings().addAll(DocumentTemplateParticipantSettingEntity.toDomainList(documentTemplateParticipantSettings));
        return result;
    }

    public static DocumentTemplateSetting toDomainSafe(DocumentTemplateSettingEntity entity) {
        DocumentTemplateSetting result = null;
        if (entity != null) {
            result = entity.toDomain();
        }
        return result;
    }

    public static List<DocumentTemplateSetting> toDomainList(List<DocumentTemplateSettingEntity> documentTemplateSettings) {
        List<DocumentTemplateSetting> result = null;
        if (documentTemplateSettings != null && !documentTemplateSettings.isEmpty()) {
            result = new ArrayList<>();
            for (DocumentTemplateSettingEntity documentTemplateSettingEntity : documentTemplateSettings) {
                result.add(toDomainSafe(documentTemplateSettingEntity));
            }
        }
        return result;
    }
}
