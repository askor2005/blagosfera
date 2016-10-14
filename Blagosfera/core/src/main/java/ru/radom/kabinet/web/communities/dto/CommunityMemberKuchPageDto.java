package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityPost;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by dream_000 on 13.05.2016.
 */
@Data
public class CommunityMemberKuchPageDto extends CommunityMemberDto {

    private List<String> postNames;

    public CommunityMemberKuchPageDto(CommunityMember communityMember) {
        super(communityMember);
        if (communityMember.getPosts() != null) {
            postNames = new ArrayList<>();
            for (CommunityPost communityPost : communityMember.getPosts()) {
                postNames.add(communityPost.getName());
            }
        }
    }

}
