package ru.radom.kabinet.voting.protocol;

import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.Voting;

/**
 *
 * Created by vgusev on 25.05.2016.
 */
public interface VotingProtocol {

    String getBatchVotingProtocol(BatchVoting batchVoting);

    String getVotingProtocol(BatchVoting batchVoting, Voting voting);

    String getVotingProtocol(BatchVoting batchVoting, Voting voting, String successDecree, String failDecree);

    String getBatchVotingBehavior();
}
