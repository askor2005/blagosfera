package ru.radom.kabinet.model.communities.postappointbehavior.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityPostRequest;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEventType;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter;
import ru.radom.kabinet.document.generator.UserFieldValue;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.model.communities.postappointbehavior.IPostAppointBehavior;
import ru.radom.kabinet.model.communities.postappointbehavior.impl.settings.PlotBuhgalterPostSettings;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.CommunityPostRequestDomainService;

import java.util.*;

/**
 * Класс - поведение назначения на пост бухгалтера КУч
 * Created by vgusev on 28.08.2015.
 */
@Service(PlotBuhgalterPostAppointBahavior.NAME)
@Transactional
public class PlotBuhgalterPostAppointBahavior/* extends BasePostAppointBehavior*/ implements IPostAppointBehavior {

    public static final String NAME = "plotBuhgalterPostAppointBahavior";

    @Override
    public PostAppointData start(CommunityPostRequest communityPostRequest) {
        // TODO Переделать
        // Создаём заявление от бухгатера на назначение на должность
        Document document = createStatementFromBuhgalter(communityPostRequest);
        return new PostAppointData(
                communityPostRequest.getCommunity(),
                PostAppointResultType.DOCUMENT/*,
                Collections.singletonList(document)*/
        );
    }

    @Autowired
    private CommunityPostRequestDomainService communityPostRequestService;

    @Autowired
    private CommunityDataService communityDataService;

    /*@EventListener
    public void onFlowOfDocumentStateEvent(FlowOfDocumentStateEvent event) {
        switch (event.getStateEventType()) {
            case DOCUMENT_SIGNED: { // Документ подписан всеми
                if (event.getParameters() != null) {
                    CommunityPostRequest communityPostRequest = null;
                    if (event.getParameters().containsKey(COMMUNITY_POST_REQUEST_ID)) {
                        Long postRequestId = VarUtils.getLong(event.getParameters().get(COMMUNITY_POST_REQUEST_ID), null);
                        if (postRequestId != null) {
                            communityPostRequest = communityPostRequestService.getById(postRequestId);
                        }
                    }
                    // Подписано заявление от бухгалтера - создаём документ - приказ
                    if (event.getParameters().containsKey(IS_STATEMENT_FROM_BUHGALTER)) {
                        createOrderToAppointBuhgalter(communityPostRequest);
                    // Подписан приказ о назначении бухгалетра-кассира КУЧ - создаём документ - договор
                    } else if (event.getParameters().containsKey(IS_ORDER_TO_APPOINT_BUHGALTER)) {
                        createContractWithBuhgalter(communityPostRequest);
                    // Подписан договор с бухгалтером кассиром - создаём инструкцию для бухгалтера - кассира
                    } else if (event.getParameters().containsKey(IS_CONTRACT_WITH_BUHGALTER)) {
                        createInstructionBuhgalter(communityPostRequest);
                    // Подписана инструкция бухгалтером кассиром - назначаем на должность
                    } else if (event.getParameters().containsKey(IS_INSTRUCTION_OF_BUHGALTER)) {
                        appointMemberToPost(communityPostRequest);
                    }
                }
                break;
            }
        }
    }*/

    @Autowired
    private DocumentService documentService;

    // Параметр документа - ИД запроса назначения на должность.
    private static final String COMMUNITY_POST_REQUEST_ID = "COMMUNITY_POST_REQUEST_ID";

    // Признак документа - документ является заявлением от бухгалтера
    private static final String IS_STATEMENT_FROM_BUHGALTER = "IS_STATEMENT_FROM_BUHGALTER";

    /**
     * Создать заявление от бухгалтера
     * @param communityPostRequest запрос на вступление
     */
    private Document createStatementFromBuhgalter(CommunityPostRequest communityPostRequest) {
        Community community = communityDataService.getByIdFullData(communityPostRequest.getCommunity().getId());
        // Участники документа
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        //-------------------------------------------------------------
        // Бухгалтер
        //-------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), communityPostRequest.getReceiver().getUser().getId(), PlotBuhgalterPostSettings.getInstance().getStatementBuhgalterParticipantName());
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // ПО
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getParent().getId(), PlotBuhgalterPostSettings.getInstance().getStatementPoParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // КУч ПО
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), PlotBuhgalterPostSettings.getInstance().getStatementKuchParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Председатель КУч ПО
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), communityPostRequest.getSender().getUser().getId(), PlotBuhgalterPostSettings.getInstance().getStatementKuchPresidentParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(IS_STATEMENT_FROM_BUHGALTER, "true");
        parameters.put(COMMUNITY_POST_REQUEST_ID, String.valueOf(communityPostRequest.getId()));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        return documentService.createDocumentDomain(PlotBuhgalterPostSettings.getInstance().getStatementFromBuhgalterTemplateCode(), createDocumentParameters, communityPostRequest.getReceiver().getUser().getId(), Collections.singletonList(stateEvent));
    }
    //------------------------------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------------------------------
    // Приказ о назначении бухгалетра-кассира КУЧ
    //------------------------------------------------------------------------------------------------------------------

    // Признак документа - документ является приказом о назначении бухгалетра-кассира
    private static final String IS_ORDER_TO_APPOINT_BUHGALTER = "IS_ORDER_TO_APPOINT_BUHGALTER";

    /**
     * Создать приказ о назначении бухгалетра-кассира КУЧ
     * @param communityPostRequest запрос на вступление
     */
    private void createOrderToAppointBuhgalter(CommunityPostRequest communityPostRequest) {
        Community community = communityDataService.getByIdFullData(communityPostRequest.getCommunity().getId());
        // Участники документа
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        //-------------------------------------------------------------
        // Бухгалтер
        //-------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), communityPostRequest.getReceiver().getUser().getId(), PlotBuhgalterPostSettings.getInstance().getOrderToAppointBuhgalterParticipantName());
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // КУч ПО
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), PlotBuhgalterPostSettings.getInstance().getOrderToAppointKuchParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // ПО
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getParent().getId(), PlotBuhgalterPostSettings.getInstance().getOrderToAppointPoParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(IS_ORDER_TO_APPOINT_BUHGALTER, "true");
        parameters.put(COMMUNITY_POST_REQUEST_ID, String.valueOf(communityPostRequest.getId()));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        documentService.createDocumentDomain(PlotBuhgalterPostSettings.getInstance().getOrderToAppointBuhgalterTemplateCode(), createDocumentParameters, DocumentEntity.SYSTEM_CREATOR_ID, Collections.singletonList(stateEvent));
    }
    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------
    // Договор с бухгалтером кассиром
    //------------------------------------------------------------------------------------------------------------------

    // Признак документа - документ является договором с бухгалтером кассиром
    private static final String IS_CONTRACT_WITH_BUHGALTER = "IS_CONTRACT_WITH_BUHGALTER";

    /**
     * Создать договор с бухгалтером кассиром
     * @param communityPostRequest запрос на вступление
     */
    private void createContractWithBuhgalter(CommunityPostRequest communityPostRequest) {
        Community community = communityDataService.getByIdFullData(communityPostRequest.getCommunity().getId());
        // Участники документа
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        //-------------------------------------------------------------
        // Бухгалтер
        //-------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), communityPostRequest.getReceiver().getUser().getId(), PlotBuhgalterPostSettings.getInstance().getContractBuhgalterParticipantName());
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // КУч ПО
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), PlotBuhgalterPostSettings.getInstance().getContractKuchParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // ПО
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getParent().getId(), PlotBuhgalterPostSettings.getInstance().getContractPoParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(IS_CONTRACT_WITH_BUHGALTER, "true");
        parameters.put(COMMUNITY_POST_REQUEST_ID, String.valueOf(communityPostRequest.getId()));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        documentService.createDocumentDomain(PlotBuhgalterPostSettings.getInstance().getContractWithBuhgalterTemplateCode(), createDocumentParameters, DocumentEntity.SYSTEM_CREATOR_ID, Collections.singletonList(stateEvent));
    }
    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------
    // Инструкция для бухгалтера - кассира КУч
    //http://ramera.ru/admin/flowOfDocuments/documentTemplate/edit?documentTemplateId=162
    //------------------------------------------------------------------------------------------------------------------

    // Признак документа - документ является инструкцией бухгалтера-кассира
    private static final String IS_INSTRUCTION_OF_BUHGALTER = "IS_INSTRUCTION_OF_BUHGALTER";

    /**
     * Создать инструкцию бухгалтера-кассира
     * @param communityPostRequest запрос на вступление
     */
    private void createInstructionBuhgalter(CommunityPostRequest communityPostRequest) {
        Community community = communityDataService.getByIdFullData(communityPostRequest.getCommunity().getId());
        // Участники документа
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        //-------------------------------------------------------------
        // Бухгалтер
        //-------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), communityPostRequest.getReceiver().getUser().getId(), PlotBuhgalterPostSettings.getInstance().getInstructionBuhgalterParticipantName());
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // КУч ПО
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), PlotBuhgalterPostSettings.getInstance().getInstructionKuchParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // ПО
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getParent().getId(), PlotBuhgalterPostSettings.getInstance().getInstructionPoParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(IS_INSTRUCTION_OF_BUHGALTER, "true");
        parameters.put(COMMUNITY_POST_REQUEST_ID, String.valueOf(communityPostRequest.getId()));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        documentService.createDocumentDomain(PlotBuhgalterPostSettings.getInstance().getInstructionBuhgalterTemplateCode(), createDocumentParameters, DocumentEntity.SYSTEM_CREATOR_ID, Collections.singletonList(stateEvent));
    }

    //------------------------------------------------------------------------------------------------------------------
}
