package ru.askor.blagosfera.data.jpa.repositories.batchvotingtemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.votingtemplate.VotingAttributeTemplate;

/**
 *
 * Created by vgusev on 13.10.2015.
 */
public interface VotingAttributeTemplateRepository extends JpaRepository<VotingAttributeTemplate, Long> {
}
