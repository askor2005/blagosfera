package ru.radom.kabinet.services.batchVoting.dto;

import lombok.Getter;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.RegisteredVoterStatus;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 22.05.2016.
 */
@Getter
public class BatchVotingVotersPageResultDto {

    private long count;

    private List<User> users;

    private Map<Long, RegisteredVoterStatus> userStates;

    public BatchVotingVotersPageResultDto(long count, List<User> users, Map<Long, RegisteredVoterStatus> userStates) {
        this.count = count;
        this.users = users;
        this.userStates = userStates;
    }
}
