package ru.radom.kabinet.services.batchVoting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.voting.VoterVotingEvent;
import ru.askor.blagosfera.domain.events.voting.VotingPageEvent;
import ru.askor.blagosfera.domain.events.voting.VotingPageEventType;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.business.services.VotingService;
import ru.askor.voting.domain.*;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.services.batchVoting.dto.VotingPageCountVotersDto;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.web.voting.VotersComparator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для обработки действий на страницах собрания
 * Created by vgusev on 22.12.2015.
 */
@Service
@Transactional
public class VotingPageService {

    @Autowired
    private VotingService votingService;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    /**
     * Проверка прав - может ли пользователь выполнить рестарт голосования
     * @param voting голосование
     * @return true если может false если нет
     * @throws VotingSystemException
     */
    public boolean isCurrentSharerCanRestartVoting(Long userId, Voting voting) throws VotingSystemException {
        //boolean result = false;
        ExceptionUtils.check(userId == null, "Не установлен текущий пользователь");
        BatchVoting batchVoting = batchVotingService.getBatchVotingByVotingId(voting.getId(), true, true);
        return userId.equals(batchVoting.getOwnerId());
        /*
        BatchVoting batchVoting = batchVotingService.getBatchVotingByVotingId(voting.getId(), true, true);
        if (voting.getIndex() == 0) { // Если голосование за председателя собрания остановлено, то только создатель собрания имеет права
            if (currentSharer.getId().equals(batchVoting.getOwnerId())) {
                result = true;
            }
        } else { // Только выбранный председатель собрания может перезапустить собрание
            // Ищем председателя собрания
            if (batchVoting.getVotings().size() > 0 && batchVoting.getVotings().get(0).getVotingItems().size() > 0) {
                Long presidentId = VarUtils.getLong(batchVoting.getVotings().get(0).getVotingItems().get(0).getValue(), -1l);
                if (currentSharer.getId().equals(presidentId)) {
                    result = true;
                }
            }
        }
        return result;*/
    }

    /**
     *
     * @param votingId
     * @return
     * @throws VotingSystemException
     */
    public Set<Long> getVotersWhoVotes(Long votingId) throws VotingSystemException {
        Voting voting = votingService.getVoting(votingId, true, true);
        // Участники голосования, кто проголосовал
        Set<Long> votersWhoVotes = new HashSet<>();
        for (VotingItem votingItem : voting.getVotingItems()) {
            votersWhoVotes.addAll(votingItem.getVotes().stream().map(Vote::getOwnerId).collect(Collectors.toList()));
        }
        return votersWhoVotes;
    }

    /**
     * Список участников, которые уже зарегистрировались
     * @param batchVotingId
     * @return
     */
    public List<UserEntity> getRegisteredVoters(Long userId, Long batchVotingId, Integer firstIndex, Integer count) throws VotingSystemException {
        List<UserEntity> registeredUserEntities = getRegisteredSharers(userId, batchVotingId);
        int lastIndex = registeredUserEntities.size();
        if (count != null) {
            lastIndex = firstIndex + count;
        }
        if (lastIndex > registeredUserEntities.size()) {
            lastIndex = registeredUserEntities.size();
        }
        return registeredUserEntities.subList(firstIndex, lastIndex);

    }

    /**
     * Получить всех пользователей, которые уже зарегистрировались
     * @param batchVotingId
     * @return
     * @throws Exception
     */
    public List<UserEntity> getRegisteredSharers(Long userId, Long batchVotingId) throws VotingSystemException {
        BatchVoting batchVoting = batchVotingService.getBatchVoting(batchVotingId, false, false);
        Set<Long> registeredVoters = batchVoting.getVotings().get(0).getParameters().getVotersAllowed();

        List<Long> registeredVotersList = new ArrayList<>();
        registeredVotersList.addAll(registeredVoters);

        List<UserEntity> registeredUserEntities = sharerDao.getByIds(registeredVotersList);
        Collections.sort(registeredUserEntities, new VotersComparator(sharerDao.getById(userId)));

        return registeredUserEntities;
    }

    /**
     * Список участников, которые ещё не зарегистрировались
     * @param batchVotingId
     * @return
     */
    public List<UserEntity> getNotRegisteredVoters(Long userId, Long batchVotingId, Integer firstIndex, Integer count) throws VotingSystemException {
        List<UserEntity> notRegisteredUserEntities = getNotRegisteredSharers(userId, batchVotingId);

        int lastIndex = notRegisteredUserEntities.size();
        if (count != null) {
            lastIndex = firstIndex + count;
        }
        if (lastIndex > notRegisteredUserEntities.size()) {
            lastIndex = notRegisteredUserEntities.size();
        }
        return notRegisteredUserEntities.subList(firstIndex, lastIndex);
    }

    /**
     * Получить всех пользователей, которые ещё не зарегистрировались
     * @param batchVotingId
     * @return
     * @throws Exception
     */
    public List<UserEntity> getNotRegisteredSharers(Long userId, Long batchVotingId) throws VotingSystemException {
        BatchVoting batchVoting = batchVotingService.getBatchVoting(batchVotingId, false, false);
        Set<Long> registeredVoters = batchVoting.getVotings().get(0).getParameters().getVotersAllowed();

        Set<RegisteredVoter> allVoters = batchVoting.getVotersAllowed();
        Set<Long> notRegisteredVoters = allVoters.stream().filter(voter ->
                !registeredVoters.contains(voter.getVoterId())).map(RegisteredVoter::getVoterId).collect(Collectors.toSet());

        List<Long> notRegisteredVotersList = new ArrayList<>();
        notRegisteredVotersList.addAll(notRegisteredVoters);

        List<UserEntity> notRegisteredUserEntities = sharerDao.getByIds(notRegisteredVotersList);
        Collections.sort(notRegisteredUserEntities, new VotersComparator(sharerDao.getById(userId)));

        return notRegisteredUserEntities;
    }


    /**
     * Получить количество участников
     * @param batchVotingId
     * @return
     */
    public VotingPageCountVotersDto getCountVoters(User currentUser, Long batchVotingId) throws VotingSystemException {
        int countRegisteredVoters = 0;
        List<UserEntity> registeredVoters = getRegisteredSharers(currentUser.getId(), batchVotingId);
        if (registeredVoters != null) {
            countRegisteredVoters = registeredVoters.size();
        }
        int countNotRegisteredVoters = 0;
        List<UserEntity> notRegisteredVoters = getNotRegisteredSharers(currentUser.getId(), batchVotingId);
        if (notRegisteredVoters != null) {
            countNotRegisteredVoters = notRegisteredVoters.size();
        }

        return new VotingPageCountVotersDto(countRegisteredVoters, countNotRegisteredVoters);
    }

    /**
     * Поторопить участника регистрации в собрании.
     * @param batchVotingId
     * @param voterId
     * @return
     */
    public void hurryVoter(User sender, Long batchVotingId, Long voterId) throws VotingSystemException {
        UserEntity receiver = sharerDao.getById(voterId);

        ExceptionUtils.check(sender == null, "Не найден текущий пользователь");
        ExceptionUtils.check(receiver == null, "Не передан участник собрания");
        ExceptionUtils.check(sender.getId().equals(receiver.getId()), "Нельзя поторопить себя!");

        BatchVoting batchVoting = batchVotingService.getBatchVoting(batchVotingId, false, false);

        ExceptionUtils.check(batchVoting == null, "Собрание не найдено!");

        boolean isHasRightToVoting = false;
        if (batchVoting.getVotersAllowed() != null) {
            for (RegisteredVoter registeredVoter : batchVoting.getVotersAllowed()) {
                if (registeredVoter.getVoterId().equals(sender.getId())) {
                    isHasRightToVoting = true;
                }
            }
        }
        ExceptionUtils.check(!isHasRightToVoting, "У Вас нет прав на данное собрание!");

        Set<Long> registeredVoters = batchVoting.getVotings().get(0).getParameters().getVotersAllowed();

        Set<RegisteredVoter> allVoters = batchVoting.getVotersAllowed();
        Set<Long> notRegisteredVoters = allVoters.stream().filter(voter ->
                !registeredVoters.contains(voter.getVoterId())).map(RegisteredVoter::getVoterId).collect(Collectors.toSet());

        ExceptionUtils.check(!notRegisteredVoters.contains(voterId), "Участник уже зарегистрировался!");

        // Отправить оповещение пользователю о регистрации
        blagosferaEventPublisher.publishEvent(new VoterVotingEvent(this, batchVoting, sender, receiver.toDomain()));
    }

    /**
     * Поторопить участника в голосовании
     * @param votingId
     * @param voterId
     * @return
     */
    public void hurryVoterInVoting(User sender, Long votingId, Long voterId) throws VotingSystemException {
        UserEntity receiver = sharerDao.getById(voterId);

        ExceptionUtils.check(sender == null, "Не найден текущий пользователь");
        ExceptionUtils.check(receiver == null, "Не передан участник собрания");
        ExceptionUtils.check(sender.getId().equals(receiver.getId()), "Нельзя поторопить себя!");

        Voting voting = votingService.getVoting(votingId, true, true);

        // Ищем голос среди всех голосов
        boolean voterIsVote = false;
        if (voting != null && voting.getVotingItems() != null) {
            for (VotingItem votingItem : voting.getVotingItems()) {
                if (votingItem.getVotes() != null) {
                    for (Vote vote : votingItem.getVotes()) {
                        if (vote.getOwnerId().equals(voterId)) {
                            voterIsVote = true;
                            break;
                        }
                    }
                }
                if (voterIsVote) {
                    break;
                }
            }
        }
        ExceptionUtils.check(voting == null, "Голосование не найдено!");
        ExceptionUtils.check(voterIsVote, "Участник уже проголосовал!");

        // отправляем уведомление
        blagosferaEventPublisher.publishEvent(new VoterVotingEvent(this, voting, sender, receiver.toDomain()));
    }

    /**
     * Перезапустить голосование
     * @param votingId
     * @return
     */
    public void restartVoting(Long userId, Long votingId) throws VotingSystemException {
        Voting voting = votingService.getVoting(votingId, true, true);
        ExceptionUtils.check(!isCurrentSharerCanRestartVoting(userId, voting), "У Вас нет прав на перезапуск данного голосования!");
        votingService.start(votingId);
        blagosferaEventPublisher.publishEvent(new VotingPageEvent(this, voting, VotingPageEventType.RESTART_VOTING));
    }

    /**
     * Завершить голосование и собрание
     * @param votingId
     * @return
     */
    public void finishVoting(Long userId, Long votingId) throws VotingSystemException {
        Voting voting = votingService.getVoting(votingId, true, true);
        ExceptionUtils.check(!isCurrentSharerCanRestartVoting(userId, voting), "У Вас нет прав на завершение данного голосования!");
        votingService.finish(votingId, true, true);
    }
}
