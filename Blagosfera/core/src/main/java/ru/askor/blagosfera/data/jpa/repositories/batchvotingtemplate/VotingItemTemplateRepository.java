package ru.askor.blagosfera.data.jpa.repositories.batchvotingtemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.votingtemplate.VotingItemTemplate;

/**
 *
 * Created by vgusev on 13.10.2015.
 */
public interface VotingItemTemplateRepository extends JpaRepository<VotingItemTemplate, Long> {
}
