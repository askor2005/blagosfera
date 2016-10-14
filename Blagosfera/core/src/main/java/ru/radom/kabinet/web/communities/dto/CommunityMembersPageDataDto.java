package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;

/**
 *
 * Created by vgusev on 31.03.2016.
 */
@Data
public class CommunityMembersPageDataDto {

    private int communityMembersCount;

    private int organizationMembersCount;

    private CommunityAnyPageDto community;

    boolean hasRightInvites;

    boolean hasRightRequests;

    boolean hasRightExclude;

    boolean isCreator;

    public CommunityMembersPageDataDto(Community community, CommunityMember selfMember, int communityMembersCount,
                                       int organizationMembersCount,
                                       boolean hasRightInvites,
                                       boolean hasRightRequests,
                                       boolean hasRightExclude,
                                       boolean isCreator) {
        setCommunityMembersCount(communityMembersCount);
        setOrganizationMembersCount(organizationMembersCount);
        setCommunity(CommunityAnyPageDto.toDto(community, selfMember));
        setHasRightInvites(hasRightInvites);
        setHasRightRequests(hasRightRequests);
        setHasRightExclude(hasRightExclude);
        setCreator(isCreator);
    }
}
