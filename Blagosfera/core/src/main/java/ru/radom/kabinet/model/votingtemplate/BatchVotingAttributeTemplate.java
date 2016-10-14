package ru.radom.kabinet.model.votingtemplate;

import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;

/**
 *
 * Created by vgusev on 13.10.2015.
 */
@Entity
@Table(name = "batch_voting_attribute_templates")
public class BatchVotingAttributeTemplate extends LongIdentifiable {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "value", nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_voting_id", nullable = false)
    private BatchVotingTemplateEntity batchVoting;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BatchVotingTemplateEntity getBatchVoting() {
        return batchVoting;
    }

    public void setBatchVoting(BatchVotingTemplateEntity batchVoting) {
        this.batchVoting = batchVoting;
    }
}
