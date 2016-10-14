package ru.askor.blagosfera.data.jpa.repositories.community;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.communities.CommunityEntity;

import java.util.Date;
import java.util.List;

public interface CommunityRepository extends JpaRepository<CommunityEntity, Long>, JpaSpecificationExecutor<CommunityEntity> {

    List<CommunityEntity> findByCreator_Id(Long creatorId);

    List<CommunityEntity> findByCreator_IdAndParentIsNull(Long creatorId);

    List<CommunityEntity> findByCreator_IdAndParentIsNotNullAndParent_Creator_IdNot(Long creatorId, Long parentCreatorId);

    /**
     * Количество участников объединения
     * @param communityId
     * @return
     */
    @Query("select count(m.id) from community_members m where m.community.id = :communityId and m.status = 2 and (m.user.id not in (select u.id from UserEntity as u where u.deleted = true))")
    int getMembersCount(@Param("communityId") Long communityId);

    /**
     * Количество подгрупп
     * @param communityId
     * @return
     */
    @Query("select count(c.id) from communities c where c.parent.id = :communityId")
    int getSubgroupsCount(@Param("communityId") Long communityId);

    /**
     * Список объединений по ИД родительского объединения
     * @param communityParentId ИД родительского объединения
     * @param pageable
     * @return
     */
    List<CommunityEntity> findByParent_Id(Long communityParentId, Pageable pageable);

    /**
     * Список объединений по ИД родительского объединения
     * @param communityParentId ИД родительского объединения
     * @return
     */
    List<CommunityEntity> findByParent_Id(Long communityParentId);

    @Query(value = "select a.id from accounts a where a.owner_id = :communityId and a.owner_type='COMMUNITY'",nativeQuery = true)
    Long[] getAccountIds(@Param("communityId") Long communityId);

    @Query(value = "select  c.id from communities c inner join community_members m on (m.sharer_id = :userId and m.community_id = c.id) left join community_visit_logs log on (log.user_id = :userId and log.community_id = c.id and log.visit_time >= :minVisitTime) where (m.status =2 or m.status = 6 ) group by c.id order by count(log.id) desc,c.name asc offset :offset limit :limit",nativeQuery = true)
    Long[] getTopVisitUserCommunities(@Param("userId") Long userId,@Param("offset") Long offset,@Param("limit") Long limit,@Param("minVisitTime")Date minVisitTime);
    @Query(value = "select  c.id from communities c inner join community_members m on (m.sharer_id = :userId and m.community_id = c.id) left join community_visit_logs log on (log.user_id = :userId and log.community_id = c.id and log.visit_time >= :minVisitTime) where ((m.status =2 or m.status = 6) and upper(c.name) like :groupName) group by c.id order by count(log.id) desc,c.name asc offset :offset limit :limit",nativeQuery = true)
    Long[] getTopVisitUserCommunitiesByName(@Param("userId") Long userId,@Param("offset") Long offset,@Param("limit") Long limit,@Param("minVisitTime")Date minVisitTime,@Param("groupName") String name);

}
