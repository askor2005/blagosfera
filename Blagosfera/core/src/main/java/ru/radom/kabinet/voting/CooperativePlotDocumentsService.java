package ru.radom.kabinet.voting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEventType;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.RegisteredVoter;
import ru.askor.voting.domain.Voting;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter;
import ru.radom.kabinet.document.generator.UserFieldValue;
import ru.radom.kabinet.document.generator.UserFieldValueBuilder;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.services.communities.kuch.CommonCreateKuchSettings;
import ru.radom.kabinet.services.communities.kuch.documents.FirstBatchVotingProtocolSettings;
import ru.radom.kabinet.services.communities.kuch.documents.SecondBatchVotingProtocolSettings;
import ru.radom.kabinet.services.communities.kuch.documents.StatementToSovietForApprovePresidentAndRevisorSettings;
import ru.radom.kabinet.services.communities.kuch.documents.StatementToSovietForCreateKuchSettings;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс для создания документов для Председателя и Секретаря собрания по созданию КУЧ
 * Created by vgusev on 23.08.2015.
 */
@Service("cooperativePlotDocumentsService")
public class CooperativePlotDocumentsService {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CommonVotingService commonVotingService;

    //------------------------------------------------------------------------------------------------------------------
    // Параметры для протокола собрания 1го этапа
    //------------------------------------------------------------------------------------------------------------------

    // Наименование параметра протокола - документ является протоколом собрания по созданию КУч
    public static final String IS_PROTOCOL_OF_FIRST_MEETING = "IS_PROTOCOL_OF_FIRST_MEETING";
    //------------------------------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------------------------------
    // Параметры для заявления в совет ПО после проведения собрания 1го этапа
    //------------------------------------------------------------------------------------------------------------------

    // Наименование параметра документа - заявление в совет ПО для создания КУч
    public static final String IS_STATEMENT_TO_SOVIET_FOR_CREATE_KUCH = "IS_STATEMENT_TO_SOVIET_FOR_CREATE_KUCH";
    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------
    // Параметры для протокола собрания 2го этапа
    //------------------------------------------------------------------------------------------------------------------

    // Наименование параметра протокола - документ является протоколом собрания по выбору председателя и ревизора КУч
    public static final String IS_PROTOCOL_OF_SECOND_MEETING = "IS_PROTOCOL_OF_SECOND_MEETING";
    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------
    // Параметры для заявления в совет ПО после проведения собрания 2го этапа
    //------------------------------------------------------------------------------------------------------------------

    // Наименование параметра документа - заявление в совет ПО об утверждении председателя и ревизора КУч
    public static final String IS_STATEMENT_TO_SOVIET_FOR_CHOOSE_PRESIDENT_AND_REVISOR_KUCH = "IS_STATEMENT_TO_SOVIET_FOR_CHOOSE_PRESIDENT_AND_REVISOR_KUCH";

    // Аттрибут - ИД документа - протокол собрания 2го этапа
    public static final String PROTOCOL_SECOND_MEETING_ATTR_ID = "PROTOCOL_SECOND_MEETING_ATTR_ID";
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Создать протокол собрания по созданию КУч.
     * @param batchVoting собрание певого этапа
     */
    public void createMeetingProtocolOfCreateKuch(BatchVoting batchVoting) {
        FirstBatchVotingProtocolSettings templateSettings = CommonCreateKuchSettings.getInstance().getFirstBatchVotingProtocolSettings();


        Community community = commonVotingService.getPOFromMeeting(batchVoting);
        String newKuchName = batchVoting.getAdditionalData().get(BatchVotingConstants.COOPERATIVE_PLOT_NAME_ATTR_NAME);
        String shortKuchName = commonVotingService.getShortCooperativePlotName(newKuchName, community);

        // Создатели КУч
        List<Long> creatorsOfKuch = new ArrayList<>();
        Set<RegisteredVoter> registeredVoters = batchVoting.getVotersAllowed();
        creatorsOfKuch.addAll(registeredVoters.stream().map(RegisteredVoter::getVoterId).collect(Collectors.toList()));

        User president = commonVotingService.getPresidentOfFirstMeeting(batchVoting);
        User secretary = commonVotingService.getSecretaryOfFirstMeeting(batchVoting);

        // Участники документа - председатель и секретарь
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        Voting votingAgendaOfMeeting = batchVoting.getVotings().get(CooperativeFirstPlotBatchVoting.VOTING_FOR_AGENDA_OF_MEETING_INDEX);
        Voting votingForCreateKuch = batchVoting.getVotings().get(CooperativeFirstPlotBatchVoting.VOTING_FOR_CREATING_SOCIAL_COMMUNITY_INDEX);

        //-------------------------------------------------------------
        // Председатель
        //-------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), president.getId(), templateSettings.getPresidentOfMeetingParticipantName());
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        userFieldValues.add(UserFieldValueBuilder.createStringValue(templateSettings.getKuchNameUserFieldName(), shortKuchName));
        userFieldValues.add(UserFieldValueBuilder.createStringValue(templateSettings.getProtocolOfVotingOfMeetingAgenta(), commonVotingService.getVotingProtocolString(votingAgendaOfMeeting))); // результаты голосования за повестку дня
        userFieldValues.add(UserFieldValueBuilder.createStringValue(templateSettings.getProtocolOfVotingOfCreateKuch(), commonVotingService.getVotingProtocolString(votingForCreateKuch))); // результаты голосования за образование КУч
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Секретарь
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), secretary.getId(), templateSettings.getSecretaryOfMeetingParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Список создателей КУч
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL_LIST.getName(), creatorsOfKuch, templateSettings.getSharesGroupParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // ПО в рамках которого создаётся КУЧ
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), templateSettings.getPoCommunityParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(IS_PROTOCOL_OF_FIRST_MEETING, "true");
        parameters.put(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME, String.valueOf(batchVoting.getId()));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        documentService.createDocumentDomain(templateSettings.getTemplateCode(), createDocumentParameters, secretary.getId(), Collections.singletonList(stateEvent));
    }

    /**
     * Создать заявление о создании КУч в совет ПО
     * @param batchVoting собрание первого этапа
     */
    public void createStatementToSovietAboutCreateKuch(BatchVoting batchVoting) {
        StatementToSovietForCreateKuchSettings templateSettings = CommonCreateKuchSettings.getInstance().getStatementToSovietForCreateKuchSettings();

        Community community = commonVotingService.getPOFromMeeting(batchVoting);
        String newKuchName = batchVoting.getAdditionalData().get(BatchVotingConstants.COOPERATIVE_PLOT_NAME_ATTR_NAME);
        String shortKuchName = commonVotingService.getShortCooperativePlotName(newKuchName, community);

        User secretary = commonVotingService.getSecretaryOfFirstMeeting(batchVoting);

        // Участники документа - все, кто создаёт КУч
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        Map<String, String> parameters = new HashMap<>();
        parameters.put(IS_STATEMENT_TO_SOVIET_FOR_CREATE_KUCH, "true");
        parameters.put(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME, String.valueOf(batchVoting.getId()));

        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        // Создатели КУч
        List<Long> creatorsOfKuch = new ArrayList<>();
        Set<RegisteredVoter> registeredVoters = batchVoting.getVotersAllowed();
        creatorsOfKuch.addAll(registeredVoters.stream().map(RegisteredVoter::getVoterId).collect(Collectors.toList()));

        //-------------------------------------------------------------
        // Список участников, которые создают КУч
        //-------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL_LIST.getName(), creatorsOfKuch, templateSettings.getSharesGroupParticipantName());
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // ПО в рамках которого создаётся КУЧ
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), templateSettings.getPoCommunityParticipantName());
        userFieldValues = new ArrayList<>();
        userFieldValues.add(UserFieldValueBuilder.createStringValue(templateSettings.getKuchNameUserFieldName(), shortKuchName));
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        documentService.createDocumentDomain(templateSettings.getTemplateCode(), createDocumentParameters, secretary.getId(), Collections.singletonList(stateEvent));
    }

    /**
     * Создать протокол собрания по выбору председателя и ревизора КУч.
     * @param batchVoting собрание второго этапа
     */
    public void createMeetingProtocolOfChoosePresidentAndRevisorKuch(BatchVoting batchVoting) {
        SecondBatchVotingProtocolSettings templateSettings = CommonCreateKuchSettings.getInstance().getSecondBatchVotingProtocolSettings();

        Community community = commonVotingService.getPOFromMeeting(batchVoting);
        Community newKuchCommunity = commonVotingService.getKuchFromSecondMeeting(batchVoting);

        User president = commonVotingService.getPresidentOfSecondMeeting(batchVoting);
        User secretary = commonVotingService.getSecretaryOfSecondMeeting(batchVoting);

        // Создатели КУч
        List<Long> creatorsOfKuch = new ArrayList<>();
        Set<RegisteredVoter> registeredVoters = batchVoting.getVotersAllowed();
        creatorsOfKuch.addAll(registeredVoters.stream().map(RegisteredVoter::getVoterId).collect(Collectors.toList()));


        // Участники документа - председатель и секретарь
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        //-------------------------------------------------------------
        // Председатель
        //-------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), president.getId(), templateSettings.getPresidentOfMeetingParticipantName());
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Секретарь
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), secretary.getId(), templateSettings.getSecretaryOfMeetingParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Созданный КУч
        //-------------------------------------------------------------
        Voting votingForPresident = batchVoting.getVotings().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_SOCIAL_COMMUNITY_INDEX);
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), newKuchCommunity.getId(), templateSettings.getKuchCommunityParticipantName());
        userFieldValues = new ArrayList<>();
        userFieldValues.add(UserFieldValueBuilder.createStringValue(templateSettings.getProtocolVotingPresidentOfKuchUserFieldName(), commonVotingService.getVotingProtocolString(votingForPresident)));
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // ПО в рамках которого создаётся КУЧ
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), templateSettings.getPoCommunityParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Создатели КУч
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL_LIST.getName(), creatorsOfKuch, templateSettings.getSharesGroupParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Председатель КУч
        //-------------------------------------------------------------
        User presidentOfKuch = commonVotingService.getPresidentOfKuch(batchVoting);
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), presidentOfKuch.getId(), templateSettings.getPresidentOfKuchParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Ревизор КУч
        //-------------------------------------------------------------
        User revisorOfKuch = commonVotingService.getRevisorOfKuch(batchVoting);
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), revisorOfKuch.getId(), templateSettings.getRevisorOfKuchParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(IS_PROTOCOL_OF_SECOND_MEETING, "true");
        parameters.put(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME, String.valueOf(batchVoting.getId()));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        documentService.createDocumentDomain(templateSettings.getTemplateCode(), createDocumentParameters, secretary.getId(), Collections.singletonList(stateEvent));
    }

    /**
     * Создать заявление о выборе председателя и ревизора КУч в совет ПО
     * @param batchVoting собрание второго этапа
     * @param protocolSecondMeeting документ - протокол собрания второго этапа
     */
    public void createStatementToSovietAboutChoosePresidentAndRevisorKuch(BatchVoting batchVoting, Document protocolSecondMeeting) {
        StatementToSovietForApprovePresidentAndRevisorSettings templateSettings = CommonCreateKuchSettings.getInstance().getStatementToSovietForApprovePresidentAndRevisorSettings();

        Community community = commonVotingService.getPOFromMeeting(batchVoting);
        Community newKuchCommunity = commonVotingService.getKuchFromSecondMeeting(batchVoting);

        User secretary = commonVotingService.getSecretaryOfSecondMeeting(batchVoting);

        // Участники документа - все, кто создаёт КУч
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        Map<String, String> parameters = new HashMap<>();
        parameters.put(IS_STATEMENT_TO_SOVIET_FOR_CHOOSE_PRESIDENT_AND_REVISOR_KUCH, "true");
        parameters.put(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME, String.valueOf(batchVoting.getId()));
        parameters.put(PROTOCOL_SECOND_MEETING_ATTR_ID, String.valueOf(protocolSecondMeeting.getId()));

        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        // Создатели КУч
        List<Long> creatorsOfKuch = new ArrayList<>();
        Set<RegisteredVoter> registeredVoters = batchVoting.getVotersAllowed();
        creatorsOfKuch.addAll(registeredVoters.stream().map(RegisteredVoter::getVoterId).collect(Collectors.toList()));

        //-------------------------------------------------------------
        // Список участников, которые создают КУч
        //-------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL_LIST.getName(), creatorsOfKuch, templateSettings.getSharesGroupParticipantName());
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Созданный КУч
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), newKuchCommunity.getId(), templateSettings.getKuchCommunityParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // ПО в рамках которого создаётся КУЧ
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), templateSettings.getPoCommunityParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Председатель КУч
        //-------------------------------------------------------------
        User presidentOfKuch = commonVotingService.getPresidentOfKuch(batchVoting);
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), presidentOfKuch.getId(), templateSettings.getPresidentOfKuchParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------


        documentService.createDocumentDomain(templateSettings.getTemplateCode(), createDocumentParameters, secretary.getId(), Collections.singletonList(stateEvent));
    }


}
