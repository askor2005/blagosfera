package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;

import java.util.List;

/**
 *
 * Created by vgusev on 04.04.2016.
 */
@Data
public class CommunityPostRequestPageDataDto {

    private CommunityAnyPageDto community;

    private List<CommunityChildPostRequestDto> children;

    private List<PossiblePostCandidateDto> candidates;

    public CommunityPostRequestPageDataDto(
            Community community, CommunityMember selfMember,
            List<Community> children, List<CommunityMember> possibleCandidates) {
        setCommunity(CommunityAnyPageDto.toDto(community, selfMember));
        setChildren(CommunityChildPostRequestDto.toDtoList(children));
        setCandidates(PossiblePostCandidateDto.toDtoList(possibleCandidates));
    }
}
