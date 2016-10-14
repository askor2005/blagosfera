package ru.askor.blagosfera.domain.document.templatesettings.dto;

import ru.askor.blagosfera.domain.document.DocumentTemplate;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateParticipantSetting;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 21.07.2016.
 */
public class DocumentTemplateSettingDto {

    public Long id;

    public Long templateId;

    public String name;

    public List<DocumentTemplateParticipantSettingDto> participants;

    public DocumentTemplateSettingDto() {}

    public DocumentTemplateSettingDto(DocumentTemplateSetting documentTemplateSetting) {
        id = documentTemplateSetting.getId();
        templateId = documentTemplateSetting.getDocumentTemplate().getId();
        name = documentTemplateSetting.getDocumentTemplate().getName();
        participants = DocumentTemplateParticipantSettingDto.toDtoList(documentTemplateSetting.getDocumentTemplateParticipantSettings());
    }

    public static List<DocumentTemplateSettingDto> toDtoList(List<DocumentTemplateSetting> documentTemplateSettings) {
        List<DocumentTemplateSettingDto> result = null;
        if (documentTemplateSettings != null && !documentTemplateSettings.isEmpty()) {
            result = new ArrayList<>();
            for (DocumentTemplateSetting documentTemplateSetting : documentTemplateSettings) {
                result.add(new DocumentTemplateSettingDto(documentTemplateSetting));
            }
        }
        return result;
    }

    public DocumentTemplateSetting toDomain() {
        DocumentTemplateSetting result = new DocumentTemplateSetting();

        DocumentTemplate documentTemplate = new DocumentTemplate();
        documentTemplate.setId(templateId);
        documentTemplate.setName(name);

        result.setId(id);
        result.setDocumentTemplate(documentTemplate);
        result.setDocumentTemplateParticipantSettings(DocumentTemplateParticipantSettingDto.toDomainList(participants));
        return result;
    }

    public static List<DocumentTemplateSetting> toDomainList(List<DocumentTemplateSettingDto> documentTemplateSettings) {
        List<DocumentTemplateSetting> result = null;
        if (documentTemplateSettings != null && !documentTemplateSettings.isEmpty()) {
            result = new ArrayList<>();
            for (DocumentTemplateSettingDto documentTemplateSetting : documentTemplateSettings) {
                result.add(documentTemplateSetting.toDomain());
            }
        }
        return result;
    }
}
