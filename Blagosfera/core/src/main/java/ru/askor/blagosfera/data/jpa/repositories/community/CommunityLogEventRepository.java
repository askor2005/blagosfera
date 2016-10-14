package ru.askor.blagosfera.data.jpa.repositories.community;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.radom.kabinet.model.communities.CommunityLogEventEntity;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 04.04.2016.
 */
public interface CommunityLogEventRepository extends JpaRepository<CommunityLogEventEntity, Long>, JpaSpecificationExecutor<CommunityLogEventEntity> {

    /*@Query("select cle from CommunityLogEventEntity cle " +
            "where cle.community.id = :communityId and cle.userEntity.id = :userId and cle.type = :type and cle.date between :fromDate and :toDate " +
            "order by cle.date desc ")
                                                                                               List<CommunityLogEventEntity> find(
                    @Param("communityId") Long communityId, @Param("userId") Long userId, @Param("type") CommunityEventType type,
                    @Param("fromDate") Date fromDate, @Param("toDate") Date toDate, Pageable pageable);*/

    List<CommunityLogEventEntity> findByCommunity_IdAndUserEntity_IdAndDateBetweenAndTypeOrderByDateDesc(
            Long communityId, Long userId, Date fromDate, Date toDate, CommunityEventType type, Pageable pageable
    );

    List<CommunityLogEventEntity> findByCommunity_IdAndDateBetweenAndTypeOrderByDateDesc(
            Long communityId, Date fromDate, Date toDate, CommunityEventType type, Pageable pageable
    );

    List<CommunityLogEventEntity> findByCommunity_IdAndDateBetweenOrderByDateDesc(
            Long communityId, Date fromDate, Date toDate, Pageable pageable
    );

    List<CommunityLogEventEntity> findByCommunity_Id(Long communityId);
}
