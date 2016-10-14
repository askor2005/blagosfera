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
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.kuch.CommonCreateKuchSettings;
import ru.radom.kabinet.services.communities.kuch.documents.ProtocolMeetingSovietForCreateKuchSettings;
import ru.radom.kabinet.services.communities.kuch.documents.ProxyPresidentKuchSettings;
import ru.radom.kabinet.services.communities.kuch.documents.StateKuchSettings;
import ru.radom.kabinet.utils.VarUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс для создания документов Совета ПО для создания КУЧ
 * Created by vgusev on 23.08.2015.
 */
@Service("cooperativePlotSovietDocumentsService")
public class CooperativePlotSovietDocumentsService {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CommonVotingService commonVotingService;

    @Autowired
    private CommunitiesService communitiesService;

    //------------------------------------------------------------------------------------------------------------------
    // Параметры документа - протокол совета по созданию КУч
    //------------------------------------------------------------------------------------------------------------------

    // Наименование параметра документа - протокол собрания совета ПО
    public static final String IS_PROTOCOL_MEETING_SOVIET = "IS_PROTOCOL_MEETING_SOVIET";
    //------------------------------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------------------------------
    // Параметры для документа - положение КУч ПО
    //------------------------------------------------------------------------------------------------------------------

    // Наименование параметра документа - заявление в совет ПО для создания КУч
    public static final String IS_DOCUMENT_OF_STATE_KUCH = "IS_DOCUMENT_OF_STATE_KUCH";
    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------
    // Параметры для документа - доверенность председателя КУч ПО
    //------------------------------------------------------------------------------------------------------------------

    // Наименование параметра документа - доверенность председателя КУч ПО
    public static final String IS_DOCUMENT_OF_PROXY_PRESIDENT_KUCH = "IS_DOCUMENT_OF_PROXY_PRESIDENT_KUCH";
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Создать документ - протокол собрания совета по созданию КУч
     * @param batchVoting собрание первого этапа
     */
    public void createProtocolSovietMeetingForCreateKuch(BatchVoting batchVoting) {
        ProtocolMeetingSovietForCreateKuchSettings templateSettings = CommonCreateKuchSettings.getInstance().getProtocolMeetingSovietForCreateKuchSettings();

        Community community = commonVotingService.getPOFromMeeting(batchVoting);
        String newKuchName = batchVoting.getAdditionalData().get(BatchVotingConstants.COOPERATIVE_PLOT_NAME_ATTR_NAME);
        String shortKuchName = commonVotingService.getShortCooperativePlotName(newKuchName, community);

        User presidentSovietOfCooperative = communitiesService.getPresidentSovietOfCooperative(community);

        // Создатели КУч
        List<Long> creatorsOfKuch = new ArrayList<>();
        Set<RegisteredVoter> registeredVoters = batchVoting.getVotersAllowed();
        creatorsOfKuch.addAll(registeredVoters.stream().map(RegisteredVoter::getVoterId).collect(Collectors.toList()));

        // Участники документа
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        //-------------------------------------------------------------
        // ПО в рамках которого создаётся КУЧ
        //-------------------------------------------------------------

        // Ищем пайщика, который предложил повестку дня
        Voting voting = batchVoting.getVotings().get(CooperativeFirstPlotBatchVoting.VOTING_FOR_AGENDA_OF_MEETING_INDEX);
        String agentaCreatotIdStr = voting.getAdditionalData().get(BatchVotingConstants.COOPERATIVE_AGENTA_CREATOR);
        Long agentaCreatorId = VarUtils.getLong(agentaCreatotIdStr, -1l);
        if (agentaCreatorId == -1l) {
            throw new RuntimeException("Не установлен пайщик ПО, который предложил повестку дня!");
        }

        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), templateSettings.getPoCommunityParticipantName());
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        userFieldValues.add(UserFieldValueBuilder.createStringValue(templateSettings.getKuchNameUserFieldName(), shortKuchName));
        userFieldValues.add(UserFieldValueBuilder.createStringValue(templateSettings.getPresidentSovietFieldName(), presidentSovietOfCooperative.getName()));
        userFieldValues.add(UserFieldValueBuilder.createStringValue(templateSettings.getCountSharesFieldName(), String.valueOf(creatorsOfKuch.size())));
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Список создателей КУч ПО
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL_LIST.getName(), creatorsOfKuch, templateSettings.getSharesGroupParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(IS_PROTOCOL_MEETING_SOVIET, "true");
        parameters.put(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME, String.valueOf(batchVoting.getId()));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        documentService.createDocumentDomain(templateSettings.getTemplateCode(), createDocumentParameters, DocumentEntity.SYSTEM_CREATOR_ID, Collections.singletonList(stateEvent));
    }

    /**
     * Создать документ - положение КУч.
     * @param batchVoting собрание первого этапа
     * @param kuchCommunityId - ИД созданного КУч
     * @param dateOfSignProtocol - Дата подписания протокола советом
     */
    public Document createDocumentStatusKuch(BatchVoting batchVoting, Long kuchCommunityId, Date dateOfSignProtocol) {
        StateKuchSettings templateSettings = CommonCreateKuchSettings.getInstance().getStateKuchSettings();

        Community community = commonVotingService.getPOFromMeeting(batchVoting);

        // Дата подписания протокола без времени
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateOfSignProtocolFormatted = dateFormatter.format(dateOfSignProtocol);

        // Участники документа - председатель и секретарь
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        //-------------------------------------------------------------
        // Куч ПО
        //-------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), kuchCommunityId, templateSettings.getKuchCommunityParticipantName());
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // ПО в рамках которого создаётся КУЧ
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), community.getId(), templateSettings.getPoCommunityParticipantName());
        userFieldValues = new ArrayList<>();
        userFieldValues.add(UserFieldValueBuilder.createStringValue(templateSettings.getDateSignProtocolUserFieldName(), dateOfSignProtocolFormatted));
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(IS_DOCUMENT_OF_STATE_KUCH, "true");
        parameters.put(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME, String.valueOf(batchVoting.getId()));
        parameters.put(BatchVotingConstants.KUCH_COMMUNITY_ID_ATTR_NAME, String.valueOf(kuchCommunityId));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        return documentService.createDocumentDomain(templateSettings.getTemplateCode(), createDocumentParameters, DocumentEntity.SYSTEM_CREATOR_ID, Collections.singletonList(stateEvent));
    }

    /**
     * Создать доверенность председателю КУч. Доверенность Председателю КУЧ о доверении председателю вести договоры переговоры и тд.
     * @param secondBatchVoting Собрание второго этапа
     * @param protocolSecondMeeting Протокол собрания 2го этапа
     * @return созданная доверенность
     */
    public Document createProxyDocumentForPresidentOfKuch(BatchVoting secondBatchVoting, Document protocolSecondMeeting) {
        ProxyPresidentKuchSettings templateSettings = CommonCreateKuchSettings.getInstance().getProxyPresidentKuchSettings();

        Community community = commonVotingService.getPOFromMeeting(secondBatchVoting);
        Community newKuchCommunity = commonVotingService.getKuchFromSecondMeeting(secondBatchVoting);

        User presidentOfKuch = commonVotingService.getPresidentOfKuch(secondBatchVoting);

        // Участники документа
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        //-------------------------------------------------------------
        // Созданный КУч
        //-------------------------------------------------------------
        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), newKuchCommunity.getId(), templateSettings.getKuchCommunityParticipantName());
        List<UserFieldValue> userFieldValues = new ArrayList<>();
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
        participantParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), presidentOfKuch.getId(), templateSettings.getPresidentOfKuchParticipantName());
        userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(IS_DOCUMENT_OF_PROXY_PRESIDENT_KUCH, "true");
        parameters.put(CooperativePlotDocumentsService.PROTOCOL_SECOND_MEETING_ATTR_ID, String.valueOf(protocolSecondMeeting.getId()));
        parameters.put(CooperativePlotCreationService.DOCUMENT_VOTING_BATCH_ID_ATTR_NAME, String.valueOf(secondBatchVoting.getId()));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        return documentService.createDocumentDomain(templateSettings.getTemplateCode(), createDocumentParameters, DocumentEntity.SYSTEM_CREATOR_ID, Collections.singletonList(stateEvent));
    }
}
