package ru.askor.blagosfera.data.jpa.repositories.batchvotingtemplate;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.votingtemplate.BatchVotingTemplateEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 13.10.2015.
 */
public interface BatchVotingTemplateRepository extends JpaRepository<BatchVotingTemplateEntity, Long> {

    List<BatchVotingTemplateEntity> findByCommunity_IdAndSubjectLikeIgnoreCaseOrderByIdDesc(Long communityId, String subject, Pageable pageable);

    @Query("select count(templ) from BatchVotingTemplateEntity templ where templ.community.id = :communityId and lower(templ.subject) like lower(:subject)")
    int countByCommunityIdAndSubjectLikeIgnoreCase(@Param("communityId") Long communityId, @Param("subject") String subject);

    @Query("select templ from BatchVotingTemplateEntity templ where templ.batchVotings in (:batchVotingIds)")
    List<BatchVotingTemplateEntity> findByBatchVotings(@Param("batchVotingIds") List<Long> batchVotingIds);

    @Query("select templ from BatchVotingTemplateEntity templ where :batchVotingId member of templ.batchVotings")
    BatchVotingTemplateEntity findByBatchVoting(@Param("batchVotingId") Long batchVotingId);

    //List<BatchVotingTemplateEntity>
}
