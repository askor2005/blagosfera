package ru.radom.kabinet.services.batchVoting;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.BatchVotingMode;
import ru.askor.voting.domain.Voting;
import ru.askor.voting.domain.VotingType;
import ru.radom.kabinet.model.votingtemplate.*;
import ru.radom.kabinet.services.ChatService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.voting.*;

import java.util.*;

/**
 * Created by vgusev on 09.10.2015.
 */
@Service
@Transactional
public class BatchVotingConstructorService {

    // Поведения собраний
    public static final Map<String, String> BEHAVIORS = new HashMap<String, String>() {{
        put(BatchVotingConstants.STANDARD_BEHAVIOR_NAME, "Поведение по умолчанию");
        put(CooperativeFirstPlotBatchVoting.NAME, "Поведение собрания по созданию КУч - первый этап");
        put(CooperativeSecondPlotBatchVoting.NAME, "Поведение собрания по созданию КУч - второй этап");
    }};

    // Виды провередния собраний
    public static final Map<BatchVotingMode, String> MODES = new HashMap<BatchVotingMode, String>() {{
        put(BatchVotingMode.PARALLEL, "Параллельное голосование");
        put(BatchVotingMode.SEQUENTIAL, "Последовательное голосование");
    }};

    // Виды голосований
    public static final Map<VotingType, String> VOTING_TYPES = new HashMap<VotingType, String>() {{
        put(VotingType.CANDIDATE, "Выборы");
        put(VotingType.PRO_CONTRA, "Голосование За/Против");
        put(VotingType.INTERVIEW, "Интервью");
        put(VotingType.MULTIPLE_SELECTION, "Голосование с множественным выбором");
        put(VotingType.SINGLE_SELECTION, "Голосование с единичным выбором");
    }};

    public static final String BATCH_VOTING_TEST_MODE_ATTR_NAME = "batch.voting.test.mode";

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private CommonVotingService commonVotingService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private BatchVotingTemplateService batchVotingTemplateService;

    public Voting createVotingForChoosePresidentOfBatchVoting(Set<Long> votersIds, boolean useBiometricIdentification) throws Exception {
        String subject = "Выборы председателя собрания";
        String description = subject;
        int index = 0;
        Map<String, String> parameters = new HashMap<>();

        if (useBiometricIdentification) {
            parameters.put(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION, VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION);
        }

        return commonVotingService.createVotingCandidate(votersIds, subject, description,
                true, false, index, false, true, true, 1L, 1L, 1L, 1L, false, parameters, "", "", "", false, 51);
    }

    public Voting createVotingForChooseSecretaryOfBatchVoting(Set<Long> votersIds, boolean useBiometricIdentification) throws Exception {
        String subject = "Выборы секретаря собрания";
        String description = subject;
        int index = 1;
        Map<String, String> parameters = new HashMap<>();

        if (useBiometricIdentification) {
            parameters.put(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION, VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION);
        }
        return commonVotingService.createVotingCandidate(votersIds, subject, description,
                true, false, index, false, true, true, 1L, 1L, 1L, 1L, false, parameters, "", "", "", false, 51);
    }

    /**
     * Получить повестку дня на основе списка голосований
     *
     * @param votings список голосования
     * @return строка с повесткой дня
     */
    public String createBatchVotingAgenda(List<Voting> votings) {
        StringBuilder descriptionSb = new StringBuilder();
        int i = 1;
        for (Voting voting : votings) {
            descriptionSb.append(i++).append(") ").append(voting.getSubject()).append("<br/>");
        }
        return descriptionSb.toString();
    }

    public Voting createVotingForMeetingAgenda(List<Voting> votings, boolean useBiometricIdentification) throws Exception {
        StringBuilder descriptionSb = new StringBuilder();
        descriptionSb.append("Повестка дня:<br/>").append(createBatchVotingAgenda(votings));
        String subject = "Голосование за повестку дня";
        String description = descriptionSb.toString();
        int index = 2;
        Map<String, String> parameters = new HashMap<>();

        if (useBiometricIdentification) {
            parameters.put(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION, VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION);
        }

        return commonVotingService.createVotingProContraAbstain(subject, description,
                true, false, index, true, false, true, parameters, "", "", "", true, 51);
    }

    private Set<Long> getCandidatesFromVotingItems(Set<VotingItemTemplate> votingItemsTemplates) {
        Set<Long> result = new HashSet<>();
        for (VotingItemTemplate votingItem : votingItemsTemplates) {
            Long value = VarUtils.getLong(votingItem.getValue(), -1l);
            if (value > -1l) {
                result.add(value);
            }
        }
        return result;
    }

    private String getAttrValue(VotingTemplateEntity votingTemplate, String attrName) {
        String result = null;
        if (votingTemplate != null && votingTemplate.getAttributes() != null) {
            for (VotingAttributeTemplate votingAttributeTemplate : votingTemplate.getAttributes()) {
                if (votingAttributeTemplate.getName().equals(attrName)){
                    result = votingAttributeTemplate.getValue();
                    break;
                }
            }
        }
        return result;
    }

    private List<String> getVariantsFromVotingItems(VotingTemplateEntity votingTemplate) {
        Set<VotingItemTemplate> votingItemsTemplates = votingTemplate.getVotingItems();
        List<String> result = new ArrayList<>();
        String selectFromType = getAttrValue(votingTemplate, BatchVotingConstants.SELECT_FROM_TYPE);
        if (selectFromType == null || selectFromType.equals("addMine")) {
            for (VotingItemTemplate votingItem : votingItemsTemplates) {
                result.add(votingItem.getValue());
            }
        }
        return result;
    }

    /**
     * Создать голосования на основе шаблона собрания
     *
     * @param batchVotingTemplate
     * @param votersIds
     * @return
     * @throws Exception
     */
    private List<Voting> createVotings(BatchVotingTemplateEntity batchVotingTemplate, Set<Long> votersIds, boolean isNeedAdditionalVoting) {
        List<Voting> result = new ArrayList<>();

        try {
            boolean isAddVotingItemsAllowed = false;
            int deltaIndex = 0;

            // Создавать дополнительные голосования нужно если выставлен флаг или поведение не по умолчанию
            if (batchVotingTemplate.getIsNeedAddAdditionalVotings()
                    || CooperativeFirstPlotBatchVoting.NAME.equals(batchVotingTemplate.getBehavior())
                    || CooperativeSecondPlotBatchVoting.NAME.equals(batchVotingTemplate.getBehavior())) {
                deltaIndex = 3;
            }

            List<Voting> templateVotings = new ArrayList<>();

            for (VotingTemplateEntity votingTemplate : batchVotingTemplate.getVotings()) {
                Voting voting = null;
                Map<String, String> additionalParameters = new HashMap<>();

                for (VotingAttributeTemplate votingAttributeTemplate : votingTemplate.getAttributes()) {
                    additionalParameters.put(votingAttributeTemplate.getName(), votingAttributeTemplate.getValue());
                }

                int percentForWin = votingTemplate.getPercentForWin() == null ? 51 : votingTemplate.getPercentForWin();

                switch (votingTemplate.getVotingType()) {
                    case CANDIDATE: // Кандидат
                        // Несколько победителей невозможно потому как можно голосовать только за 1го а процент для победы 51 и более
                        voting = commonVotingService.createVotingCandidate(
                                getCandidatesFromVotingItems(votingTemplate.getVotingItems()), votingTemplate.getSubject(), votingTemplate.getDescription(),
                                votingTemplate.getIsVoteCancellable(), votingTemplate.getIsVoteCommentsAllowed(),
                                votingTemplate.getIndex() + deltaIndex, isAddVotingItemsAllowed,
                                votingTemplate.getIsVisible(), votingTemplate.isStopBatchVotingOnFailResult(),
                                votingTemplate.getMinSelectionCount(), votingTemplate.getMaxSelectionCount(),
                                votingTemplate.getMinWinnersCount(), votingTemplate.getMaxWinnersCount(),
                                votingTemplate.getMultipleWinners(),
                                additionalParameters, votingTemplate.getSuccessDecree(), votingTemplate.getFailDecree(),
                                votingTemplate.getSentence(), votingTemplate.isAddAbstain(), percentForWin);
                        break;
                    case PRO_CONTRA: // За\Против\Воздержался
                        // Несколько победителей невозможно потому как можно голосовать только за 1го а процент для победы 51 и более
                        voting = commonVotingService.createVotingProContraAbstain(
                                votingTemplate.getSubject(), votingTemplate.getDescription(),
                                votingTemplate.getIsVoteCancellable(), votingTemplate.getIsVoteCommentsAllowed(),
                                votingTemplate.getIndex() + deltaIndex, votingTemplate.getIsVisible(),
                                votingTemplate.isStopBatchVotingOnFailResult(), false,
                                additionalParameters, votingTemplate.getSuccessDecree(), votingTemplate.getFailDecree(),
                                votingTemplate.getSentence(), votingTemplate.isAddAbstain(), percentForWin);
                        break;
                    case INTERVIEW:
                        // В инетрвью всегда несколько победителей, потому что никто заранее не видит выбору другого участника
                        voting = commonVotingService.createInterviewVoting(
                                votingTemplate.getSubject(), votingTemplate.getDescription(),
                                votingTemplate.getIsVoteCancellable(), votingTemplate.getIsVoteCommentsAllowed(),
                                votingTemplate.getIndex() + deltaIndex, votingTemplate.getIsVisible(), true,
                                additionalParameters, votingTemplate.getSuccessDecree(), votingTemplate.getFailDecree(),
                                votingTemplate.getSentence(), votingTemplate.isAddAbstain());
                        break;
                    case MULTIPLE_SELECTION:
                        voting = commonVotingService.createMultipleSelectionVoting(
                                votingTemplate.getSubject(), votingTemplate.getDescription(),
                                votingTemplate.getIsVoteCancellable(), votingTemplate.getIsVoteCommentsAllowed(),
                                votingTemplate.getIndex() + deltaIndex, votingTemplate.getIsVisible(),
                                votingTemplate.isStopBatchVotingOnFailResult(),
                                getVariantsFromVotingItems(votingTemplate),
                                votingTemplate.getMinSelectionCount(), votingTemplate.getMaxSelectionCount(),
                                votingTemplate.getMinWinnersCount(), votingTemplate.getMaxWinnersCount(),
                                votingTemplate.getMultipleWinners(), additionalParameters,
                                votingTemplate.getSuccessDecree(), votingTemplate.getFailDecree(),
                                votingTemplate.getSentence(), votingTemplate.isAddAbstain(), percentForWin);
                        break;
                    case SINGLE_SELECTION:
                        // Несколько победителей невозможно потому как можно голосовать только за 1го а процент для победы 51 и более
                        voting = commonVotingService.createSingleSelectionVoting(
                                votingTemplate.getSubject(), votingTemplate.getDescription(),
                                votingTemplate.getIsVoteCancellable(), votingTemplate.getIsVoteCommentsAllowed(),
                                votingTemplate.getIndex() + deltaIndex, votingTemplate.getIsVisible(),
                                votingTemplate.isStopBatchVotingOnFailResult(),
                                getVariantsFromVotingItems(votingTemplate), false,
                                additionalParameters, votingTemplate.getSuccessDecree(), votingTemplate.getFailDecree(),
                                votingTemplate.getSentence(), votingTemplate.isAddAbstain(), percentForWin);
                        break;
                }
                if (voting != null) {
                    templateVotings.add(voting);
                }
            }


            // Создавать дополнительные голосования нужно если выставлен флаг или поведение не по умолчанию
            if (batchVotingTemplate.getIsNeedAddAdditionalVotings()
                    || CooperativeFirstPlotBatchVoting.NAME.equals(batchVotingTemplate.getBehavior())
                    || CooperativeSecondPlotBatchVoting.NAME.equals(batchVotingTemplate.getBehavior())) {
                // Первое голосование - за председателя собрания
                result.add(createVotingForChoosePresidentOfBatchVoting(votersIds, batchVotingTemplate.getUseBiometricIdentificationInAdditionalVotings()));
                // Второе голосование - за секретаря собрания
                result.add(createVotingForChooseSecretaryOfBatchVoting(votersIds, batchVotingTemplate.getUseBiometricIdentificationInAdditionalVotings()));
                // Третье голосование - за повестку дня на основе созданных голосований
                result.add(createVotingForMeetingAgenda(templateVotings, batchVotingTemplate.getUseBiometricIdentificationInAdditionalVotings()));
            }

            result.addAll(templateVotings);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (isNeedAdditionalVoting) {
            for (Voting voting : result) {
                if (voting.getAdditionalData().containsKey(BatchVotingConstants.SOURCE_VOTING_INDEX)) {
                    try {
                        voting.getAdditionalData().put(BatchVotingConstants.SOURCE_VOTING_INDEX,
                                Long.toString(Long.parseLong(voting.getAdditionalData().get(BatchVotingConstants.SOURCE_VOTING_INDEX))+3));//если есть доп голосовния то индекс смещается
                    } catch (NumberFormatException e) {

                    }
                }
            }
        }
        return result;
    }

    /**
     * Старт голосования
     *
     * @param batchVotingTemplate
     * @param ownerId
     */
    public BatchVoting startBatchVoting(BatchVotingTemplateEntity batchVotingTemplate, Long ownerId, Long communityId) {
        if (batchVotingTemplate.getCommunity() != null && batchVotingTemplate.getCommunity().getId() != null) {
            ExceptionUtils.check(!batchVotingTemplate.getCommunity().getId().equals(communityId), "Шаблон собрания не принадлежит объединению");
        }

        boolean isNeedadditionalVotings = BooleanUtils.toBooleanDefaultIfNull(batchVotingTemplate.getIsNeedAddAdditionalVotings(), false);

        ExceptionUtils.check(batchVotingTemplate.getVotersAllowed() == null || batchVotingTemplate.getVotersAllowed().isEmpty(), "Не переданы участники собрания");
        Set<Long> votersWhoNeedSignProtocol = new HashSet<>();
        for (VoterAllowedTemplate voterAllowedTemplate : batchVotingTemplate.getVotersAllowed()) {
            if (BooleanUtils.toBooleanDefaultIfNull(voterAllowedTemplate.getSignProtocol(), false)) {
                votersWhoNeedSignProtocol.add(voterAllowedTemplate.getVoterId());
            }
        }
        ExceptionUtils.check(!isNeedadditionalVotings && votersWhoNeedSignProtocol.isEmpty(), "Не установлены участники которые подписывают протокол собрания");


        Date startDate = batchVotingTemplate.getStartDate();
        Date endDate = new Date(startDate.getTime() + batchVotingTemplate.getBatchVotingHoursCount() * 60l * 60l * 1000l);
        Date votersRegistrationEndDate = new Date(startDate.getTime() + batchVotingTemplate.getRegistrationHoursCount() * 60l * 60l * 1000l);

        Map<String, String> additionalData = new HashMap<>();
        for (BatchVotingAttributeTemplate batchVotingAttributeTemplate : batchVotingTemplate.getAttributes()) {
            additionalData.put(batchVotingAttributeTemplate.getName(), batchVotingAttributeTemplate.getValue());
        }
        additionalData.put(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME, String.valueOf(communityId));

        if (!votersWhoNeedSignProtocol.isEmpty()) {
            additionalData.put(BatchVotingConstants.PARTICIPANTS_WHO_SING_PROTOCOL_IDS_BY_COMMA, StringUtils.join(votersWhoNeedSignProtocol, ","));
        }

        if (batchVotingTemplate.isTestBatchVotingMode()) {
            additionalData.put(BatchVotingConstructorService.BATCH_VOTING_TEST_MODE_ATTR_NAME, String.valueOf(batchVotingTemplate.isTestBatchVotingMode()));
        }

        Set<Long> votersAllowedIds = new HashSet<>();
        if (batchVotingTemplate.getVotersAllowed() != null && !batchVotingTemplate.getVotersAllowed().isEmpty()) {
            for (VoterAllowedTemplate voterAllowed : batchVotingTemplate.getVotersAllowed()) {
                votersAllowedIds.add(voterAllowed.getVoterId());
            }
        }

        List<Voting> votings = createVotings(batchVotingTemplate, votersAllowedIds, batchVotingTemplate.getIsNeedAddAdditionalVotings());

        BatchVoting batchVoting = commonVotingService.createBatchVoting(
                ownerId, batchVotingTemplate.getSubject(), batchVotingTemplate.getDescription(),
                null, null,
                batchVotingTemplate.getQuorum(),
                votersAllowedIds,
                startDate, endDate, votersRegistrationEndDate,
                batchVotingTemplate.getIsCanFinishBeforeEndDate(), BatchVotingMode.SEQUENTIAL,
                batchVotingTemplate.getVotingRestartCount(), batchVotingTemplate.getSecretVoting(), batchVotingTemplate.getIsNeedAddAdditionalVotings(),
                batchVotingTemplate.getIsNeedCreateChat(), null, additionalData, votings
        );

        if (batchVotingTemplate.getId() != null) {
            // Добавляем собрание в архив
            batchVotingTemplate.getBatchVotings().add(batchVoting.getId());
            batchVotingTemplate.setLastBatchVotingDate(new Date());

            batchVotingTemplateService.save(batchVotingTemplate);
        }

        return batchVoting;
    }
}
