package ru.radom.kabinet.services.bpmhandlers.voting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.askor.voting.business.services.BatchVotingService;
import ru.radom.kabinet.voting.BatchVotingConstants;
import ru.radom.kabinet.voting.BatchVotingEventListener;
import ru.radom.kabinet.voting.StandardBatchVotingBehavior;

import javax.annotation.PostConstruct;

/**
 * Поведение собрания для BPM таска собрания
 * Created by vgusev on 11.12.2015.
 */
@Component
public class BPMBatchVotingBehavior extends StandardBatchVotingBehavior {

    public static final String NAME = "BPMBatchVotingBehavior";

    @Autowired
    private BatchVotingService batchVotingService;

    @PostConstruct
    public void init() {
        batchVotingService.registerBatchVotingBehavior(NAME, this);
    }
}
