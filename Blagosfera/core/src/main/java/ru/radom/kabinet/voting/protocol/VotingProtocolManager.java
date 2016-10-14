package ru.radom.kabinet.voting.protocol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.Voting;

import java.util.List;

/**
 *
 * Created by vgusev on 25.05.2016.
 */
@Service
@Transactional
public class VotingProtocolManager {

    private List<VotingProtocol> votingProtocols;

    @Autowired
    public void setVotingProtocols(List<VotingProtocol> votingProtocols) {
        this.votingProtocols = votingProtocols;
    }

    private VotingProtocol getVotingProtocol(BatchVoting batchVoting) {
        VotingProtocol result = null;
        if (votingProtocols != null && batchVoting != null && batchVoting.getParameters().getBehavior() != null) {
            for (VotingProtocol votingProtocol : votingProtocols) {
                if (batchVoting.getParameters().getBehavior().equals(votingProtocol.getBatchVotingBehavior())) {
                    result = votingProtocol;
                    break;
                }
            }
        }
        return result;
    }

    public String getBatchVotingProtocolString(BatchVoting batchVoting) {
        String result = null;
        VotingProtocol votingProtocol = getVotingProtocol(batchVoting);
        if (votingProtocol != null) {
            result = votingProtocol.getBatchVotingProtocol(batchVoting);
        }
        return result;
    }

    public String getVotingProtocolString(BatchVoting batchVoting, Voting voting) {
        return getVotingProtocolString(batchVoting, voting, null, null);
    }

    public String getVotingProtocolString(BatchVoting batchVoting, Voting voting, String successDecree, String failDecree) {
        String result = null;
        VotingProtocol votingProtocol = getVotingProtocol(batchVoting);
        if (votingProtocol != null) {
            result = votingProtocol.getVotingProtocol(batchVoting, voting, successDecree, failDecree);
        }
        return result;
    }
}
