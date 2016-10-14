package ru.askor.blagosfera.domain.events.voting;

import lombok.Getter;
import ru.askor.voting.business.event.VotingEvent;
import ru.askor.voting.domain.Voting;

/**
 * Created by vgusev on 22.12.2015.
 */
@Getter
public class VotingPageEvent extends VotingEvent {

    private VotingPageEventType votingPageEventType;

    public VotingPageEvent(Object source, Voting voting, VotingPageEventType votingPageEventType) {
        super(source, voting);
        this.votingPageEventType = votingPageEventType;
    }
}
