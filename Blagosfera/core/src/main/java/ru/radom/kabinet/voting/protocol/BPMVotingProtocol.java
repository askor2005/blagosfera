package ru.radom.kabinet.voting.protocol;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.kabinet.services.bpmhandlers.voting.BPMBatchVotingBehavior;

/**
 *
 * Created by vgusev on 30.06.2016.
 */
@Service
@Transactional
public class BPMVotingProtocol extends DefaultVotingProtocol {

    @Override
    public String getBatchVotingBehavior() {
        return BPMBatchVotingBehavior.NAME;
    }
}
