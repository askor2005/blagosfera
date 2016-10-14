package ru.radom.kabinet.web.voting.dto;

import lombok.Data;
import ru.askor.blagosfera.core.util.DateUtils;
import ru.askor.voting.domain.Vote;

import java.time.LocalDateTime;

/**
 *
 * Created by vgusev on 11.05.2016.
 */
@Data
public class VoteItemProtocolDto {

    private Long id;
    private Long voterId;
    private String voterName;
    private LocalDateTime voteDateTime;

    public VoteItemProtocolDto(Vote vote, String voterName) {
        setId(vote.getId());
        setVoterId(vote.getOwnerId());
        setVoterName(voterName);
        setVoteDateTime(vote.getCreated());
    }
}
