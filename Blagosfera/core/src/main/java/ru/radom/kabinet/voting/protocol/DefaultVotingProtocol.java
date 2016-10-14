package ru.radom.kabinet.voting.protocol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.business.util.DefaultBatchVotingBehavior;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.Vote;
import ru.askor.voting.domain.Voting;
import ru.askor.voting.domain.VotingItem;
import ru.radom.kabinet.services.script.ScriptEngineService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.VarUtils;

import java.util.*;

/**
 * Создание протокола для стандартного поведения собрания
 * Created by vgusev on 25.05.2016.
 */
@Service
@Transactional
public class DefaultVotingProtocol implements VotingProtocol {
    private static final String VOTING_PROTOCOL_SCRIPT_SETTINGS_ATTR_NAME = "voting.protocol.script";

    @Autowired
    private ScriptEngineService scriptEngineService;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private UserDataService userDataService;

    private List<User> getUsers(Voting voting) {
        Set<Long> userIds = new HashSet<>();
        for (VotingItem votingItem : voting.getVotingItems()) {
            if (votingItem.getVotes() != null) {
                for (Vote vote : votingItem.getVotes()) {
                    userIds.add(vote.getOwnerId());
                }
            }
            switch (voting.getParameters().getVotingType()) {
                case CANDIDATE:  // Голосование за кандидата
                    Long candidateId = VarUtils.getLong(votingItem.getValue(), null);
                    if (candidateId != null) {
                        userIds.add(candidateId);
                    }
                    break;
            }
        }
        return userDataService.getByIds(new ArrayList<>(userIds));
    }

    @Override
    public String getBatchVotingBehavior() {
        return DefaultBatchVotingBehavior.NAME;
    }

    @Override
    public String getBatchVotingProtocol(BatchVoting batchVoting) {
        return null;
    }

    @Override
    public String getVotingProtocol(BatchVoting batchVoting, Voting voting) {
        return getVotingProtocol(batchVoting, voting, null, null);
    }

    @Override
    public String getVotingProtocol(BatchVoting batchVoting, Voting voting, String successDecree, String failDecree) {
        String votingProtocolScript = settingsManager.getSystemSetting(VOTING_PROTOCOL_SCRIPT_SETTINGS_ATTR_NAME, null);
        ExceptionUtils.check(votingProtocolScript == null, "Не найдена настройка - скрипт для создания строки протокола собрания");
        List<User> users = getUsers(voting);
        Map<String, Object> scriptVars = new HashMap<>();
        scriptVars.put("batchVoting", batchVoting);
        scriptVars.put("voting", voting);
        scriptVars.put("users", users);
        scriptVars.put("successDecree", successDecree);
        scriptVars.put("failDecree", failDecree);
        return scriptEngineService.runScript(votingProtocolScript, "protocol", scriptVars);
    }
}
