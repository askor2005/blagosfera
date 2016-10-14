package ru.radom.kabinet.services.bpmhandlers.voting.dto;

import lombok.Data;

import java.util.Map;

/**
 *
 * Created by vgusev on 01.07.2016.
 */
@Data
public class BPMBatchVotingAddParametersDto {

    private Long batchVotingId;

    private Map<String, String> additionalParameters;
}
