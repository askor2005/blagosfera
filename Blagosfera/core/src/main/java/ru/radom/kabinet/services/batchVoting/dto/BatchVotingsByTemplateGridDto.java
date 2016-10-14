package ru.radom.kabinet.services.batchVoting.dto;

import lombok.Getter;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.mvc.dto.BatchVotingDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 14.10.2015.
 */
@Getter
public class BatchVotingsByTemplateGridDto {

    private boolean success = true;

    private int total = 0;

    private List<BatchVotingDto> items = new ArrayList<>();

    public BatchVotingsByTemplateGridDto(boolean success, int total, List<BatchVotingDto> items) {
        this.success = success;
        this.total = total;
        this.items = items;
    }

    public static BatchVotingsByTemplateGridDto successDtoFromDomain(int count, List<BatchVoting> batchVotings) {
        List<BatchVotingDto> batchVotingDtos = new ArrayList<>();
        if (batchVotings != null) {
            batchVotingDtos.addAll(batchVotings.stream().map(batchVoting -> new BatchVotingDto(batchVoting, false)).collect(Collectors.toList()));
        }
        return new BatchVotingsByTemplateGridDto(true, count, batchVotingDtos);
    }

    public static BatchVotingsByTemplateGridDto failDto() {
        return new BatchVotingsByTemplateGridDto(false, 0, null);
    }
}
