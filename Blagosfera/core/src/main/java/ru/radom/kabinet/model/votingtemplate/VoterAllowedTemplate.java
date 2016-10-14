package ru.radom.kabinet.model.votingtemplate;

import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;

/**
 *
 * Created by vgusev on 30.05.2016.
 */
@Entity
@Table(name = "voters_allowed_templates")
public class VoterAllowedTemplate extends LongIdentifiable {

    @Column(name = "voter_id")
    private Long voterId;

    @Column(name = "sign_protocol")
    private Boolean isSignProtocol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_voting_id")
    private BatchVotingTemplateEntity batchVotingTemplate;

    public Long getVoterId() {
        return voterId;
    }

    public void setVoterId(Long voterId) {
        this.voterId = voterId;
    }

    public Boolean getSignProtocol() {
        return isSignProtocol;
    }

    public void setSignProtocol(Boolean signProtocol) {
        isSignProtocol = signProtocol;
    }

    public BatchVotingTemplateEntity getBatchVotingTemplate() {
        return batchVotingTemplate;
    }

    public void setBatchVotingTemplate(BatchVotingTemplateEntity batchVotingTemplate) {
        this.batchVotingTemplate = batchVotingTemplate;
    }
}
