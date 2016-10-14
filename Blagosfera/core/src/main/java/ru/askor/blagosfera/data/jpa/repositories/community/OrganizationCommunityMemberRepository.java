package ru.askor.blagosfera.data.jpa.repositories.community;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.radom.kabinet.model.communities.OrganizationCommunityMemberEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 19.10.2015.
 */
public interface OrganizationCommunityMemberRepository extends JpaRepository<OrganizationCommunityMemberEntity, Long> {

    // Найти участников по ИД объединения, по имени и по статусу
    List<OrganizationCommunityMemberEntity> findByCommunity_IdAndStatusInAndOrganization_NameLikeIgnoreCase(Long communityId, List<CommunityMemberStatus> statuses, String communityName, Pageable pageable);

    // Найти участника в объединении
    OrganizationCommunityMemberEntity findByCommunity_IdAndOrganization_Id(Long communityId, Long organizationId);

    // Найти участников в объединении по статусу
    List<OrganizationCommunityMemberEntity> findByCommunity_IdAndStatus(Long communityId, CommunityMemberStatus status);

    // Найти оргагинации в объединении
    List<OrganizationCommunityMemberEntity> findByCommunity_IdAndOrganization_IdIn(Long communityId, List<Long> organizationIds);

    @Query("select count(m) from OrganizationCommunityMemberEntity m where m.community.id = :communityId and m.status in (:memberStatuses)")
    int getCountMembers(@Param("communityId") Long communityId, @Param("memberStatuses") List<CommunityMemberStatus> memberStatuses);
}
