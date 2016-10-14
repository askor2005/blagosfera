package ru.radom.kabinet.web.communities.dto;

import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.community.OrganizationCommunityMember;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 19.10.2015.
 */
public class PossibleOrganizationsMembersGridDto {

    private static class PossibleOrganizationItemDto {
        private Long id;
        private String name;
        private CommunityMemberStatus status;
        private Long memberId;

        public PossibleOrganizationItemDto(Long id, String name, CommunityMemberStatus status, Long memberId) {
            this.id = id;
            this.name = name;
            this.status = status;
            this.memberId = memberId;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public CommunityMemberStatus getStatus() {
            return status;
        }

        public Long getMemberId() {
            return memberId;
        }
    }

    private boolean success = true;

    private int total = 0;

    private List<PossibleOrganizationItemDto> items = new ArrayList<>();

    public PossibleOrganizationsMembersGridDto(boolean success, int total, List<PossibleOrganizationItemDto> items) {
        this.success = success;
        this.total = total;
        this.items = items;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getTotal() {
        return total;
    }

    public List<PossibleOrganizationItemDto> getItems() {
        return items;
    }

    public static PossibleOrganizationsMembersGridDto successDtoFromDomain(int count, List<Community> organizations, List<OrganizationCommunityMember> members) {
        List<PossibleOrganizationItemDto> items = new ArrayList<>();
        for (Community community : organizations) {
            OrganizationCommunityMember findMember = null;
            if (members != null) {
                for (OrganizationCommunityMember member : members) {
                    if (member != null && member.getOrganization().getId().equals(community.getId())) {
                        findMember = member;
                        break;
                    }
                }
            }
            Long memberId = null;
            CommunityMemberStatus status = null;
            if (findMember != null) {
                memberId = findMember.getId();
                status = findMember.getStatus();
            }

            items.add(new PossibleOrganizationItemDto(community.getId(), community.getName(), status, memberId));
        }
        return new PossibleOrganizationsMembersGridDto(true, count, items);
    }

    public static PossibleOrganizationsMembersGridDto failDto() {
        return new PossibleOrganizationsMembersGridDto(false, 0, null);
    }
}
