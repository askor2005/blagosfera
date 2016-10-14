package ru.radom.kabinet.voting;

import org.apache.commons.lang3.StringUtils;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.core.util.DateUtils;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.events.voting.VotingPageEvent;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.business.event.VotingEvent;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.business.services.VotingService;
import ru.askor.voting.domain.*;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.model.chat.DialogEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.services.ChatService;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.StompService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.kuch.CommonCreateKuchSettings;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.SystemSettingsConstants;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.voting.protocol.VotingProtocolManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс с методами для работы с собраниями 1го и 2го этапа создания КУч ПО
 * Created by vgusev on 24.08.2015.
 */
@Transactional
@Service("commonVotingService")
public class CommonVotingServiceImpl implements CommonVotingService {

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private StompService stompService;

    @Autowired
    private VotingService votingService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private VotingProtocolManager votingProtocolManager;

    private List<User> getVoters(VotingEvent event) {
        List<User> result = new ArrayList<>();
        try {
            Set<Long> voterIds = null;
            if (event.getVoting() != null) {
                Voting voting = votingService.getVoting(event.getVoting().getId(), true, true);
                voterIds = voting.getParameters().getVotersAllowed();
            } else if (event.getBatchVoting() != null) {
                BatchVoting batchVoting = batchVotingService.getBatchVoting(event.getBatchVoting().getId(), true, true);
                voterIds = new HashSet<>();
                for (RegisteredVoter registeredVoter : batchVoting.getVotersAllowed()) {
                    voterIds.add(registeredVoter.getVoterId());
                }
            }

            if (voterIds != null) {
                result.addAll(voterIds.stream().map(userDataService::getByIdMinData).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    // Изменение статусов собрания
    @Override
    @TransactionalEventListener
    public void onVotingEvent(VotingEvent event) {
        try {
            if (event instanceof VotingPageEvent) {
                VotingPageEvent votingPageEvent = (VotingPageEvent)event;
                switch (votingPageEvent.getVotingPageEventType()) {
                    case RESTART_VOTING: // Голосование перезапущено
                        List<User> voters = getVoters(event);
                        stompService.send(voters, "restart_voting_" + event.getVoting().getId(), "{}");
                        break;
                }
            } else {
                switch (event.getEventType()) {
                    // Изменился статус голосов
                    case VOTE_ADDED:
                    case VOTE_DELETED: {
                        List<User> voters = getVoters(event);
                        stompService.send(voters, "change_votes_" + event.getVoting().getId(), "{}");
                        break;
                    }
                    // Зарегистрировался участник собрания
                    case VOTER_REGISTERED: {
                        List<User> voters = getVoters(event);
                        stompService.send(voters, "voter_registered_" + event.getBatchVoting().getId(), "{}");
                        break;
                    }
                    // Добавлены или удалены варианты голосований
                    case VOTING_ITEM_ADDED:
                    case VOTING_ITEM_DELETED: {
                        List<User> voters = getVoters(event);
                        stompService.send(voters, "change_voting_item_" + event.getVoting().getId(), "{}");
                        break;
                    }
                    //
                    case BATCH_VOTING_STATE_CHANGE: {
                        System.err.println(event.getBatchVoting().getState());
                        List<User> voters = getVoters(event);
                        stompService.send(voters, "batch_voting_finished_" + event.getBatchVoting().getId(), "{}");
                        break;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public BatchVoting createBatchVoting(Long ownerId, String subject, String description, String additionalDescription,
                                         String registrationDescription,
                                         String behavior, long quorumPercent,
                                         Set<Long> votersAllowed,
                                         Date startDate, Date endDate, Date votersRegistrationEndDate,
                                         boolean isCanFinishBeforeEndDate, BatchVotingMode batchVotingMode,
                                         int votingRestartCount, boolean isSecretVoting, boolean isNeedAddAdditionalVotings,
                                         boolean isNeedCreateChat, String descriptionButtonValue,
                                         Map<String, String> additionalData, List<Voting> votings) {
        try {
            BatchVoting batchVoting = new BatchVoting();

            batchVoting.setCreated(LocalDateTime.now());
            batchVoting.setOwnerId(ownerId);
            batchVoting.setSubject(subject);
            batchVoting.setParameters(new BatchVotingParameters());
            batchVoting.getParameters().setBehavior(behavior); // У таска собрания своё поведение

            // Кворум в людях
            Long quorum = (long)Math.ceil((votersAllowed.size() * quorumPercent) / 100d);

            batchVoting.getParameters().setQuorum(quorum);

            batchVoting.getParameters().setStartDate(DateUtils.toLocalDateTime(startDate));
            batchVoting.getParameters().setEndDate(DateUtils.toLocalDateTime(endDate));
            batchVoting.getParameters().setCanFinishBeforeEndDate(isCanFinishBeforeEndDate);

            Set<RegisteredVoter> registeredVoters = new HashSet<>();
            List<Long> votersIds = new ArrayList<>();
            for (Long voterId : votersAllowed) {
                votersIds.add(voterId);
                RegisteredVoter registeredVoter = new RegisteredVoter();
                registeredVoter.setVoterId(voterId);
                registeredVoter.setStatus(RegisteredVoterStatus.UNKNOWN);
                registeredVoters.add(registeredVoter);
            }

            boolean votersNeedBeVerified = settingsManager.getSystemSettingAsBool(SystemSettingsConstants.VOTING_VOTERS_NEED_BE_VERIFIED, false);
            if (votersNeedBeVerified) {
                List<User> votersUsers = userDataService.getByIds(votersIds);
                if (votersUsers != null) {
                    List<String> voterErrorsNames = new ArrayList<>();
                    for (User voterUser : votersUsers) {
                        if (!voterUser.isVerified()) {
                            voterErrorsNames.add(voterUser.getName());
                        }
                    }
                    ExceptionUtils.check(!voterErrorsNames.isEmpty(),
                            (voterErrorsNames.size() > 1 ? "Следующие участники не идентифицированы: " : "Не идентифицирован участник ") +
                                    StringUtils.join(voterErrorsNames, ", ") +
                                    ". Создание собрания не возможно.");
                }
            }


            batchVoting.getVotersAllowed().addAll(registeredVoters);
            batchVoting.getParameters().setMode(batchVotingMode);
            batchVoting.getParameters().setVotersRegistrationEndDate(DateUtils.toLocalDateTime(votersRegistrationEndDate));

            batchVoting.getParameters().setVotingRestartCount(votingRestartCount);
            batchVoting.getParameters().setSecretVoting(isSecretVoting);

            // Создаём голосования
            batchVoting.getVotings().addAll(votings);

            // Добавлены доп. голосования
            batchVoting.getAdditionalData().put(BatchVotingConstants.NEED_ADD_ADDITIONAL_VOTINGS, String.valueOf(isNeedAddAdditionalVotings));
            // Описание собрания
            batchVoting.getAdditionalData().put(BatchVotingConstants.BATCH_VOTING_TARGETS_ATTR_NAME, description);
            // Доп. описание собрания
            batchVoting.getAdditionalData().put(BatchVotingConstants.ADDITIONAL_MEETING_TARGETS_ATTR_NAME, additionalDescription);
            //
            batchVoting.getAdditionalData().put(BatchVotingConstants.MEETING_REGISTRATION_DESCRIPTION, registrationDescription);
            //
            batchVoting.getAdditionalData().put(BatchVotingConstants.QUORUM_PERCENT_ATTR_NAME, String.valueOf(quorumPercent));

            // Если нужно создать чат
            if (isNeedCreateChat) {
                // Диалог для собрания
                DialogEntity dialog = chatService.createDialog(subject, votersIds);
                batchVoting.getAdditionalData().put(BatchVotingConstants.BATCH_VOTING_DIALOG_ID_ATTR_NAME, String.valueOf(dialog.getId()));
            }

            // Текст кнопки, которая открывает описание собрания
            batchVoting.getAdditionalData().put(BatchVotingConstants.BATCH_VOTING_DESCRIPTION_ATTR_NAME, descriptionButtonValue);

            // Добавляем доп параметры
            batchVoting.getAdditionalData().putAll(additionalData);

            // Сохраняем собрание из формы
            batchVoting = batchVotingService.saveBatchVoting(batchVoting);

            // Стартуем гегистрацию участников
            batchVotingService.startVotersRegistration(batchVoting.getId());

            return batchVoting;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public BatchVoting createBatchVoting(Long ownerId, String subject, String description, String additionalDescription, String registrationDescription, long quorumPercent, Set<Long> votersAllowed, Date startDate, Date endDate, Date votersRegistrationEndDate, boolean isCanFinishBeforeEndDate, BatchVotingMode batchVotingMode, int votingRestartCount, boolean isSecretVoting, boolean isNeedAddAdditionalVotings, boolean isNeedCreateChat, String descriptionButtonValue, Map<String, String> additionalData, List<Voting> votings) {
        return createBatchVoting(ownerId, subject, description, additionalDescription,
                registrationDescription, BatchVotingConstants.STANDARD_BEHAVIOR_NAME, quorumPercent, votersAllowed, startDate, endDate, votersRegistrationEndDate,
                isCanFinishBeforeEndDate, batchVotingMode, votingRestartCount, isSecretVoting, isNeedAddAdditionalVotings,
                isNeedCreateChat, descriptionButtonValue, additionalData, votings);
    }

    @Override
    public Set<Long> getCandidatesFromRegisteredVoters(Set<RegisteredVoter> registeredVoters) {
        Set<Long> result = new HashSet<>();
        for (RegisteredVoter registeredVoter : registeredVoters) {
            result.add(registeredVoter.getVoterId());
        }
        return result;
    }

    /**
     * Добавить в голосование описание
     * @param voting голосование
     * @param description текст описания
     */
    private void addToVotingDescription(Voting voting, String description) {
        if (!StringUtils.isBlank(description)) {
            voting.getAdditionalData().put(BatchVotingConstants.VOTING_DESCRIPTION, description);
        }
    }

    private void addToVotingDecree(Voting voting, String successDecree, String failDecree) {
        if (!StringUtils.isBlank(successDecree)) {
            voting.getAdditionalData().put(BatchVotingConstants.VOTING_SUCCESS_DECREE_ATTR_NAME, successDecree);
        }
        if (!StringUtils.isBlank(failDecree)) {
            voting.getAdditionalData().put(BatchVotingConstants.VOTING_FAIL_DECREE_ATTR_NAME, failDecree);
        }
    }

    private void addToVotingSentence(Voting voting, String sentence) {
        if (!StringUtils.isBlank(sentence)) {
            voting.getAdditionalData().put(BatchVotingConstants.VOTING_SENTENCE_ATTR_NAME, sentence);
        }
    }

    /**
     * Установить json с доп. кнопками с просмотром текста в голосовании в доп. параметры голосования
     * @param voting голосование
     * @param modalButtons кнопки с контентом
     */
    private void addToVotingModalButtons(Voting voting, Set<VotingButtonContentDto> modalButtons) {
        if (modalButtons != null && voting != null && voting.getAdditionalData() != null) {
            voting.getAdditionalData().put(BatchVotingConstants.VOTING_BUTTONS_WITH_MODAL_CONTENT, serializeService.toJson(modalButtons));
        }
    }

    /**
     * Установить текст победы варианта голосования
     * @param voting голосование
     * @param votingWinnerText текст победы
     */
    private void addToVotingWinnerText(Voting voting, String votingWinnerText) {
        if (!StringUtils.isBlank(votingWinnerText)) {
            voting.getAdditionalData().put(BatchVotingConstants.VOTING_WINNER_TEXT, votingWinnerText);
        }
    }

    private void addStopBatchVotingOnFailResult(Voting voting, boolean stopBatchVotingOnFailResult) {
        voting.getAdditionalData().put(BatchVotingConstants.STOP_BATCH_VOTING_ON_FAIL_RESULT, String.valueOf(stopBatchVotingOnFailResult));
    }

    private void addPercentForWinToVoting(Voting voting, int percentForWin) {
        ExceptionUtils.check(percentForWin < 1 || percentForWin > 100, "У голосования \"" + voting.getSubject() + "\" значение параметра \"Процент для победы\" должен быть в диапазоне от 1 до 100");
        voting.getAdditionalData().put(BatchVotingConstants.PERCENT_FOR_WIN, String.valueOf(percentForWin));
    }

    private void addWinnersCountToVoting(Voting voting, Long minWinnersCount, Long maxWinnersCount) {
        if (minWinnersCount != null) {
            voting.getAdditionalData().put(BatchVotingConstants.VOTING_MIN_WINNERS_COUNT_ATTR_NAME, String.valueOf(minWinnersCount));
        }
        if (maxWinnersCount != null) {
            voting.getAdditionalData().put(BatchVotingConstants.VOTING_MAX_WINNERS_COUNT_ATTR_NAME, String.valueOf(maxWinnersCount));
        }
    }


    @Override
    public Voting createVotingCandidate(Set<Long> candidates, String subject, String description,
                                        boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                        long index, boolean isAddVotingItemsAllowed, boolean isVisible,
                                        boolean stopBatchVotingOnFailResult, boolean addAbstain,
                                        Set<VotingButtonContentDto> modalButtons, String votingWinnerText,
                                        long minSelectionCount, long maxSelectionCount,
                                        Long minWinnersCount, Long maxWinnersCount,
                                        boolean multipleWinners,
                                        Map<String, String> additionalParameters,
                                        String successDecree, String failDecree,
                                        String sentence, int percentForWin) throws VotingSystemException {
        VotingParameters votingParameters = new VotingParameters();
        votingParameters.setVotingType(VotingType.CANDIDATE);
        votingParameters.setVoteCancellable(isVoteCancellable);
        votingParameters.setVoteCommentsAllowed(isVoteCommentsAllowed);
        votingParameters.setAddVotingItemsAllowed(true);
        votingParameters.setMinSelectionCount(minSelectionCount);
        votingParameters.setMaxSelectionCount(maxSelectionCount);
        votingParameters.setMultipleWinners(multipleWinners);
        votingParameters.setAddVotingItemsAllowed(isAddVotingItemsAllowed);
        votingParameters.setAbstainAllowed(addAbstain);

        Voting result = new Voting(null, null,null, index, subject, VotingState.ACTIVE, votingParameters, new VotingResult());
        if (additionalParameters != null) {
            result.getAdditionalData().putAll(additionalParameters);
        }
        result.setSubject(subject);
        result.setIndex(index);
        result.getVotingItems().addAll(createVotingItems(candidates));
        result.setVisible(isVisible);
        addToVotingDescription(result, description);
        addToVotingModalButtons(result, modalButtons);
        addToVotingWinnerText(result, votingWinnerText);
        addToVotingDecree(result, successDecree, failDecree);
        addToVotingSentence(result, sentence);
        addStopBatchVotingOnFailResult(result, stopBatchVotingOnFailResult);
        addPercentForWinToVoting(result, percentForWin);
        addWinnersCountToVoting(result, minWinnersCount, maxWinnersCount);
        return result;
    }

    @Override
    public Voting createVotingCandidate(Set<Long> candidates, String subject, String description,
                                        boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                        long index, boolean isAddVotingItemsAllowed, boolean isVisible,
                                        boolean stopBatchVotingOnFailResult,
                                        long minSelectionCount, long maxSelectionCount,
                                        Long minWinnersCount, Long maxWinnersCount,
                                        boolean multipleWinners,
                                        Map<String, String> additionalParameters, String successDecree, String failDecree,
                                        String sentence, boolean addAbstain, int percentForWin) throws VotingSystemException {
        return createVotingCandidate(candidates, subject, description, isVoteCancellable, isVoteCommentsAllowed,
                                    index, isAddVotingItemsAllowed, isVisible, stopBatchVotingOnFailResult, addAbstain, null, null,
                                    minSelectionCount, maxSelectionCount, minWinnersCount, maxWinnersCount,
                                    multipleWinners, additionalParameters,
                                    successDecree, failDecree, sentence, percentForWin
        );
    }

    private List<VotingItem> createVotingItems(Set<Long> candidates) throws VotingSystemException {
        List<VotingItem> result = new ArrayList<>();
        for (Long candidateId : candidates) {
            VotingItem votingItem = new VotingItem(null, null, null, String.valueOf(candidateId));
            result.add(votingItem);
        }
        return result;
    }

    private List<VotingItem> createVotingItemsTextVariants(List<String> variants) throws VotingSystemException {
        List<VotingItem> result = new ArrayList<>();
        for (String variant : variants) {
            result.add(new VotingItem(null, null, null, variant));
        }
        return result;
    }

    @Override
    public Voting createVotingProContraAbstain(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                               long index, boolean isVisible, boolean stopBatchVotingOnFailResult, boolean addAbstain,
                                               Set<VotingButtonContentDto> modalButtons, String votingWinnerText, boolean multipleWinners,
                                               Map<String, String> additionalParameters, String successDecree, String failDecree,
                                               String sentence, int percentForWin) throws VotingSystemException {
        VotingParameters votingParameters = new VotingParameters();
        votingParameters.setVotingType(VotingType.PRO_CONTRA);
        votingParameters.setAbstainAllowed(addAbstain);
        votingParameters.setVoteCancellable(isVoteCancellable);
        votingParameters.setVoteCommentsAllowed(isVoteCommentsAllowed);
        votingParameters.setAddVotingItemsAllowed(true);
        votingParameters.setMinSelectionCount(1L);
        votingParameters.setMaxSelectionCount(1L);
        votingParameters.setMultipleWinners(multipleWinners);
        // TODO
        //votingParameters.setFailOnContraResult(true);

        Voting result = new Voting(null, null,null, index, subject, VotingState.ACTIVE, votingParameters, new VotingResult());
        if (additionalParameters != null) {
            result.getAdditionalData().putAll(additionalParameters);
        }
        result.setSubject(subject);
        result.setIndex(index);
        result.setVisible(isVisible);
        addToVotingDescription(result, description);
        addToVotingModalButtons(result, modalButtons);
        addToVotingWinnerText(result, votingWinnerText);
        addToVotingDecree(result, successDecree, failDecree);
        addToVotingSentence(result, sentence);
        addStopBatchVotingOnFailResult(result, stopBatchVotingOnFailResult);
        addPercentForWinToVoting(result, percentForWin);
        return result;
    }

    @Override
    public Voting createVotingProContraAbstain(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                               long index, boolean isVisible, boolean stopBatchVotingOnFailResult, boolean multipleWinners,
                                               Map<String, String> additionalParameters,
                                               String successDecree, String failDecree,
                                               String sentence, boolean addAbstain, int percentForWin) throws VotingSystemException {
        return createVotingProContraAbstain(subject, description, isVoteCancellable, isVoteCommentsAllowed,
                index, isVisible, stopBatchVotingOnFailResult, addAbstain, null, null, multipleWinners,additionalParameters,
                successDecree, failDecree, sentence, percentForWin);
    }

    @Override
    public Voting createMultipleSelectionVoting(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                                long index, boolean isVisible, boolean stopBatchVotingOnFailResult, List<String> variants,
                                                long minSelectionCount, long maxSelectionCount,
                                                Long minWinnersCount, Long maxWinnersCount,
                                                boolean addAbstain,
                                                Set<VotingButtonContentDto> modalButtons, String votingWinnerText,
                                                boolean multipleWinners, Map<String, String> additionalParameters,
                                                String successDecree, String failDecree,
                                                String sentence, int percentForWin) throws VotingSystemException {
        VotingParameters votingParameters = new VotingParameters();
        votingParameters.setVotingType(VotingType.MULTIPLE_SELECTION);
        votingParameters.setVoteCancellable(isVoteCancellable);
        votingParameters.setVoteCommentsAllowed(isVoteCommentsAllowed);
        votingParameters.setAddVotingItemsAllowed(true);
        votingParameters.setMinSelectionCount(minSelectionCount);
        votingParameters.setMaxSelectionCount(maxSelectionCount);
        votingParameters.setMultipleWinners(multipleWinners);
        votingParameters.setAbstainAllowed(addAbstain);

        Voting result = new Voting(null, null,null, index, subject, VotingState.ACTIVE, votingParameters, new VotingResult());
        if (additionalParameters != null) {
            result.getAdditionalData().putAll(additionalParameters);
        }
        result.setSubject(subject);
        result.setIndex(index);
        result.getVotingItems().addAll(createVotingItemsTextVariants(variants));
        result.setVisible(isVisible);
        addToVotingDescription(result, description);
        addToVotingModalButtons(result, modalButtons);
        addToVotingWinnerText(result, votingWinnerText);
        addToVotingDecree(result, successDecree, failDecree);
        addToVotingSentence(result, sentence);
        addStopBatchVotingOnFailResult(result, stopBatchVotingOnFailResult);
        addPercentForWinToVoting(result, percentForWin);
        addWinnersCountToVoting(result, minWinnersCount, maxWinnersCount);
        return result;
    }

    @Override
    public Voting createMultipleSelectionVoting(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                                long index, boolean isVisible, boolean stopBatchVotingOnFailResult, List<String> variants,
                                                long minSelectionCount, long maxSelectionCount,
                                                Long minWinnersCount, Long maxWinnersCount,
                                                boolean multipleWinners,
                                                Map<String, String> additionalParameters, String successDecree, String failDecree,
                                                String sentence, boolean addAbstain, int percentForWin) throws VotingSystemException {
        return createMultipleSelectionVoting(subject, description, isVoteCancellable, isVoteCommentsAllowed,
                                             index, isVisible, stopBatchVotingOnFailResult, variants,
                                             minSelectionCount, maxSelectionCount, minWinnersCount, maxWinnersCount,
                                             addAbstain, null, null, multipleWinners,additionalParameters, successDecree, failDecree, sentence, percentForWin);
    }

    @Override
    public Voting createSingleSelectionVoting(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                              long index, boolean isVisible, boolean stopBatchVotingOnFailResult, List<String> variants, boolean addAbstain,
                                              Set<VotingButtonContentDto> modalButtons, String votingWinnerText, boolean multipleWinners,
                                              Map<String, String> additionalParameters,
                                              String successDecree, String failDecree,
                                              String sentence, int percentForWin) throws VotingSystemException {
        VotingParameters votingParameters = new VotingParameters();
        votingParameters.setVotingType(VotingType.SINGLE_SELECTION);
        votingParameters.setVoteCancellable(isVoteCancellable);
        votingParameters.setVoteCommentsAllowed(isVoteCommentsAllowed);
        votingParameters.setAddVotingItemsAllowed(true);
        votingParameters.setMinSelectionCount(1L);
        votingParameters.setMaxSelectionCount(1L);
        votingParameters.setMultipleWinners(multipleWinners);
        votingParameters.setAbstainAllowed(addAbstain);

        Voting result = new Voting(null, null,null, index, subject, VotingState.ACTIVE, votingParameters, new VotingResult());
        if (additionalParameters != null) {
            result.getAdditionalData().putAll(additionalParameters);
        }
        result.setSubject(subject);
        result.setIndex(index);
        result.getVotingItems().addAll(createVotingItemsTextVariants(variants));
        result.setVisible(isVisible);
        addToVotingDescription(result, description);
        addToVotingModalButtons(result, modalButtons);
        addToVotingWinnerText(result, votingWinnerText);
        addToVotingDecree(result, successDecree, failDecree);
        addToVotingSentence(result, sentence);
        addStopBatchVotingOnFailResult(result, stopBatchVotingOnFailResult);
        addPercentForWinToVoting(result, percentForWin);
        return result;
    }

    @Override
    public Voting createSingleSelectionVoting(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                              long index, boolean isVisible, boolean stopBatchVotingOnFailResult,
                                              List<String> variants, boolean multipleWinners,
                                              Map<String, String> additionalParameters,
                                              String successDecree, String failDecree,
                                              String sentence, boolean addAbstain, int percentForWin) throws VotingSystemException {
        return createSingleSelectionVoting(subject, description, isVoteCancellable, isVoteCommentsAllowed,
                                           index, isVisible, stopBatchVotingOnFailResult,  variants, addAbstain, null, null, multipleWinners,additionalParameters,
                                           successDecree, failDecree, sentence, percentForWin
        );
    }

    @Override
    public Voting createInterviewVoting(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                        long index, boolean isVisible, boolean addAbstain,
                                        Set<VotingButtonContentDto> modalButtons, String votingWinnerText, boolean multipleWinners,
                                        Map<String, String> additionalParameters,
                                        String successDecree, String failDecree,
                                        String sentence) throws VotingSystemException {
        VotingParameters votingParameters = new VotingParameters();
        votingParameters.setVotingType(VotingType.INTERVIEW);
        votingParameters.setVoteCancellable(isVoteCancellable);
        votingParameters.setVoteCommentsAllowed(isVoteCommentsAllowed);
        votingParameters.setAddVotingItemsAllowed(true);
        votingParameters.setMinSelectionCount(1L);
        votingParameters.setMaxSelectionCount(1L);
        votingParameters.setMultipleWinners(multipleWinners);
        votingParameters.setAbstainAllowed(addAbstain);

        Voting result = new Voting(null, null,null, index, subject, VotingState.ACTIVE, votingParameters, new VotingResult());
        if (additionalParameters != null) {
            result.getAdditionalData().putAll(additionalParameters);
        }
        result.setSubject(subject);
        result.setIndex(index);
        result.setVisible(isVisible);
        addToVotingDescription(result, description);
        addToVotingModalButtons(result, modalButtons);
        addToVotingWinnerText(result, votingWinnerText);
        addToVotingDecree(result, successDecree, failDecree);
        addToVotingSentence(result, sentence);
        return result;
    }

    @Override
    public Voting createInterviewVoting(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                        long index, boolean isVisible, boolean multipleWinners, Map<String, String> additionalParameters,
                                        String successDecree, String failDecree,
                                        String sentence, boolean addAbstain) throws VotingSystemException {
        return createInterviewVoting(subject, description, isVoteCancellable, isVoteCommentsAllowed,
                                     index, isVisible, addAbstain, null, null, multipleWinners,additionalParameters,
                                     successDecree, failDecree, sentence
        );
    }

    @Override
    public String getVotingProtocolString(Voting voting) {
        return getVotingProtocolString(voting, null, null);
    }

    @Override
    public String getVotingProtocolString(Voting voting, String successDecree, String failDecree) {
        BatchVoting batchVoting = null;
        try {
            batchVoting = batchVotingService.getBatchVotingByVotingId(voting.getId(), true, true);
        } catch (Exception e) {
            ExceptionUtils.check(true, e.getMessage());
        }
        ExceptionUtils.check(batchVoting == null, "Не найдено собрание по голосованию");
        return getVotingProtocolString(batchVoting, voting, successDecree, failDecree);
    }

    @Override
    public String getVotingProtocolString(BatchVoting batchVoting, Voting voting, String successDecree, String failDecree) {
        ExceptionUtils.check(batchVoting == null, "Не найдено собрание по голосованию");
        return votingProtocolManager.getVotingProtocolString(batchVoting, voting, successDecree, failDecree);
    }

    @Override
    public User getPresidentOfFirstMeeting(BatchVoting batchVoting) {
        // 1й элемент голосования возвращает выбранного председателя собрания
        String userIdStr = batchVoting.getVotings().get(CooperativeFirstPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_MEETING_INDEX).getVotingItems().get(0).getValue();
        return userDataService.getByIdMinData(VarUtils.getLong(userIdStr, -1l));
    }

    @Override
    public User getSecretaryOfFirstMeeting(BatchVoting batchVoting) {
        // 2й элемент голосования возвращает выбранного секретаря собрания
        String userIdStr = batchVoting.getVotings().get(CooperativeFirstPlotBatchVoting.VOTING_FOR_SECRETARY_OF_MEETING_INDEX).getVotingItems().get(0).getValue();
        return userDataService.getByIdMinData(VarUtils.getLong(userIdStr, -1l));
    }

    @Override
    public User getPresidentOfSecondMeeting(BatchVoting batchVoting) {
        // 1й элемент голосования возвращает выбранного председателя собрания
        String userIdStr = batchVoting.getVotings().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_MEETING_INDEX).getVotingItems().get(0).getValue();
        return userDataService.getByIdMinData(VarUtils.getLong(userIdStr, -1l));
    }

    @Override
    public User getSecretaryOfSecondMeeting(BatchVoting batchVoting) {
        // 2й элемент голосования возвращает выбранного секретаря собрания
        String userIdStr = batchVoting.getVotings().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_SECRETARY_OF_MEETING_INDEX).getVotingItems().get(0).getValue();
        return userDataService.getByIdMinData(VarUtils.getLong(userIdStr, -1l));
    }

    @Override
    public User getPresidentOfKuch(BatchVoting secondBatchVoting) {
        String userIdStr = secondBatchVoting.getVotings().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_SOCIAL_COMMUNITY_INDEX).getVotingItems().get(0).getValue();
        return userDataService.getByIdMinData(VarUtils.getLong(userIdStr, -1l));
    }

    @Override
    public User getRevisorOfKuch(BatchVoting secondBatchVoting) {
        String userIdStr = secondBatchVoting.getVotings().get(CooperativeSecondPlotBatchVoting.VOTING_FOR_REVISOR_OF_SOCIAL_COMMUNITY_INDEX).getVotingItems().get(0).getValue();
        return userDataService.getByIdMinData(VarUtils.getLong(userIdStr, -1l));
    }

    @Override
    public Community getPOFromMeeting(BatchVoting batchVoting) {
        Long communityId = VarUtils.getLong(batchVoting.getAdditionalData().get(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME), -1l);
        ExceptionUtils.check(communityId == -1l, "Не правильный ИД Потребительского Общества: " + communityId);
        return communityDomainService.getByIdFullData(communityId);
    }

    @Override
    public Community getKuchFromSecondMeeting(BatchVoting secondBatchVoting) {
        Long newKuchCommunityId = VarUtils.getLong(secondBatchVoting.getAdditionalData().get(BatchVotingConstants.KUCH_COMMUNITY_ID_ATTR_NAME), -1l);
        ExceptionUtils.check(newKuchCommunityId == -1l, "Не правильный ИД КУч ПО: " + newKuchCommunityId);
        return communityDomainService.getByIdFullData(newKuchCommunityId);
    }

    @Override
    public String getShortCooperativePlotName(String createName, Community parentCommunity) {
        // «Ласточка» - ПО РОС «РА-ДОМ»
        //CommonCreateKuchSettings.getInstance().getShortKuchName();
        //String result = new StringBuilder().append("\"").append(createName).append("\" - ").append(parentCommunity.getRusShortName()).append("\"").toString();
        String result = CommonCreateKuchSettings.getStringFromSettings(CommonCreateKuchSettings.getInstance().getShortKuchName(), parentCommunity, createName);
        return getKuchName(result);
    }

    @Override
    public String getFullCooperativePlotName(String createName, Community parentCommunity) {
        // «Ласточка» - ПО РОС «РА-ДОМ» Потребительского Общества Развития Общественных Систем «РА-ДОМ»
        //String result = new StringBuilder().append("\"").append(createName).append("\" - ").append(parentCommunity.getRusShortName()).append(" ").append(Padeg.getOfficePadeg(parentCommunity.getRusFullName(), PadegConstants.PADEG_R)).append("\"").toString();
        String result = CommonCreateKuchSettings.getStringFromSettings(CommonCreateKuchSettings.getInstance().getFullKuchName(), parentCommunity, createName);
        return getKuchName(result);
    }

    @Override
    public String getShortCooperativePlotNameForCreateCommunity(String createName, Community parentCommunity) {
        // «Ласточка» - ПО РОС «РА-ДОМ»
        //CommonCreateKuchSettings.getInstance().getShortKuchName();
        //String result = new StringBuilder().append("\"").append(createName).append("\" - ").append(parentCommunity.getRusShortName()).append("\"").toString();
        String result = CommonCreateKuchSettings.getStringFromSettings(CommonCreateKuchSettings.getInstance().getShortKuchNameCommunityField(), parentCommunity, createName);
        return getKuchName(result);
    }

    @Override
    public String getFullCooperativePlotNameForCreateCommunity(String createName, Community parentCommunity) {
        // «Ласточка» - ПО РОС «РА-ДОМ» Потребительского Общества Развития Общественных Систем «РА-ДОМ»
        //String result = new StringBuilder().append("\"").append(createName).append("\" - ").append(parentCommunity.getRusShortName()).append(" ").append(Padeg.getOfficePadeg(parentCommunity.getRusFullName(), PadegConstants.PADEG_R)).append("\"").toString();
        String result = CommonCreateKuchSettings.getStringFromSettings(CommonCreateKuchSettings.getInstance().getFullKuchNameCommunityField(), parentCommunity, createName);
        return getKuchName(result);
    }

    @Override
    @Deprecated
    public List<Field> decodePlotAddress(String addressFieldsEncoded) throws UnsupportedEncodingException {
        List<Field> result = new ArrayList<>();
        String addressFields = URLDecoder.decode(addressFieldsEncoded, "UTF-8");
        String[] fieldsParts = addressFields.split(";");
        for (String fieldPart : fieldsParts) {
            String[] fieldData = fieldPart.split(":");
            if (fieldData.length == 2) {
                Long fieldId = VarUtils.getLong(fieldData[0], -1l);
                String fieldValue = fieldData[1];
                FieldEntity fieldEntity = fieldDao.getById(fieldId);
                if (fieldEntity != null && fieldValue != null) {
                    Field field = fieldEntity.toDomain();
                    field.setValue(fieldValue);
                    result.add(field);
                }
            }
        }
        return result;
    }

    private // Убрать из имени КУч все двойные кавычки и заменить их на "ёлочки"
    static String getKuchName(String sourceName) {
        String result = "";
        if (sourceName != null) {
            sourceName = StringUtils.trim(sourceName);
            sourceName = sourceName.replaceAll("[\\s]{2,}", " ");
            sourceName = sourceName.replaceAll("\"\"", "\"");
            String[] parts = sourceName.split(" ");
            List<String> partList = new ArrayList<>();
            for (int index = 0; index < parts.length; index++) {
                String part = parts[index];
                if (part.charAt(0) == '"') {
                    part = "«" + part.substring(1);
                }
                if (part.charAt(part.length() - 1) == '"') {
                    part = part.substring(0, part.length() - 1) + "»";
                }
                partList.add(part);
            }
            result = StringUtils.join(partList, " ");
            result = result.replaceAll("««", "«");
            result = result.replaceAll("»»", "»");
        }
        return result;
    }

    @Override
    public List<VotingItem> getWinners(Voting voting) {
        List<VotingItem> result = new ArrayList<>();
        long percentForWin = VarUtils.getLong(voting.getAdditionalData().get(BatchVotingConstants.PERCENT_FOR_WIN), 51L);
        for (VotingItem votingItem : voting.getVotingItems()) {
            if (votingItem.getVotesPercent() >= percentForWin && !votingItem.getValue().equals(VotingType.ITEM_ABSTAIN)) {
                result.add(votingItem);
                if (voting.getParameters().getVotingType().equals(VotingType.SINGLE_SELECTION) ||
                        (voting.getParameters().getVotingType().equals(VotingType.MULTIPLE_SELECTION) && !voting.getParameters().isMultipleWinners())) {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public List<VotingItem> getWinners(Long votingId) {
        List<VotingItem> result = null;
        try {
            Voting voting = votingService.getVoting(votingId, true, true);
            result = getWinners(voting);
        } catch (Exception e) {
            ExceptionUtils.check(true, e.getMessage());
        }
        return result;
    }
}
