package ru.askor.blagosfera.data.jpa.repositories.batchvotingtemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.votingtemplate.VoterAllowedTemplate;
import ru.radom.kabinet.model.votingtemplate.VotingTemplateEntity;

/**
 *
 * Created by vgusev on 30.05.2015.
 */
public interface VoterAllowedTemplateRepository extends JpaRepository<VoterAllowedTemplate, Long> {
}
