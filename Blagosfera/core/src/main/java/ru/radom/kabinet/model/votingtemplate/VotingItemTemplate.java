package ru.radom.kabinet.model.votingtemplate;

import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;

/**
 *
 * Created by vgusev on 13.10.2015.
 */
@Entity
@Table(name = "voting_item_templates")
public class VotingItemTemplate extends LongIdentifiable {

    // Значение варианта
    @Column(name = "value", nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voting_id", nullable = false)
    private VotingTemplateEntity voting;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public VotingTemplateEntity getVoting() {
        return voting;
    }

    public void setVoting(VotingTemplateEntity voting) {
        this.voting = voting;
    }
}
