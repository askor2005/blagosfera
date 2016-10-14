package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.community.OrganizationCommunityMember;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 04.04.2016.
 */
@Data
public class CommunityOrganizationMemberDataDto {

    private Long id;

    private String name;

    private String link;

    private String avatar;

    private Long memberId;

    private CommunityMemberStatus status;

    public CommunityOrganizationMemberDataDto(OrganizationCommunityMember organizationCommunityMember) {
        setId(organizationCommunityMember.getOrganization().getId());
        setName(organizationCommunityMember.getOrganization().getName());
        setLink(organizationCommunityMember.getOrganization().getLink());
        setAvatar(organizationCommunityMember.getOrganization().getAvatar());
        setMemberId(organizationCommunityMember.getId());
        setStatus(organizationCommunityMember.getStatus());
    }

    public static List<CommunityOrganizationMemberDataDto> toDtoList(List<OrganizationCommunityMember> members) {
        List<CommunityOrganizationMemberDataDto> result = null;
        if (members != null) {
            result = new ArrayList<>();
            for (OrganizationCommunityMember member : members) {
                result.add(new CommunityOrganizationMemberDataDto(member));
            }
        }
        return result;
    }
}
