package ru.askor.blagosfera.data.jpa.repositories.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;

import java.util.List;

/**
 *
 * Created by Maxim Nikitin on 01.03.2016.
 */
public interface CommunityMemberRepository extends JpaRepository<CommunityMemberEntity, Long>, JpaSpecificationExecutor<CommunityMemberEntity> {

    /**
     *
     * @param communityId
     * @param userId
     * @return
     */
    CommunityMemberEntity findByCommunity_IdAndUser_Id(Long communityId, Long userId);

    /**
     *
     * @param communityId
     * @param userId
     * @param status
     * @return
     */
    CommunityMemberEntity findByCommunity_IdAndUser_IdAndStatus(Long communityId, Long userId, CommunityMemberStatus status);

    /**
     *
     * @param communityIds
     * @param userId
     * @return
     */
    List<CommunityMemberEntity> findByCommunity_IdInAndUser_Id(List<Long> communityIds, Long userId);

    @Query("select count(m) from community_members m where m.community.id = :communityId and m.status in (:memberStatuses)")
    int getCountMembers(@Param("communityId") Long communityId, @Param("memberStatuses") List<CommunityMemberStatus> memberStatuses);

    /**
     *
     * @param memberIds
     * @return
     */
    List<CommunityMemberEntity> findByIdIn(List<Long> memberIds);

    /**
     *
     * @param communityId
     * @param status
     * @return
     */
    List<CommunityMemberEntity> findByCommunity_IdAndStatus(Long communityId, CommunityMemberStatus status);

    @Query("select c from community_members c where c.id = :id")
    CommunityMemberEntity getById(@Param("id") Long id);


}
