package ru.radom.kabinet.web.communities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunityMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Обёртка для возможных кандатов на должность в объединении
 * Created by vgusev on 05.04.2016.
 */
@Data
@AllArgsConstructor
public class PossiblePostCandidateDto {

    private Long id;

    private String name;

    public static List<PossiblePostCandidateDto> toDtoList(List<CommunityMember> communityMembers) {
        List<PossiblePostCandidateDto> result = null;
        if (communityMembers != null && !communityMembers.isEmpty()) {
            result = new ArrayList<>();
            for (CommunityMember communityMember : communityMembers) {
                result.add(new PossiblePostCandidateDto(communityMember.getUser().getId(), communityMember.getUser().getName()));
            }
        }
        return result;
    }
}
