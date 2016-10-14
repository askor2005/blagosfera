package ru.radom.kabinet.voting;

import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.business.event.VotingEvent;
import ru.askor.voting.domain.*;
import ru.askor.voting.domain.exception.VotingSystemException;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 *
 * Created by mnikitin on 25.05.2016.
 */
public interface CommonVotingService {

    // Изменение статусов собрания
    void onVotingEvent(VotingEvent event);

    /**
     * Создать собрание по параметрам
     * @param ownerId ИД создателя собрания
     * @param subject название собрания
     * @param description описание собрания
     * @param additionalDescription доп. описание собрания
     * @param registrationDescription доп. описание на странице регистрации
     * @param behavior поведение собрания
     * @param quorumPercent кворум в процентах
     * @param votersAllowed ИДы участников собрания
     * @param startDate дата начала собрания
     * @param endDate дата окончания собрания
     * @param votersRegistrationEndDate дата окончания регистрации в собрании
     * @param isCanFinishBeforeEndDate возможность завершить собрание до её окончания
     * @param batchVotingMode поведение голосований в собрании
     * @param votingRestartCount количество рестартов голосований
     * @param isSecretVoting голосования являются закрытыми
     * @param isNeedAddAdditionalVotings нужно создавать доп. голосования
     * @param descriptionButtonValue текст кнопки, которая открывает описание собрания
     * @param isNeedCreateChat нужно создавать чат собрания
     * @param additionalData доп. данные собрания
     * @param votings голосования собрания
     * @return собрание
     */
    BatchVoting createBatchVoting(Long ownerId, String subject, String description, String additionalDescription,
                                  String registrationDescription,
                                  String behavior, long quorumPercent,
                                  Set<Long> votersAllowed,
                                  Date startDate, Date endDate, Date votersRegistrationEndDate,
                                  boolean isCanFinishBeforeEndDate, BatchVotingMode batchVotingMode,
                                  int votingRestartCount, boolean isSecretVoting, boolean isNeedAddAdditionalVotings,
                                  boolean isNeedCreateChat, String descriptionButtonValue,
                                  Map<String, String> additionalData, List<Voting> votings);

    BatchVoting createBatchVoting(Long ownerId, String subject, String description, String additionalDescription,
                                  String registrationDescription,
                                  long quorumPercent,
                                  Set<Long> votersAllowed,
                                  Date startDate, Date endDate, Date votersRegistrationEndDate,
                                  boolean isCanFinishBeforeEndDate, BatchVotingMode batchVotingMode,
                                  int votingRestartCount, boolean isSecretVoting, boolean isNeedAddAdditionalVotings,
                                  boolean isNeedCreateChat, String descriptionButtonValue,
                                  Map<String, String> additionalData, List<Voting> votings);

    Set<Long> getCandidatesFromRegisteredVoters(Set<RegisteredVoter> registeredVoters);

    /**
     * Создать голосование за кандидатов.
     * @param candidates
     * @param subject
     * @param description
     * @param isVoteCancellable
     * @param isVoteCommentsAllowed
     * @param index
     * @param isAddVotingItemsAllowed
     * @param isVisible
     * @param stopBatchVotingOnFailResult
     * @param addAbstain
     * @param modalButtons
     * @param votingWinnerText
     * @param minSelectionCount
     * @param maxSelectionCount
     * @param minWinnersCount
     * @param maxWinnersCount
     * @param multipleWinners
     * @param additionalParameters
     * @param successDecree
     * @param failDecree
     * @param sentence
     * @param percentForWin
     * @return
     * @throws VotingSystemException
     */
    Voting createVotingCandidate(Set<Long> candidates, String subject, String description,
                                 boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                 long index, boolean isAddVotingItemsAllowed, boolean isVisible, boolean stopBatchVotingOnFailResult,
                                 boolean addAbstain, Set<VotingButtonContentDto> modalButtons,
                                 String votingWinnerText,
                                 long minSelectionCount, long maxSelectionCount,
                                 Long minWinnersCount, Long maxWinnersCount,
                                 boolean multipleWinners,
                                 Map<String, String> additionalParameters, String successDecree, String failDecree,
                                 String sentence, int percentForWin) throws VotingSystemException;

    /**
     * Создать голосование за кандидатов.
     * @param candidates
     * @param subject
     * @param description
     * @param isVoteCancellable
     * @param isVoteCommentsAllowed
     * @param index
     * @param isAddVotingItemsAllowed
     * @param isVisible
     * @param stopBatchVotingOnFailResult
     * @param minSelectionCount
     * @param maxSelectionCount
     * @param minWinnersCount
     * @param maxWinnersCount
     * @param multipleWinners
     * @param additionalParameters
     * @param successDecree
     * @param failDecree
     * @param sentence
     * @param addAbstain
     * @param percentForWin
     * @return
     * @throws VotingSystemException
     */
    Voting createVotingCandidate(Set<Long> candidates, String subject, String description,
                                 boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                 long index, boolean isAddVotingItemsAllowed, boolean isVisible, boolean stopBatchVotingOnFailResult,
                                 long minSelectionCount, long maxSelectionCount,
                                 Long minWinnersCount, Long maxWinnersCount,
                                 boolean multipleWinners,
                                 Map<String, String> additionalParameters, String successDecree, String failDecree,
                                 String sentence, boolean addAbstain, int percentForWin) throws VotingSystemException;

    /**
     * Создать голосование за\против\воздержался
     * @param subject
     * @param description
     * @param isVoteCancellable
     * @param isVoteCommentsAllowed
     * @param index
     * @param isVisible
     * @param stopBatchVotingOnFailResult
     * @param addAbstain
     * @return
     * @throws VotingSystemException
     */
    Voting createVotingProContraAbstain(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                        long index, boolean isVisible, boolean stopBatchVotingOnFailResult, boolean addAbstain,
                                        Set<VotingButtonContentDto> modalButtons, String votingWinnerText, boolean multipleWinners,
                                        Map<String, String> additionalParameters, String successDecree, String failDecree,
                                        String sentence, int percentForWin) throws VotingSystemException;

    /**
     * Создать голосование за\против\воздержался
     * @param subject
     * @param description
     * @param isVoteCancellable
     * @param isVoteCommentsAllowed
     * @param index
     * @param isVisible
     * @param stopBatchVotingOnFailResult
     * @param addAbstain
     * @return
     * @throws VotingSystemException
     */
    Voting createVotingProContraAbstain(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                        long index, boolean isVisible, boolean stopBatchVotingOnFailResult, boolean multipleWinners,
                                        Map<String, String> additionalParameters,
                                        String successDecree, String failDecree,
                                        String sentence, boolean addAbstain, int percentForWin) throws VotingSystemException;

    /**
     * Создать голосование с множественным выбором
     * @param subject
     * @param description
     * @param isVoteCancellable
     * @param isVoteCommentsAllowed
     * @param index
     * @param isVisible
     * @param stopBatchVotingOnFailResult
     * @param variants
     * @param minSelectionCount
     * @param maxSelectionCount
     * @param minWinnersCount
     * @param maxWinnersCount
     * @param addAbstain
     * @param modalButtons
     * @param votingWinnerText
     * @param multipleWinners
     * @param additionalParameters
     * @param successDecree
     * @param failDecree
     * @param sentence
     * @param percentForWin
     * @return
     * @throws VotingSystemException
     */
    Voting createMultipleSelectionVoting(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                         long index, boolean isVisible, boolean stopBatchVotingOnFailResult, List<String> variants,
                                         long minSelectionCount, long maxSelectionCount,
                                         Long minWinnersCount, Long maxWinnersCount,
                                         boolean addAbstain,
                                         Set<VotingButtonContentDto> modalButtons, String votingWinnerText,
                                         boolean multipleWinners, Map<String, String> additionalParameters,
                                         String successDecree, String failDecree,
                                         String sentence, int percentForWin) throws VotingSystemException;

    /**
     * Создать голосование с множественным выбором
     * @param subject
     * @param description
     * @param isVoteCancellable
     * @param isVoteCommentsAllowed
     * @param index
     * @param isVisible
     * @param stopBatchVotingOnFailResult
     * @param variants
     * @param minSelectionCount
     * @param maxSelectionCount
     * @param minWinnersCount
     * @param maxWinnersCount
     * @param multipleWinners
     * @param additionalParameters
     * @param successDecree
     * @param failDecree
     * @param sentence
     * @param addAbstain
     * @param percentForWin
     * @return
     * @throws VotingSystemException
     */
    Voting createMultipleSelectionVoting(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                         long index, boolean isVisible, boolean stopBatchVotingOnFailResult, List<String> variants,
                                         long minSelectionCount, long maxSelectionCount,
                                         Long minWinnersCount, Long maxWinnersCount,
                                         boolean multipleWinners,
                                         Map<String, String> additionalParameters, String successDecree, String failDecree,
                                         String sentence, boolean addAbstain, int percentForWin) throws VotingSystemException;

    /**
     * Создать голосование с еденичным выбором
     * @param subject
     * @param description
     * @param isVoteCancellable
     * @param isVoteCommentsAllowed
     * @param index
     * @param isVisible
     * @param variants
     * @param addAbstain
     * @return
     * @throws VotingSystemException
     */
    Voting createSingleSelectionVoting(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                       long index, boolean isVisible, boolean stopBatchVotingOnFailResult, List<String> variants, boolean addAbstain,
                                       Set<VotingButtonContentDto> modalButtons, String votingWinnerText, boolean multipleWinners,
                                       Map<String, String> additionalParameters,
                                       String successDecree, String failDecree,
                                       String sentence, int percentForWin) throws VotingSystemException;

    /**
     * Создать голосование с еденичным выбором
     * @param subject
     * @param description
     * @param isVoteCancellable
     * @param isVoteCommentsAllowed
     * @param index
     * @param isVisible
     * @param variants
     * @param addAbstain
     * @return
     * @throws VotingSystemException
     */
    Voting createSingleSelectionVoting(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                       long index, boolean isVisible, boolean stopBatchVotingOnFailResult, List<String> variants, boolean multipleWinners,
                                       Map<String, String> additionalParameters,
                                       String successDecree, String failDecree,
                                       String sentence, boolean addAbstain, int percentForWin) throws VotingSystemException;

    /**
     * Создать голосование - интервью
     * @param subject
     * @param description
     * @param isVoteCancellable
     * @param isVoteCommentsAllowed
     * @param index
     * @param isVisible
     * @return
     * @throws VotingSystemException
     */
    Voting createInterviewVoting(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                 long index, boolean isVisible, boolean addAbstain,
                                 Set<VotingButtonContentDto> modalButtons, String votingWinnerText, boolean multipleWinners,
                                 Map<String, String> additionalParameters,
                                 String successDecree, String failDecree,
                                 String sentence) throws VotingSystemException;

    /**
     * Создать голосование - интервью
     * @param subject
     * @param description
     * @param isVoteCancellable
     * @param isVoteCommentsAllowed
     * @param index
     * @param isVisible
     * @param addAbstain
     * @return
     * @throws VotingSystemException
     */
    Voting createInterviewVoting(String subject, String description, boolean isVoteCancellable, boolean isVoteCommentsAllowed,
                                 long index, boolean isVisible, boolean multipleWinners, Map<String, String> additionalParameters,
                                 String successDecree, String failDecree,
                                 String sentence, boolean addAbstain) throws VotingSystemException;

    /**
     * Протокол голосования.
     * @param voting
     * @return
     */
    String getVotingProtocolString(Voting voting);

    /**
     * Протокол голосования.
     * @param voting
     * @param successDecree
     * @param failDecree
     * @return
     */
    String getVotingProtocolString(Voting voting, String successDecree, String failDecree);

    /**
     * Протокол голосования.
     * @param batchVoting
     * @param voting
     * @param successDecree
     * @param failDecree
     * @return
     */
    String getVotingProtocolString(BatchVoting batchVoting, Voting voting, String successDecree, String failDecree);

    /**
     * Председатель собрания в первом собрании
     * @param batchVoting
     * @return
     */
    User getPresidentOfFirstMeeting(BatchVoting batchVoting);

    /**
     * Секретарь собрания в первом собрании
     * @param batchVoting
     * @return
     */
    User getSecretaryOfFirstMeeting(BatchVoting batchVoting);

    /**
     * Председатель собрания во втором собрании
     * @param batchVoting
     * @return
     */
    User getPresidentOfSecondMeeting(BatchVoting batchVoting);

    /**
     * Секретарь собрания во втором собрании
     * @param batchVoting
     * @return
     */
    User getSecretaryOfSecondMeeting(BatchVoting batchVoting);

    /**
     * Выбранный Председатель КУч в собрании 2го этапа.
     * @param secondBatchVoting
     * @return
     */
    User getPresidentOfKuch(BatchVoting secondBatchVoting);

    /**
     * Выбранный Ревизор КУч в собрании 2го этапа.
     * @param secondBatchVoting
     * @return
     */
    User getRevisorOfKuch(BatchVoting secondBatchVoting);

    /**
     * Получить ПО из параметров собрания.
     * @param batchVoting
     * @return
     */
    Community getPOFromMeeting(BatchVoting batchVoting);

    /**
     * Получить КУч ПО из параметров собрания 2го этапа
     * @param secondBatchVoting
     * @return
     */
    Community getKuchFromSecondMeeting(BatchVoting secondBatchVoting);

    /**
     * Короткое имя КУч
     * @param createName имя, которое было установлено при создании КУч
     * @param parentCommunity родительское ПО
     * @return
     */
    String getShortCooperativePlotName(String createName, Community parentCommunity);

    /**
     * Полное имя КУч
     * @param createName имя, которое было установлено при создании КУч
     * @param parentCommunity родительское ПО
     * @return
     */
    String getFullCooperativePlotName(String createName, Community parentCommunity);

    /**
     * Полное имя КУч для поля при создании объединения
     * @param createName имя, которое было установлено при создании КУч
     * @param parentCommunity родительское ПО
     * @return
     */
    String getShortCooperativePlotNameForCreateCommunity(String createName, Community parentCommunity);

    /**
     * Полное имя КУч для поля при создании объединения
     * @param createName имя, которое было установлено при создании КУч
     * @param parentCommunity родительское ПО
     * @return
     */
    String getFullCooperativePlotNameForCreateCommunity(String createName, Community parentCommunity);

    /**
     * Декодинг адреса КУч, который был установлен при создании собрания
     * @param addressFieldsEncoded
     * @return
     * @throws UnsupportedEncodingException
     */
    @Deprecated
    List<Field> decodePlotAddress(String addressFieldsEncoded) throws UnsupportedEncodingException;

    /**
     *
     * @param voting
     * @return
     */
    List<VotingItem> getWinners(Voting voting);

    /**
     *
     * @param votingId
     * @return
     */
    List<VotingItem> getWinners(Long votingId);
}
