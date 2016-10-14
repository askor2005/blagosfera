package ru.radom.kabinet.services.communities.organizationmember;

import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.community.OrganizationCommunityMember;

import java.util.List;

/**
 *
 * Created by vgusev on 17.03.2016.
 */
public interface OrganizationMemberDomainService {

    OrganizationCommunityMember getById(Long id);

    OrganizationCommunityMember save(OrganizationCommunityMember member);

    /**
     * Загрузить участника объединения - оргранизацию
     * @param communityId
     * @param organizationId
     * @return
     */
    OrganizationCommunityMember getByCommunityIdAndOrganizationId(Long communityId, Long organizationId);

    /**
     * Загрузить участников объединения - оргранизацию
     * @param communityId
     * @param organizationIds
     * @return
     */
    List<OrganizationCommunityMember> getByCommunityIdAndOrganizationIds(Long communityId, List<Long> organizationIds);

    List<OrganizationCommunityMember> getByCommunityIdAndStatus(Long communityId, CommunityMemberStatus status);

    void delete(Long id);

    /**
     *
     * @param communityId
     * @param status
     * @param communityName
     * @param page
     * @param perPage
     * @return
     */
    List<OrganizationCommunityMember> find(Long communityId, CommunityMemberStatus status, String communityName, int page, int perPage);

    /**
     * Поиск участников объединения - юр лиц
     * @param communityId
     * @param statuses
     * @param communityName
     * @param page
     * @param perPage
     * @return
     */
    List<OrganizationCommunityMember> find(Long communityId, List<CommunityMemberStatus> statuses, String communityName, int page, int perPage);

    /**
     * Количество участников объединения
     * @param communityId
     * @return
     */
    int getMembersCount(Long communityId, List<CommunityMemberStatus> memberStatuses);
}
