package ru.radom.kabinet.services.communities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.BatchVotingMode;
import ru.askor.voting.domain.RegisteredVoter;
import ru.askor.voting.domain.Voting;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.services.communities.kuch.CreateKuchSecondMeetingSettings;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.voting.BatchVotingConstants;
import ru.radom.kabinet.voting.CommonVotingService;
import ru.radom.kabinet.voting.CooperativeFirstPlotBatchVoting;
import ru.radom.kabinet.voting.CooperativeSecondPlotBatchVoting;

import java.util.*;

/**
 * Сервис создания собрания для создания КУч 2го этапа
 * Created by vgusev on 24.08.2015.
 */
@Service
@Transactional
public class CooperativeSecondMeetingService {

    @Autowired
    private CommonVotingService commonVotingService;

    /*@Autowired
    private SharerDao sharerDao;*/

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private CommunityDataService communityDataService;

    /**
     * Создать собрание для выбора председателя и ревизора КУЧ
     * @param batchVotingForCreateKuch - собрание, на котором выбрали председателя, секретаря собрания и проголосовали за создание КУЧ
     * @param kuchCommunity - КУч ПО
     * @return созданное собрание
     * @throws Exception
     */
    public BatchVoting createCooperativeMeetingForVotingPresidentAndRevisor(BatchVoting batchVotingForCreateKuch, Community kuchCommunity) throws Exception {
        Set<RegisteredVoter> votersAllowed = new HashSet<>();
        Set<Long> votersIds = new HashSet<>();
        for (RegisteredVoter voterAllowed : batchVotingForCreateKuch.getVotersAllowed()) {
            votersAllowed.add(new RegisteredVoter(voterAllowed.getVoterId()));
            votersIds.add(voterAllowed.getVoterId());
        }

        String name = batchVotingForCreateKuch.getAdditionalData().get(BatchVotingConstants.COOPERATIVE_PLOT_NAME_ATTR_NAME);
        String plotDescription = batchVotingForCreateKuch.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_TARGETS_ATTR_NAME);
        // Доп. инфа по целям и задачам создаваемого КУч
        String additionalMeetingTargets = batchVotingForCreateKuch.getAdditionalData().get(BatchVotingConstants.ADDITIONAL_MEETING_TARGETS_ATTR_NAME);
        // Тип голосования
        boolean isSecretVoting = batchVotingForCreateKuch.getParameters().isSecretVoting();

        Community parentCommunity = kuchCommunity.getParent();
        parentCommunity = communityDataService.getByIdFullData(parentCommunity.getId());

        // Создателем собрания должен быть председатель собрания 1го этапа
        User owner = getPresidentOfFirstMeeting(batchVotingForCreateKuch);

        String subject = CreateKuchSecondMeetingSettings.getStringFromSettings(CreateKuchSecondMeetingSettings.getInstance().getMeetingName(), parentCommunity, commonVotingService.getShortCooperativePlotName(name, parentCommunity), owner);

        String descriptionButtonValue = batchVotingForCreateKuch.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_DESCRIPTION_ATTR_NAME);

        String registrationDescription =
            CreateKuchSecondMeetingSettings.getStringFromSettings(
                CreateKuchSecondMeetingSettings.getInstance().getMeetingRegistrationDescription(),
                parentCommunity,
                commonVotingService.getShortCooperativePlotName(name, parentCommunity),
                owner);

        Date startDate = new Date();
        Date endDate = new Date();
        endDate.setTime(endDate.getTime() + 3 * 24 * 60 * 60 * 1000);
        Date votersRegistrationEndDate = new Date();
        votersRegistrationEndDate.setTime(votersRegistrationEndDate.getTime() + 24 * 60 * 60 * 1000);

        Map<String, String> additionalData = new HashMap<>();
        //"communityId" : "ИД объединения с типом Потребительское Общество",
        additionalData.put(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME, String.valueOf(kuchCommunity.getParent().getId()));
        //"plotName" : "Название КУЧ",
        additionalData.put(BatchVotingConstants.COOPERATIVE_PLOT_NAME_ATTR_NAME, name);
        // ИД созданного КУч
        additionalData.put(BatchVotingConstants.KUCH_COMMUNITY_ID_ATTR_NAME, String.valueOf(kuchCommunity.getId()));


        List<Voting> votings = Arrays.asList(
                // Голосование за председателя собрания
                createVotingForPresidentMeeting(votersAllowed, name, parentCommunity, owner),
                // Голосование за секретаря собрания
                createVotingForSecretaryMeeting(votersAllowed, name, parentCommunity, owner),
                // Голосование за повестку дня
                createVotingForMeetingAgenta(name, parentCommunity, owner),
                // Голосование за председателя КУЧ
                createVotingForPresidentOfCooperative(votersAllowed, name, parentCommunity, false, owner),
                // Голосование за ревизора КУЧ
                createVotingForRevisorOfCooperative(votersAllowed, name, parentCommunity, false, owner)
        );


        return commonVotingService.createBatchVoting(
                owner.getId(), subject, plotDescription, additionalMeetingTargets,
                registrationDescription,
                CooperativeSecondPlotBatchVoting.NAME, 51,
                votersIds,
                startDate, endDate, votersRegistrationEndDate,
                true, BatchVotingMode.SEQUENTIAL,
                3, isSecretVoting, false,
                true, descriptionButtonValue, additionalData, votings
        );
    }

    // Председатель собрания 1го этапа
    private User getPresidentOfFirstMeeting(BatchVoting batchVoting) {
        // 1й элемент голосования возвращает выбранного председателя собрания
        Long userId =
                VarUtils.getLong(
                        batchVoting.getVotings()
                                .get(CooperativeFirstPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_MEETING_INDEX)
                                .getVotingItems().get(0).getValue(), null);
        ExceptionUtils.check(userId == null, "Пользователь не найден");
        return userDataService.getByIdMinData(userId);
    }

    // Создание голосования на выбор председателя собрания
    private Voting createVotingForPresidentMeeting(Set<RegisteredVoter> voterAllowed, String cooperativeName, Community parentCommunity, User owner) throws VotingSystemException {
        String shortKuchName = commonVotingService.getShortCooperativePlotName(cooperativeName, parentCommunity);
        String subject = CreateKuchSecondMeetingSettings.getStringFromSettings(CreateKuchSecondMeetingSettings.getInstance().getVotingSubjects().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_MEETING_INDEX), parentCommunity, shortKuchName, owner);
        String description = CreateKuchSecondMeetingSettings.getStringFromSettings(CreateKuchSecondMeetingSettings.getInstance().getVotingDescriptions().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_MEETING_INDEX), parentCommunity, shortKuchName, owner);
        //, votingTemplate.getSuccessDecree(), votingTemplate.getFailDecree()
        // TODO Добавить постановления
        String successDecree = "";
        String failDecree = "";
        String sentence = "";
        return commonVotingService.createVotingCandidate(
                commonVotingService.getCandidatesFromRegisteredVoters(voterAllowed), subject, description, true, true,
                CooperativeSecondPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_MEETING_INDEX, false, true, true, 1, 1, 1L, 1L, false, new HashMap<>(),
                successDecree, failDecree, sentence, false, 51
        );
    }

    // Создание голосования на выбор секретаря собрания
    private Voting createVotingForSecretaryMeeting(Set<RegisteredVoter> voterAllowed, String cooperativeName, Community parentCommunity, User owner) throws VotingSystemException {
        String shortKuchName = commonVotingService.getShortCooperativePlotName(cooperativeName, parentCommunity);
        String subject = CreateKuchSecondMeetingSettings.getStringFromSettings(CreateKuchSecondMeetingSettings.getInstance().getVotingSubjects().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_SECRETARY_OF_MEETING_INDEX), parentCommunity, shortKuchName, owner);
        String description = CreateKuchSecondMeetingSettings.getStringFromSettings(CreateKuchSecondMeetingSettings.getInstance().getVotingDescriptions().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_SECRETARY_OF_MEETING_INDEX), parentCommunity, shortKuchName, owner);
        // TODO Добавить постановления
        String successDecree = "";
        String failDecree = "";
        String sentence = "";
        return commonVotingService.createVotingCandidate(
                commonVotingService.getCandidatesFromRegisteredVoters(voterAllowed), subject, description, true, true,
                CooperativeSecondPlotBatchVoting.VOTING_FOR_SECRETARY_OF_MEETING_INDEX, false, true, true, 1, 1, 1L, 1L, false, new HashMap<>(),
                successDecree, failDecree, sentence, false, 51
        );
    }

    // Создание голосования за повестку дня
    private Voting createVotingForMeetingAgenta(String cooperativeName, Community parentCommunity, User owner) throws VotingSystemException {
        String shortKuchName = commonVotingService.getShortCooperativePlotName(cooperativeName, parentCommunity);
        String subject = CreateKuchSecondMeetingSettings.getStringFromSettings(CreateKuchSecondMeetingSettings.getInstance().getVotingSubjects().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_AGENDA_OF_MEETING_INDEX), parentCommunity, shortKuchName, owner);
        String description = CreateKuchSecondMeetingSettings.getStringFromSettings(CreateKuchSecondMeetingSettings.getInstance().getVotingDescriptions().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_AGENDA_OF_MEETING_INDEX), parentCommunity, shortKuchName, owner);
        // TODO Добавить постановления
        String successDecree = "";
        String failDecree = "";
        String sentence = "";
        return commonVotingService.createVotingProContraAbstain(
                subject, description, true, true, CooperativeSecondPlotBatchVoting.VOTING_FOR_AGENDA_OF_MEETING_INDEX,
                true, true, false, new HashMap<>(), successDecree, failDecree, sentence, true, 51
        );
    }

    // Голосование за выбор председятеля КУЧ
    private Voting createVotingForPresidentOfCooperative(Set<RegisteredVoter> voterAllowed, String cooperativeName, Community parentCommunity, boolean isAddVotingItemsAllowed, User owner) throws VotingSystemException {
        String shortKuchName = commonVotingService.getShortCooperativePlotName(cooperativeName, parentCommunity);
        String subject = CreateKuchSecondMeetingSettings.getStringFromSettings(CreateKuchSecondMeetingSettings.getInstance().getVotingSubjects().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_SOCIAL_COMMUNITY_INDEX), parentCommunity, shortKuchName, owner);
        String description = CreateKuchSecondMeetingSettings.getStringFromSettings(CreateKuchSecondMeetingSettings.getInstance().getVotingDescriptions().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_SOCIAL_COMMUNITY_INDEX), parentCommunity, shortKuchName, owner);
        // TODO Добавить постановления
        String successDecree = "";
        String failDecree = "";
        String sentence = "";
        return commonVotingService.createVotingCandidate(
                commonVotingService.getCandidatesFromRegisteredVoters(voterAllowed), subject, description, true, true,
                CooperativeSecondPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_SOCIAL_COMMUNITY_INDEX, isAddVotingItemsAllowed,
                false, true, 1, 1, 1L, 1L, false, new HashMap<>(), successDecree, failDecree, sentence, false, 51);
    }

    // Голосование за выбор ревизора КУЧ
    private Voting createVotingForRevisorOfCooperative(Set<RegisteredVoter> voterAllowed, String cooperativeName, Community parentCommunity, boolean isAddVotingItemsAllowed, User owner) throws VotingSystemException {
        String shortKuchName = commonVotingService.getShortCooperativePlotName(cooperativeName, parentCommunity);
        String subject = CreateKuchSecondMeetingSettings.getStringFromSettings(CreateKuchSecondMeetingSettings.getInstance().getVotingSubjects().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_REVISOR_OF_SOCIAL_COMMUNITY_INDEX), parentCommunity, shortKuchName, owner);
        String description = CreateKuchSecondMeetingSettings.getStringFromSettings(CreateKuchSecondMeetingSettings.getInstance().getVotingDescriptions().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_REVISOR_OF_SOCIAL_COMMUNITY_INDEX), parentCommunity, shortKuchName, owner);
        // TODO Добавить постановления
        String successDecree = "";
        String failDecree = "";
        String sentence = "";
        return commonVotingService.createVotingCandidate(
                commonVotingService.getCandidatesFromRegisteredVoters(voterAllowed), subject, description, true, true,
                CooperativeSecondPlotBatchVoting.VOTING_FOR_REVISOR_OF_SOCIAL_COMMUNITY_INDEX, isAddVotingItemsAllowed,
                false, true, 1, 1, 1L, 1L, false, new HashMap<>(), successDecree, failDecree, sentence, false, 51
        );
    }

}
