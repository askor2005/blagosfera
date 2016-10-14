package ru.radom.kabinet.services.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentClassDataSourceRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentTemplateParticipantSettingRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentTemplateRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentTemplateSettingRepository;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateParticipantSetting;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSettingCustomSourceHandler;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEventType;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter;
import ru.radom.kabinet.document.model.DocumentClassDataSourceEntity;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.model.document.DocumentTemplateParticipantSettingEntity;
import ru.radom.kabinet.model.document.DocumentTemplateSettingEntity;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 15.07.2016.
 */
@Service
@Transactional
public class DocumentTemplateSettingServiceImpl implements DocumentTemplateSettingService {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentTemplateSettingRepository documentTemplateSettingRepository;

    @Autowired
    private DocumentTemplateRepository documentTemplateRepository;

    @Autowired
    private DocumentClassDataSourceRepository documentClassDataSourceRepository;

    @Autowired
    private DocumentTemplateParticipantSettingRepository documentTemplateParticipantSettingRepository;

    @Override
    public DocumentTemplateSetting getById(Long id) {
        return DocumentTemplateSettingEntity.toDomainSafe(documentTemplateSettingRepository.findOne(id));
    }

    @Override
    public List<DocumentTemplateSetting> getByIds(List<Long> ids) {
        return DocumentTemplateSettingEntity.toDomainList(documentTemplateSettingRepository.findAll(ids));
    }

    @Override
    public Document createDocument(DocumentTemplateSetting documentTemplateSetting, Long creatorId, DocumentTemplateSettingCustomSourceHandler documentTemplateSettingCustomSourceHandler, Map<String, String> documentParameters) {
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();
        List<DocumentTemplateParticipantSetting> documentTemplateParticipantSettings = documentTemplateSetting.getDocumentTemplateParticipantSettings();

        for (DocumentTemplateParticipantSetting documentTemplateParticipantSetting : documentTemplateParticipantSettings) {
            Long sourceId = null;

            switch (documentTemplateParticipantSetting.getDocumentTemplateParticipantType()) {
                case CUSTOM:
                    sourceId = documentTemplateSettingCustomSourceHandler.handleCustomSource(documentTemplateParticipantSetting.getSourceName());
                    break;
                case DEFAULT:
                    sourceId = documentTemplateParticipantSetting.getSourceId();
                    break;
            }

            ParticipantCreateDocumentParameter participantParameter =
                    new ParticipantCreateDocumentParameter(
                            documentTemplateParticipantSetting.getDocumentClassDataSource().getType().getName(),
                            sourceId,
                            documentTemplateParticipantSetting.getDocumentClassDataSource().getName()
                    );

            createDocumentParameters.add(new CreateDocumentParameter(
                    participantParameter, Collections.emptyList()
            ));
        }

        Map<String, String> parameters;
        if (documentParameters != null) {
            parameters = documentParameters;
        } else {
            parameters = Collections.emptyMap();
        }
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        return documentService.createDocumentDomain(
                documentTemplateSetting.getDocumentTemplate().getId(),
                createDocumentParameters,
                creatorId, Collections.singletonList(stateEvent), null, true, null, true, true);
    }

    @Override
    public DocumentTemplateSetting save(DocumentTemplateSetting documentTemplateSetting) {
        ExceptionUtils.check(documentTemplateSetting == null, "Не передан объект настроек шаблона документа");
        DocumentTemplateSettingEntity entity;
        if (documentTemplateSetting.getId() == null) {
            entity = new DocumentTemplateSettingEntity();
        } else {
            entity = documentTemplateSettingRepository.getOne(documentTemplateSetting.getId());
        }
        DocumentTemplateEntity documentTemplateEntity = documentTemplateRepository.getOne(documentTemplateSetting.getDocumentTemplate().getId());
        entity.setDocumentTemplate(documentTemplateEntity);
        entity = documentTemplateSettingRepository.save(entity);


        List<DocumentTemplateParticipantSettingEntity> documentTemplateParticipantSettingEntities = new ArrayList<>();
        for (DocumentTemplateParticipantSetting documentTemplateParticipantSetting : documentTemplateSetting.getDocumentTemplateParticipantSettings()) {
            DocumentTemplateParticipantSettingEntity documentTemplateParticipantSettingEntity;
            if (documentTemplateParticipantSetting.getId() != null) {
                documentTemplateParticipantSettingEntity = documentTemplateParticipantSettingRepository.getOne(documentTemplateParticipantSetting.getId());
            } else {
                documentTemplateParticipantSettingEntity = new DocumentTemplateParticipantSettingEntity();
            }
            DocumentClassDataSourceEntity documentClassDataSourceEntity = documentClassDataSourceRepository.getOne(documentTemplateParticipantSetting.getDocumentClassDataSource().getId());

            documentTemplateParticipantSettingEntity.setParticipantSourceName(documentTemplateParticipantSetting.getParticipantSourceName());
            documentTemplateParticipantSettingEntity.setDocumentClassDataSource(documentClassDataSourceEntity);
            documentTemplateParticipantSettingEntity.setDocumentTemplateParticipantType(documentTemplateParticipantSetting.getDocumentTemplateParticipantType());
            documentTemplateParticipantSettingEntity.setSourceId(documentTemplateParticipantSetting.getSourceId());
            documentTemplateParticipantSettingEntity.setSourceName(documentTemplateParticipantSetting.getSourceName());
            documentTemplateParticipantSettingEntity.setDocumentTemplateSetting(entity);

            documentTemplateParticipantSettingEntity = documentTemplateParticipantSettingRepository.save(documentTemplateParticipantSettingEntity);
            documentTemplateParticipantSettingEntities.add(documentTemplateParticipantSettingEntity);
        }
        entity.setDocumentTemplateParticipantSettings(documentTemplateParticipantSettingEntities);
        entity = documentTemplateSettingRepository.save(entity);
        return entity.toDomain();
    }
}
