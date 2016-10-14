package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.Voting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 11.05.2016.
 */
@Data
public class CommunityVotingsPageDataDto {

    private List<VotingItemDto> votings;

    private String baseLink;

    public CommunityVotingsPageDataDto(
            List<BatchVoting> batchVotings,
            Map<Long, User> owners,
            String baseLink) {
        List<VotingItemDto> votingsDto;
        if (batchVotings != null) {
            votingsDto = new ArrayList<>();
            for (BatchVoting batchVoting : batchVotings) {
                votingsDto.addAll(VotingItemDto.toListDto(batchVoting, owners.get(batchVoting.getId())));

            }
            setVotings(votingsDto);
        }
        setBaseLink(baseLink);
    }
}
