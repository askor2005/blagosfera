package ru.radom.kabinet.services.communities.organizationmember.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.OrganizationCommunityMember;
import ru.askor.blagosfera.domain.document.Document;
import ru.radom.kabinet.web.communities.dto.CommunityDocumentDto;

/**
 *
 * Created by vgusev on 04.11.2015.
 */
@Data
public class OrganizationCommunityMemberDto {

    private CommunityDocumentDto document;

    private Long id;

    private Long communityId;

    private String communityName;

    private String communityLink;

    private Long organizationId;

    private String organizationName;

    private String organizationLink;

    public OrganizationCommunityMemberDto(Document document, Long id, Long communityId, String communityName, String communityLink, Long organizationId, String organizationName, String organizationLink) {
        this.document = new CommunityDocumentDto(document);
        this.id = id;
        this.communityId = communityId;
        this.communityName = communityName;
        this.communityLink = communityLink;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.organizationLink = organizationLink;
    }

    public static OrganizationCommunityMemberDto toDto(OrganizationCommunityMember member) {
        return new OrganizationCommunityMemberDto(member.getDocument(),
                member.getId(),
                member.getCommunity().getId(), member.getCommunity().getName(), member.getCommunity().getLink(),
                member.getOrganization().getId(), member.getOrganization().getName(), member.getOrganization().getLink());
    }
}
