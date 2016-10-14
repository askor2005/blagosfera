package ru.radom.kabinet.services.batchVoting.dto;

import ru.radom.kabinet.model.votingtemplate.VotingItemTemplate;

/**
 * Вариант голосования
 * Created by vgusev on 12.10.2015.
 */
public class VotingItemTemplateDto {

    public Long id;

    // Значение варианта
    public String value;

    public VotingItemTemplateDto() {
    }

    public VotingItemTemplateDto(VotingItemTemplate votingItemTemplate) {
        id = votingItemTemplate.getId();
        value = votingItemTemplate.getValue();
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
