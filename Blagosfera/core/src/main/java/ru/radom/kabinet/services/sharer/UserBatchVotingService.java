package ru.radom.kabinet.services.sharer;

import ru.askor.voting.domain.BatchVotingState;
import ru.askor.voting.domain.RegisteredVoterStatus;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingVotersPageResultDto;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingsPageResultDto;

import java.util.Date;
import java.util.Map;

/**
 *
 * Created by vgusev on 23.05.2016.
 */
public interface UserBatchVotingService {

    /**
     * Фильтрация собраний пользователя
     * @param ownerId
     * @param voterId
     * @param startDateStart
     * @param startDateEnd
     * @param endDateStart
     * @param endDateEnd
     * @param parameters
     * @param batchVotingState
     * @param subject
     * @param page
     * @param currentUserId
     * @return
     */
    BatchVotingsPageResultDto filterBatchVotings(
            Long ownerId,
            Long voterId, Date startDateStart, Date startDateEnd, Date endDateStart, Date endDateEnd,
            Map<String, String> parameters, BatchVotingState batchVotingState, String subject, int page, Long currentUserId);

    /**
     *
     * @param batchVotingId
     * @param registeredVoterStatus
     * @param voterName
     * @param page
     * @return
     * @throws VotingSystemException
     */
    BatchVotingVotersPageResultDto filterBatchVotingVoters(Long batchVotingId, RegisteredVoterStatus registeredVoterStatus, String voterName, int page) throws VotingSystemException;
}
