package ru.radom.kabinet.web.communities.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.askor.blagosfera.core.util.DateUtils;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.BatchVotingState;
import ru.askor.voting.domain.RegisteredVoter;
import ru.askor.voting.domain.RegisteredVoterStatus;
import ru.radom.kabinet.json.TimeStampDateSerializer;

import java.util.Date;

/**
 *
 * Created by vgusev on 11.05.2016.
 */
@Data
public class BatchVotingItemDto {

    private Long id;
    private String subject;
    private String ownerIkp;
    private Long ownerId;
    private String shortOwnerName;
    private String ownerName;
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date startDate;
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date endDate;
    private Long votingId;
    private BatchVotingState state;
    private int votersCount;
    private int registeredVotersCount;
    private String templateLink;

    public BatchVotingItemDto(BatchVoting batchVoting, User owner, String templateLink) {
        setId(batchVoting.getId());
        //setDescription(batchVoting.getAdditionalData().get(CommonVotingService.VOTING_DESCRIPTION));
        setSubject(batchVoting.getSubject());
        setStartDate(DateUtils.toDate(batchVoting.getParameters().getStartDate()));
        setEndDate(DateUtils.toDate(batchVoting.getParameters().getEndDate()));
        setOwnerId(owner.getId());
        setOwnerIkp(owner.getIkp());
        setShortOwnerName(owner.getShortName());
        setOwnerName(owner.getName());
        setState(batchVoting.getState());
        if (batchVoting.getVotings() != null && !batchVoting.getVotings().isEmpty()) {
            setVotingId(batchVoting.getVotings().get(0).getId());
        }
        setVotersCount(batchVoting.getVotersAllowed().size());
        int registeredVotersCount = 0;
        if (batchVoting.getVotersAllowed() != null) {
            for (RegisteredVoter registeredVoter : batchVoting.getVotersAllowed()) {
                if (RegisteredVoterStatus.REGISTERED.equals(registeredVoter.getStatus())) {
                    registeredVotersCount++;
                }
            }
        }
        setRegisteredVotersCount(registeredVotersCount);
        setTemplateLink(templateLink);
    }
}
