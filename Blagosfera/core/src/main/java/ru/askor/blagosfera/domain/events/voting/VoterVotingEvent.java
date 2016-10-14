package ru.askor.blagosfera.domain.events.voting;

import java.time.LocalDateTime;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.Voting;

/**
 * События участников в собрании
 * Created by vgusev on 14.08.2015.
 */
public class VoterVotingEvent extends BlagosferaEvent {

    private BatchVoting batchVoting;
    private Voting voting;
    private User sender;
    private User receiver;
    private LocalDateTime createdDate = LocalDateTime.now();

    public VoterVotingEvent(Object source, BatchVoting batchVoting, User sender, User receiver) {
        super(source);
        this.batchVoting = batchVoting;
        this.sender = sender;
        this.receiver = receiver;
    }

    public VoterVotingEvent(Object source, Voting voting, User sender, User receiver) {
        super(source);
        this.voting = voting;
        this.sender = sender;
        this.receiver = receiver;
    }

    public BatchVoting getBatchVoting() {
        return batchVoting;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public Voting getVoting() {
        return voting;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
}
