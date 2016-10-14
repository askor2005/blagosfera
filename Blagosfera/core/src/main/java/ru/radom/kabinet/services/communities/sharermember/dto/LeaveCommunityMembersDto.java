package ru.radom.kabinet.services.communities.sharermember.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.document.Document;
import ru.radom.kabinet.web.communities.dto.CommunityDocumentDto;
import ru.radom.kabinet.web.communities.dto.CommunityMemberDto;

import java.util.List;
import java.util.Map;

/**
 * Обёртка для данных на странице вывода пайщиков из ПО и КУч
 * Created by vgusev on 29.10.2015.
 */
@Data
public class LeaveCommunityMembersDto {

    private List<CommunityMemberDto> members;

    private List<CommunityDocumentDto> createdProtocols; // Уже созданные, но не подписанные протоколы

    private Map<Long, String> documentLinks;

    public LeaveCommunityMembersDto(List<CommunityMember> members, Map<Long, String> documentLinks, List<Document> createdProtocols) {
        setMembers(CommunityMemberDto.toDtoList(members));
        setDocumentLinks(documentLinks);
        setCreatedProtocols(CommunityDocumentDto.toListDto(createdProtocols));
    }
}
