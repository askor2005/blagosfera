package ru.radom.kabinet.services.bpmhandlers.voting.dto;

import org.springframework.util.ReflectionUtils;
import ru.askor.blagosfera.core.util.DateUtils;
import ru.askor.voting.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Класс, расширяющий класс модели BatchVoting для удобства получения свойств в модели bpm
 * Created by vgusev on 13.12.2015.
 */
public class BPMBatchVotingExtended extends BatchVoting {

    public BPMBatchVotingExtended(BatchVoting sourceBatchVoting) {
        ReflectionUtils.shallowCopyFieldState(sourceBatchVoting, this);
    }

    /**
     * ИДы участников собрания для bpm модели
     * @return список ИДов
     */
    @SuppressWarnings("unused")
    public List<Long> getVotersIds() {
        List<Long> result = new ArrayList<>();
        Set<RegisteredVoter> registeredVoters = this.getVotersAllowed();
        if (registeredVoters != null) {
            for (RegisteredVoter registeredVoter : registeredVoters) {
                if (registeredVoter.getStatus() == RegisteredVoterStatus.REGISTERED) {
                    result.add(registeredVoter.getVoterId());
                }
            }
        }
        return result;
    }

    /**
     * Получить дату последнего голоса
     * @return
     */
    public Long getEndBatchVotingTimeStamp() {
        Long result = -1l;
        if (getVotings() != null) {
            for (Voting voting : getVotings()) {
                if (voting.getVotingItems() != null) {
                    for (VotingItem votingItem : voting.getVotingItems()) {
                        if (votingItem.getVotes() != null) {
                            for (Vote vote : votingItem.getVotes()) {
                                if (vote != null && vote.getCreated() != null) {
                                    if (DateUtils.toDate(vote.getCreated()).getTime() > result) {
                                        result = DateUtils.toDate(vote.getCreated()).getTime();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
