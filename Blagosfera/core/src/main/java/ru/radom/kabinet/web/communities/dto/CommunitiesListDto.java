package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 09.03.2016.
 */
@Data
public class CommunitiesListDto {

    private List<CommunityListItemDto> list;

    private long count;

    public static CommunitiesListDto toDto(List<Community> communities, long count, List<CommunityMember> members,
                                           Map<Long, Boolean> canDeleteCommunitiesMap, Map<Long, Boolean> canRestoreCommunitiesMap) {
        Map<Long, CommunityMember> memberInCommunities = new HashMap<>();
        if (members != null) {
            for (CommunityMember communityMember : members) {
                memberInCommunities.put(communityMember.getCommunity().getId(), communityMember);
            }
        }

        List<CommunityListItemDto> list = new ArrayList<>();
        if (communities != null) {
            for (Community community : communities) {
                CommunityMember communityMember = memberInCommunities.get(community.getId());
                boolean isCanDelete = canDeleteCommunitiesMap.get(community.getId());
                boolean isCanRestore = canRestoreCommunitiesMap.get(community.getId());
                list.add(new CommunityListItemDto(community, communityMember, isCanDelete, isCanRestore));
            }
        }
        CommunitiesListDto result = new CommunitiesListDto();
        result.setList(list);
        result.setCount(count);
        return result;
    }
}
