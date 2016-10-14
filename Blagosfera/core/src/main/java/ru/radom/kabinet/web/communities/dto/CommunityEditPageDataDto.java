package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.kabinet.security.SecurityUtils;

/**
 *
 * Created by vgusev on 24.03.2016.
 */
@Data
public class CommunityEditPageDataDto {

    private CommunityFullDataDto community;

    //private Map<String, Boolean> permissions;

    public CommunityEditPageDataDto(Community community, CommunityMember selfMember) {
        setCommunity(new CommunityFullDataDto(community, selfMember));
    }
}
