package ru.askor.blagosfera.domain.events.voting;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.user.User;

public class VoterErrorEvent extends BlagosferaEvent {

    private User voter;
    private String error;

    public VoterErrorEvent(Object source, User voter, String error) {
        super(source);
        this.voter = voter;
        this.error = error;
    }

    public User getVoter() {
        return voter;
    }

    public String getError() {
        return error;
    }
}
