package ru.radom.kabinet.model.communities.postappointbehavior.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityPostRequest;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentFolder;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.radom.kabinet.document.services.DocumentFolderDataService;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.model.communities.postappointbehavior.IPostAppointBehavior;
import ru.radom.kabinet.model.communities.postappointbehavior.impl.settings.PostDocumentCustomSourceHandler;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.CommunityPostRequestDomainService;
import ru.radom.kabinet.services.document.DocumentTemplateSettingService;
import ru.radom.kabinet.utils.SpringUtils;

import java.util.*;

/**
 *
 * Created by vgusev on 02.05.2016.
 */
@Service(DocumentPostAppointBehavior.NAME)
@Transactional
public class DocumentPostAppointBehavior/* extends BasePostAppointBehavior*/ implements IPostAppointBehavior/*, ApplicationListener<ApplicationEvent>*/ {

    public static final String NAME = "documentPostAppointBehavior";

    // Параметр документа - ИД запроса назначения на должность.
    private static final String COMMUNITY_POST_REQUEST_ID = "COMMUNITY_POST_REQUEST_ID";

    // Параметр документа - .
    private static final String DOCUMENT_APPOINT_TO_COMMUNITY_POST = "DOCUMENT_APPOINT_TO_COMMUNITY_POST";

    @Override
        public PostAppointData start(CommunityPostRequest communityPostRequest) {
        // Создаём документ по шаблону
        DocumentFolder documentFolder = createDocuments(communityPostRequest);
        return new PostAppointData(
                communityPostRequest.getCommunity(),
                PostAppointResultType.DOCUMENT,
                documentFolder
        );
    }


    @Autowired
    private CommunityDataService communityDataService;

    @Autowired
    private CommunityPostRequestDomainService communityPostRequestDomainService;

    @Autowired
    private DocumentFolderDataService documentFolderDataService;

    @Autowired
    private DocumentTemplateSettingService documentTemplateSettingService;

    @Autowired
    private DocumentService documentService;

    /*@Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof FlowOfDocumentStateEvent) {
            FlowOfDocumentStateEvent documentStateEvent = (FlowOfDocumentStateEvent) event;
            switch (documentStateEvent.getStateEventType()) {
                case DOCUMENT_SIGNED: { // Документ подписан всеми
                    if (documentStateEvent.getParameters() != null) {
                        if (documentStateEvent.getParameters().containsKey(COMMUNITY_POST_REQUEST_ID) &&
                                documentStateEvent.getParameters().containsKey(DOCUMENT_APPOINT_TO_COMMUNITY_POST)) {
                            Long postRequestId = VarUtils.getLong(documentStateEvent.getParameters().get(COMMUNITY_POST_REQUEST_ID), null);
                            if (postRequestId != null) {
                                CommunityPostRequest communityPostRequest = communityPostRequestDomainService.getById(postRequestId);

                                boolean isAllDocsSigned = false;
                                if (documentStateEvent.getDocument().getDocumentFolder() != null) {
                                    DocumentFolder documentFolder = documentFolderDataService.getById(documentStateEvent.getDocument().getDocumentFolder().getId());
                                    for (Document document : documentFolder.getDocuments()) {
                                        if (!document.getId().equals(documentStateEvent.getDocument().getId())) {
                                            isAllDocsSigned = isAllDocsSigned && documentService.isSignedDocument(document.getId());
                                        }
                                        if (!isAllDocsSigned) {
                                            break;
                                        }
                                    }

                                    *//*for (Document document : documentStateEvent.getDocument().getDocumentFolder()) {

                                    }*//*
                                }


                                appointMemberToPost(communityPostRequest);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }*/

    private DocumentFolder createDocuments(CommunityPostRequest communityPostRequest) {
        PostDocumentCustomSourceHandler postDocumentCustomSourceHandler = SpringUtils.getBean(
                "postDocumentCustomSourceHandler",
                communityPostRequest.getReceiver().getUser().getId(),
                communityPostRequest.getCommunity().getId()
        );

        Map<String, String> documentParameters = new HashMap<>();
        documentParameters.put(DOCUMENT_APPOINT_TO_COMMUNITY_POST, Boolean.TRUE.toString());
        documentParameters.put(COMMUNITY_POST_REQUEST_ID, String.valueOf(communityPostRequest.getId()));

        List<DocumentTemplateSetting> documentTemplateSettings = communityPostRequest.getCommunityPost().getDocumentTemplateSettings();
        Set<Document> documents = new HashSet<>();
        for (DocumentTemplateSetting documentTemplateSetting : documentTemplateSettings) {
            Document document = documentTemplateSettingService.createDocument(
                    documentTemplateSetting,
                    communityPostRequest.getSender().getUser().getId(),
                    postDocumentCustomSourceHandler,
                    documentParameters
            );
            documents.add(document);
        }

        DocumentFolder documentFolder = new DocumentFolder();
        documentFolder.setDescription("");
        documentFolder.setName("");
        documentFolder.setDocuments(documents);

        documentFolder = documentFolderDataService.save(documentFolder);

        return documentFolder;

        // Анализируем шаблон. Участник физ лицо - пользователь которого принимают в объединение
        /*ExceptionUtils.check(communityPostRequest.getCommunityPost().getDocumentTemplate() == null,
                "Не найден шаблон документа который нужно подписывать для вступления на должность");

        DocumentClass documentClass = communityPostRequest.getCommunityPost().getDocumentTemplate().getDocumentClass();
        List<DocumentClassDataSource> documentClassDataSources = documentClass.getDataSources();

        ExceptionUtils.check(documentClassDataSources == null || documentClassDataSources.isEmpty(),
                "Не найдены участники класса документа который нужно подписывать для вступления на должность");*/

        /*DocumentClassDataSource appointedDataSource = null;
        DocumentClassDataSource communityDataSource = null;
        for (DocumentClassDataSource documentClassDataSource : documentClassDataSources) {
            if (appointedDataSource == null && ParticipantsTypes.INDIVIDUAL.equals(documentClassDataSource.getType())) {
                appointedDataSource = documentClassDataSource;
            }
            if (communityDataSource == null &&
                    (ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.equals(documentClassDataSource.getType()) ||
                            ParticipantsTypes.COMMUNITY_WITHOUT_ORGANIZATION.equals(documentClassDataSource.getType()))
                    ) {
                communityDataSource = documentClassDataSource;
            }
            if (appointedDataSource != null && communityDataSource != null) {
                break;
            }
        }
        ExceptionUtils.check(appointedDataSource == null, "Не найден участник документа - участник принимаемый на должность");

        // Участники документа
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        //-------------------------------------------------------------
        // Принимаемый участник
        //-------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(
                ParticipantsTypes.INDIVIDUAL.getName(),
                communityPostRequest.getReceiver().getUser().getId(),
                appointedDataSource.getName()
        );
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Объединение
        //-------------------------------------------------------------
        if (communityDataSource != null) {
            Community community = communityDataService.getByIdFullData(communityPostRequest.getCommunity().getId());
            String communityParticipantType = ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName();
            if (community.getCommunityType() != null) {
                communityParticipantType = community.getCommunityType().getName();
            }

            participantParameter = new ParticipantCreateDocumentParameter(
                    communityParticipantType,
                    communityPostRequest.getCommunity().getId(),
                    communityDataSource.getName()
            );
            userFieldValues = new ArrayList<>();
            createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        }
        //-------------------------------------------------------------

        DocumentTemplate documentTemplate = communityPostRequest.getCommunityPost().getDocumentTemplate();
        ExceptionUtils.check(StringUtils.isEmpty(documentTemplate.getCode()), "Не уставнолен код шаблона документа");

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(DOCUMENT_APPOINT_TO_COMMUNITY_POST, "true");
        parameters.put(COMMUNITY_POST_REQUEST_ID, String.valueOf(communityPostRequest.getId()));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        return documentService.createDocumentDomain(documentTemplate.getCode(), createDocumentParameters, DocumentEntity.SYSTEM_CREATOR_ID, Collections.singletonList(stateEvent));*/
    }
}
 