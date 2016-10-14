package ru.askor.blagosfera.domain.document.templatesettings.dto;

import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.DocumentClassDataSource;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateParticipantSetting;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateParticipantType;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 21.07.2016.
 */
public class DocumentTemplateParticipantSettingDto {

    public Long id;
    public Long participantId;
    public String name;// : "Физ лицо",
    public ParticipantsTypes type;// : "INDIVIDUAL",
    public DocumentTemplateParticipantType sourceType;// : "DEFAULT",
    public String sourceName;//"Принимаемый участник",
    public Long sourceId;
    public String participantSourceName;

    public DocumentTemplateParticipantSettingDto() {}

    public DocumentTemplateParticipantSettingDto(DocumentTemplateParticipantSetting documentTemplateParticipantSetting) {
        id = documentTemplateParticipantSetting.getId();
        participantId = documentTemplateParticipantSetting.getDocumentClassDataSource().getId();
        name = documentTemplateParticipantSetting.getDocumentClassDataSource().getName();
        type = documentTemplateParticipantSetting.getDocumentClassDataSource().getType();
        sourceType = documentTemplateParticipantSetting.getDocumentTemplateParticipantType();
        sourceName = documentTemplateParticipantSetting.getSourceName();
        sourceId = documentTemplateParticipantSetting.getSourceId();
        participantSourceName = documentTemplateParticipantSetting.getParticipantSourceName();
    }

    public static List<DocumentTemplateParticipantSettingDto> toDtoList(List<DocumentTemplateParticipantSetting> documentTemplateParticipantSettings) {
        List<DocumentTemplateParticipantSettingDto> result = null;
        if (documentTemplateParticipantSettings != null && !documentTemplateParticipantSettings.isEmpty()) {
            result = new ArrayList<>();
            for (DocumentTemplateParticipantSetting documentTemplateParticipantSetting : documentTemplateParticipantSettings) {
                result.add(new DocumentTemplateParticipantSettingDto(documentTemplateParticipantSetting));
            }
        }
        return result;
    }

    public DocumentTemplateParticipantSetting toDomain() {
        DocumentClassDataSource documentClassDataSource = new DocumentClassDataSource();
        documentClassDataSource.setId(participantId);
        documentClassDataSource.setName(name);

        DocumentTemplateParticipantSetting result = new DocumentTemplateParticipantSetting();
        result.setId(id);
        result.setParticipantSourceName(participantSourceName);
        result.setSourceName(sourceName);
        result.setSourceId(sourceId);
        result.setDocumentTemplateParticipantType(sourceType);
        result.setDocumentClassDataSource(documentClassDataSource);
        return result;
    }

    public static List<DocumentTemplateParticipantSetting> toDomainList(List<DocumentTemplateParticipantSettingDto> documentTemplateParticipantSettings) {
        List<DocumentTemplateParticipantSetting> result = null;
        if (documentTemplateParticipantSettings != null && !documentTemplateParticipantSettings.isEmpty()) {
            result = new ArrayList<>();
            for (DocumentTemplateParticipantSettingDto documentTemplateParticipantSetting : documentTemplateParticipantSettings) {
                result.add(documentTemplateParticipantSetting.toDomain());
            }
        }
        return result;
    }
}
