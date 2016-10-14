package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;

/**
 * Created by vtarasenko on 01.08.2016.
 */
@Data
public class PossibleCommunityMemberDto{
    private CommunityUserMemberDataDto communityMember;
    private Integer countVerified;

}
