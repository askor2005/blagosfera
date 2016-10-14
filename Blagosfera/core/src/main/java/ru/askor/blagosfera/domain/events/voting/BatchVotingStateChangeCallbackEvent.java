package ru.askor.blagosfera.domain.events.voting;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.radom.kabinet.voting.BatchVotingStateChangeCallback;

public class BatchVotingStateChangeCallbackEvent extends BlagosferaEvent {

    private long batchVotingId;
    private BatchVotingStateChangeCallback callback;

    public BatchVotingStateChangeCallbackEvent(Object source, long batchVotingId, BatchVotingStateChangeCallback callback) {
        super(source);
        this.batchVotingId = batchVotingId;
        this.callback = callback;
    }

    public void doCallback() {
        callback.batchVotingStateChangeCallback(batchVotingId);
    }
}
