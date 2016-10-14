package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.BatchVoting;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingVotersPageResultDto;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingsPageResultDto;
import ru.radom.kabinet.web.user.dto.UserDataDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 11.05.2016.
 */
@Data
public class BatchVotingVotersGridDto {

    private boolean success = true;

    private long total = 0;

    private List<BatchVoterUserDto> items = new ArrayList<>();

    public BatchVotingVotersGridDto(boolean success, long total, List<BatchVoterUserDto> items) {
        this.success = success;
        this.total = total;
        this.items = items;
    }

    public static BatchVotingVotersGridDto successDto(BatchVotingVotersPageResultDto batchVotingVotersPageResult) {
        List<BatchVoterUserDto> batchVoterUsers = new ArrayList<>();
        if (batchVotingVotersPageResult.getUsers() != null) {
            for (User user : batchVotingVotersPageResult.getUsers()) {
                batchVoterUsers.add(new BatchVoterUserDto(user, batchVotingVotersPageResult.getUserStates().get(user.getId())));
            }
        }
        return new BatchVotingVotersGridDto(true, batchVotingVotersPageResult.getCount(), batchVoterUsers);
    }

    public static BatchVotingVotersGridDto failDto() {
        return new BatchVotingVotersGridDto(false, 0, null);
    }
}
