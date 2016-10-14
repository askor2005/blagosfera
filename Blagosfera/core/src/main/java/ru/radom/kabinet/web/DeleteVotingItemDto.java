package ru.radom.kabinet.web;

/**
 * Created by vtarasenko on 05.06.2016.
 */
public class DeleteVotingItemDto {
    private Long votingItemId;
    private Long votingId;

    public Long getVotingItemId() {
        return votingItemId;
    }

    public void setVotingItemId(Long votingItemId) {
        this.votingItemId = votingItemId;
    }

    public Long getVotingId() {
        return votingId;
    }

    public void setVotingId(Long votingId) {
        this.votingId = votingId;
    }
}
