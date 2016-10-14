package ru.radom.kabinet.voting;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEventType;
import ru.askor.voting.business.event.VotingEvent;
import ru.askor.voting.business.event.VotingEventType;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.BatchVotingState;
import ru.askor.voting.domain.RegisteredVoter;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter;
import ru.radom.kabinet.document.generator.UserFieldValue;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.services.ChatService;
import ru.radom.kabinet.services.batchVoting.BatchVotingConstructorService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.voting.settings.ConstructorBatchVotingSettings;

import java.util.*;

/**
 *
 * Created by vgusev on 30.06.2016.
 */
@Transactional
@Component
public class BatchVotingEventListener {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private ChatService chatService;

    @Autowired
    private BatchVotingService batchVotingService;

    @EventListener
    public void onHandleEvent(ApplicationEvent event) {
        if (event instanceof VotingEvent) {
            onVotingEvent((VotingEvent) event);
        } else if (event instanceof FlowOfDocumentStateEvent) {
            onDocumentEvent((FlowOfDocumentStateEvent) event);
        }
    }

    private void onVotingEvent(VotingEvent event) {
        if (event.getEventType() == VotingEventType.BATCH_VOTING_STATE_CHANGE) {
            BatchVoting batchVoting = event.getBatchVoting();

            if (!batchVoting.getParameters().getBehavior().equals(BatchVotingConstants.STANDARD_BEHAVIOR_NAME)) {
                return;
            }

            // Создать протокол собрания
            try {
                batchVoting = batchVotingService.getBatchVoting(batchVoting.getId(), true, true);
                //Voting firstFailedVoting = batchVotingService.getFirstFailedVoting(batchVoting);

                if (batchVoting.getState() == BatchVotingState.FINISHED/* && firstFailedVoting == null*/) {
                    // Создаём протокол собрания
                    createProtocolOfBatchVoting(batchVoting);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onDocumentEvent(FlowOfDocumentStateEvent event) {
        try {
            switch (event.getStateEventType()) {
                case DOCUMENT_SIGNED: // Документ подписан всеми
                    if (event.getParameters() != null) {
                        // Подписан протокол собрания
                        if (event.getParameters().containsKey(BatchVotingConstants.IS_PROTOCOL_OF_BATCH_VOTING)) {
                            // Удалить чат собрания если он есть и в нём более 2х участников (2 участника - чат между этими пользователями)
                            String batchVotingIdStr = event.getParameters().get(BatchVotingConstants.BATCH_VOTING_ID_ATTR_NAME);
                            Long batchVotingId = VarUtils.getLong(batchVotingIdStr, null);
                            BatchVoting batchVoting = batchVotingService.getBatchVoting(batchVotingId, false, false);
                            String dialogIdStr = batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_DIALOG_ID_ATTR_NAME);
                            Long dialogId = VarUtils.getLong(dialogIdStr, null);
                            if (dialogId != null) {
                                chatService.closeDialog(dialogId, 3);
                            }
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionUtils.check(true, e.getMessage());
        }
    }

    private void createProtocolBase(BatchVoting batchVoting, ConstructorBatchVotingSettings constructorBatchVotingSettings, String templateCode, List<CreateDocumentParameter> additionalDocumentParameters) {
        // Участники собрания
        List<Long> batchVotingVoters = new ArrayList<>();
        for (RegisteredVoter registeredVoter : batchVoting.getVotersAllowed()) {
            batchVotingVoters.add(registeredVoter.getVoterId());
        }

        //
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();

        ParticipantCreateDocumentParameter participantParameter;

        if (additionalDocumentParameters != null) {
            createDocumentParameters.addAll(additionalDocumentParameters);
        }

        //-------------------------------------------------------------
        // Подписанты документа
        //-------------------------------------------------------------

        String votersIdsByComma = batchVoting.getAdditionalData().get(BatchVotingConstants.PARTICIPANTS_WHO_SING_PROTOCOL_IDS_BY_COMMA);
        List<Long> votersIds = new ArrayList<>();
        if (votersIdsByComma != null) {
            String[] votersIdsStr = votersIdsByComma.split(",");

            for (String voterIdStr : votersIdsStr) {
                Long voterId = VarUtils.getLong(voterIdStr, null);
                if (voterId != null) {
                    votersIds.add(voterId);
                }
            }
            if (!votersIds.isEmpty()) {
                participantParameter = new ParticipantCreateDocumentParameter(
                        ParticipantsTypes.INDIVIDUAL_LIST.getName(), votersIds, constructorBatchVotingSettings.getVoterWhoSignProtocolParticipantName());
                createDocumentParameters.add(new CreateDocumentParameter(participantParameter, Collections.emptyList()));
            }
        }
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Участники собрания
        //-------------------------------------------------------------
        participantParameter = new ParticipantCreateDocumentParameter(
                ParticipantsTypes.INDIVIDUAL_LIST.getName(), batchVotingVoters, constructorBatchVotingSettings.getVotersParticipantName());
        List<UserFieldValue> userFieldValues = new ArrayList<>();
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, userFieldValues));
        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Объединение
        //-------------------------------------------------------------
        if (batchVoting.getAdditionalData() != null && batchVoting.getAdditionalData().containsKey(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME)) {
            Long communityId = VarUtils.getLong(batchVoting.getAdditionalData().get(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME), null);
            if (communityId != null) {
                participantParameter = new ParticipantCreateDocumentParameter(
                        ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.getName(), communityId, constructorBatchVotingSettings.getCommunityParticipantName());
                createDocumentParameters.add(new CreateDocumentParameter(participantParameter, Collections.emptyList()));
            }
        }
        //-------------------------------------------------------------
        // Создаём евент, который вызовется после подписания документа
        Map<String, String> parameters = new HashMap<>();
        parameters.put(BatchVotingConstants.IS_PROTOCOL_OF_BATCH_VOTING, "true");
        parameters.put(BatchVotingConstants.BATCH_VOTING_ID_ATTR_NAME, String.valueOf(batchVoting.getId()));
        FlowOfDocumentStateEvent stateEvent = new FlowOfDocumentStateEvent(this, parameters, FlowOfDocumentStateEventType.DOCUMENT_SIGNED);

        Map<String, Object> scriptVars = new HashMap<>();
        scriptVars.put("batchVoting", batchVoting);

        boolean testBatchVoting = VarUtils.getBool(batchVoting.getAdditionalData().get(BatchVotingConstructorService.BATCH_VOTING_TEST_MODE_ATTR_NAME), false);

        Document document = documentService.createDocumentDomain(
                templateCode, createDocumentParameters, batchVoting.getOwnerId(),
                Collections.singletonList(stateEvent), null, true, scriptVars, false,
                testBatchVoting
        );

        batchVoting.getAdditionalData().put(BatchVotingConstants.BATCH_VOTING_PROTOCOL_ID, String.valueOf(document.getId()));

        try {
            // Сохраняем ИД протокола в доп. параметрах
            batchVotingService.saveAdditionalData(batchVoting);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionUtils.check(true, e.getMessage());
        }
    }

    private void createProtocolOfBatchVotingWithPresidentVoting(BatchVoting batchVoting, ConstructorBatchVotingSettings constructorBatchVotingSettings, String templateCode) {
        List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();
        //-------------------------------------------------------------
        // Председатель собрания
        //-------------------------------------------------------------

        String presidentIdStr = batchVoting.getVotings().get(BatchVotingConstants.VOTING_FOR_PRESIDENT_OF_MEETING_INDEX).getVotingItems().get(0).getValue();
        Long presidentId = VarUtils.getLong(presidentIdStr, null);

        ParticipantCreateDocumentParameter participantParameter = new ParticipantCreateDocumentParameter(
                ParticipantsTypes.INDIVIDUAL.getName(), presidentId, constructorBatchVotingSettings.getPresidentVotingParticipantName());
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, Collections.emptyList()));

        //-------------------------------------------------------------

        //-------------------------------------------------------------
        // Секретарь собрания
        //-------------------------------------------------------------

        String secretaryIdStr = batchVoting.getVotings().get(BatchVotingConstants.VOTING_FOR_SECRETARY_OF_MEETING_INDEX).getVotingItems().get(0).getValue();
        Long secretaryId = VarUtils.getLong(secretaryIdStr, null);

        participantParameter = new ParticipantCreateDocumentParameter(
                ParticipantsTypes.INDIVIDUAL.getName(), secretaryId, constructorBatchVotingSettings.getSecretaryVotingParticipantName());
        createDocumentParameters.add(new CreateDocumentParameter(participantParameter, Collections.emptyList()));
        //-------------------------------------------------------------

        createProtocolBase(batchVoting, constructorBatchVotingSettings, templateCode, createDocumentParameters);
    }

    private void createProtocolOfBatchVotingWithoutPresidentVoting(BatchVoting batchVoting, ConstructorBatchVotingSettings constructorBatchVotingSettings) {
        createProtocolBase(batchVoting, constructorBatchVotingSettings, constructorBatchVotingSettings.getTemplateCodeWithoutPresidentVoting(), null);
    }

    private void createProtocolOfBatchVoting(BatchVoting batchVoting) {
        ConstructorBatchVotingSettings constructorBatchVotingSettings;
        if (batchVoting.getAdditionalData().containsKey(BatchVotingConstants.BATCH_VOTING_PROTOCOL_SETTINGS_ATTR_NAME)) {
            constructorBatchVotingSettings = settingsManager.getSystemSettingsAsObject(
                    batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_PROTOCOL_SETTINGS_ATTR_NAME),
                    ConstructorBatchVotingSettings.class, ConstructorBatchVotingSettings.getDefaultSettings());
        } else {
            constructorBatchVotingSettings = settingsManager.getSystemSettingsAsObject(
                    BatchVotingConstants.BATCH_VOTING_STANDARD_PROTOCOL_SETTINGS_KEY, ConstructorBatchVotingSettings.class, ConstructorBatchVotingSettings.getDefaultSettings());
        }

        boolean needHandlePresidentVoting = isNeedHandlePresidentVoting(batchVoting);
        if (needHandlePresidentVoting) { // Создаём протокол с председателем и секретарём собрания
            String votersIdsByComma = batchVoting.getAdditionalData().get(BatchVotingConstants.PARTICIPANTS_WHO_SING_PROTOCOL_IDS_BY_COMMA);
            String templateCode;
            if (StringUtils.isBlank(votersIdsByComma)) {
                templateCode = constructorBatchVotingSettings.getTemplateCodeWithoutUsersSigner();
            } else {
                templateCode = constructorBatchVotingSettings.getTemplateCode();
            }
            createProtocolOfBatchVotingWithPresidentVoting(batchVoting, constructorBatchVotingSettings, templateCode);
        } else { // Создаём протокол без председателя и секретаря собрания
            createProtocolOfBatchVotingWithoutPresidentVoting(batchVoting, constructorBatchVotingSettings);
        }
    }

    public boolean isNeedHandlePresidentVoting(BatchVoting batchVoting) {
        boolean result = false;
        if (batchVoting.getAdditionalData().containsKey(BatchVotingConstants.NEED_ADD_ADDITIONAL_VOTINGS) &&
                "true".equals(batchVoting.getAdditionalData().get(BatchVotingConstants.NEED_ADD_ADDITIONAL_VOTINGS))) {
            result = true;
        }
        return result;
    }
}
