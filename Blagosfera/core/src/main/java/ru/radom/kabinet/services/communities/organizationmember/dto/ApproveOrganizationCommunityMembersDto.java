package ru.radom.kabinet.services.communities.organizationmember.dto;

import ru.askor.blagosfera.domain.community.OrganizationCommunityMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Обёртка участников на встулпние в объединение
 * Created by vgusev on 29.10.2015.
 */
public class ApproveOrganizationCommunityMembersDto {

    private List<OrganizationCommunityMemberDto> members;

    public ApproveOrganizationCommunityMembersDto(List<OrganizationCommunityMember> members) {
        this.members = new ArrayList<>();
        for (OrganizationCommunityMember organizationCommunityMember : members) {
            this.members.add(OrganizationCommunityMemberDto.toDto(organizationCommunityMember));
        }
    }

    public List<OrganizationCommunityMemberDto> getMembers() {
        return members;
    }
}
