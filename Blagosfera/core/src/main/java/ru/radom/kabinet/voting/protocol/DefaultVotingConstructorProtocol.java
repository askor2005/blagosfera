package ru.radom.kabinet.voting.protocol;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.kabinet.voting.BatchVotingConstants;
import ru.radom.kabinet.voting.StandardBatchVotingBehavior;

/**
 * Создание протокола для стандартного поведения собрания созданное конструктором
 * Created by vgusev on 25.05.2016.
 */
@Service
@Transactional
public class DefaultVotingConstructorProtocol extends DefaultVotingProtocol {

    @Override
    public String getBatchVotingBehavior() {
        return BatchVotingConstants.STANDARD_BEHAVIOR_NAME;
    }
}
