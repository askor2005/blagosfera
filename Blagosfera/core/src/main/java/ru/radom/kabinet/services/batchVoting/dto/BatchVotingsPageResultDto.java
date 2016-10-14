package ru.radom.kabinet.services.batchVoting.dto;

import lombok.Getter;
import ru.askor.voting.domain.BatchVoting;

import java.util.List;
import java.util.Map;

/**
 * Обёртка с коллекцией собраний на странице и количеством всех собраний
 * Created by vgusev on 19.05.2016.
 */
@Getter
public class BatchVotingsPageResultDto {

    private long count;

    private List<BatchVoting> batchVotings;

    private Map<Long, String> templateLinks;

    public BatchVotingsPageResultDto(long count, List<BatchVoting> batchVotings, Map<Long, String> templateLinks) {
        this.count = count;
        this.batchVotings = batchVotings;
        this.templateLinks = templateLinks;
    }
}
