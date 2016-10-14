package ru.radom.kabinet.web.communities.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.Voting;
import ru.radom.kabinet.json.FullDateSerializer;
import ru.radom.kabinet.voting.BatchVotingConstants;
import ru.radom.kabinet.voting.CommonVotingService;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Обёртка голосования для страницы списка голосований в объединении
 * Created by vgusev on 12.05.2016.
 */
@Data
public class VotingItemDto {

    private Long id;
    private String subject;
    private String description;
    private String ownerIkp;
    private String ownerName;
    @JsonSerialize(using = FullDateSerializer.class)
    private Date startDate;
    @JsonSerialize(using = FullDateSerializer.class)
    private Date endDate;

    public static List<VotingItemDto> toListDto(BatchVoting batchVoting, User owner) {
        List<VotingItemDto> result = null;
        if (batchVoting != null && batchVoting.getVotings() != null) {
            result = new ArrayList<>();
            for (Voting voting : batchVoting.getVotings()) {
                VotingItemDto votingItemDto = new VotingItemDto();
                votingItemDto.setId(voting.getId());
                votingItemDto.setSubject(voting.getSubject());
                votingItemDto.setDescription(batchVoting.getAdditionalData().get(BatchVotingConstants.VOTING_DESCRIPTION));
                votingItemDto.setOwnerIkp(owner.getIkp());
                votingItemDto.setOwnerName(owner.getName());




                votingItemDto.setStartDate(Date.from(voting.getParameters().getStartDate().atZone(ZoneId.systemDefault()).toInstant()));
                votingItemDto.setEndDate(Date.from(voting.getParameters().getEndDate().atZone(ZoneId.systemDefault()).toInstant()));
                result.add(votingItemDto);
            }
        }
        return result;
    }
}
