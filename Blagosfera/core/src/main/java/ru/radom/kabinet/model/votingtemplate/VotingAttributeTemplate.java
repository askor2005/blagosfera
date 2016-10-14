package ru.radom.kabinet.model.votingtemplate;

import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;

/**
 *
 * Created by vgusev on 13.10.2015.
 */
@Entity
@Table(name = "voting_attribute_templates")
public class VotingAttributeTemplate extends LongIdentifiable {

    public static final String ADD_ABSTAIN = "ADD_ABSTAIN";
    public static final String USE_BIOMETRIC_IDENTIFICATION = "USE_BIOMETRIC_IDENTIFICATION";
    public static final String ADD_CHAT_TO_PROTOCOL = "ADD_CHAT_TO_PROTOCOL";
    public static final String SKIP_RESULTS = "SKIP_RESULTS";
    public static final String NOT_RESTARTABLE = "NOT_RESTARTABLE";

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "value", nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voting_id", nullable = false)
    private VotingTemplateEntity voting;

    public VotingAttributeTemplate() {
    }

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

    public VotingTemplateEntity getVoting() {
        return voting;
    }

    public void setVoting(VotingTemplateEntity voting) {
        this.voting = voting;
    }
}
