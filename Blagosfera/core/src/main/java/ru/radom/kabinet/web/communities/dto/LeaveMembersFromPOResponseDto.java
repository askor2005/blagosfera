package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.radom.kabinet.services.communities.organizationmember.dto.OrganizationMembersHandleResult;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;

/**
 * Обёртка для данных ответа при выводе пайщиков из ПО (физ и юр лиц)
 * Created by vgusev on 06.04.2016.
 */
@Data
public class LeaveMembersFromPOResponseDto {

    private String documentName;

    private String documentLink;

    public LeaveMembersFromPOResponseDto(CommunityMemberResponseDto memberResponseDto) {
        if (memberResponseDto != null && memberResponseDto.getDocument() != null) {
            setDocumentName(memberResponseDto.getDocument().getName());
            setDocumentLink(memberResponseDto.getDocument().getLink());
        }
    }

    public LeaveMembersFromPOResponseDto(OrganizationMembersHandleResult organizationMembersHandleResult) {
        if (organizationMembersHandleResult != null && organizationMembersHandleResult.getDocument() != null) {
            setDocumentName(organizationMembersHandleResult.getDocument().getName());
            setDocumentLink(organizationMembersHandleResult.getDocument().getLink());
        }
    }
}
