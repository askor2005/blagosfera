package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.kabinet.security.SecurityUtils;

/**
 *
 * Created by vgusev on 10.03.2016.
 */
@Data
public class CommunityInfoPageDataDto {

    private CommunityFullDataDto community;

    //private Map<String, Boolean> permissions;

    public CommunityInfoPageDataDto(Community community, CommunityMember selfMember) {
        setCommunity(new CommunityFullDataDto(community, selfMember));
    }

}
