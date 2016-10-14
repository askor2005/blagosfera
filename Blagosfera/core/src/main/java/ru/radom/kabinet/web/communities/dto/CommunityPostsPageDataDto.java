package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunityPermission;
import ru.askor.blagosfera.domain.community.CommunityPost;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 30.03.2016.
 */
@Data
public class CommunityPostsPageDataDto {

    private List<CommunityPostDto> posts;

    private List<CommunityPermissionDto> permissions;

    public CommunityPostsPageDataDto(List<CommunityPost> communityPosts, List<CommunityPermission> communityPermissions) {
        if (communityPosts != null) {
            this.posts = communityPosts.stream().map(CommunityPostDto::new).collect(Collectors.toList());
        }
        if (communityPermissions != null) {
            this.permissions = communityPermissions.stream().map(CommunityPermissionDto::new).collect(Collectors.toList());
        }
    }
}
