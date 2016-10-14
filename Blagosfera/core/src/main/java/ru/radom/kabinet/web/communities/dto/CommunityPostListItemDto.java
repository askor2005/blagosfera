package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.radom.kabinet.dto.community.CommunityUserPost;

/**
 * Обёртка для таблицы на странице назначения на должности
 * Created by vgusev on 05.04.2016.
 */
@Data
public class CommunityPostListItemDto {

    private int id;

    private Long postId;

    private Long userId;

    private String userName;

    private String communityName;

    private String name;

    public CommunityPostListItemDto(CommunityUserPost communityUserPost, int index) {
        setId(index);
        setPostId(communityUserPost.getId());
        setName(communityUserPost.getPostName());
        setUserId(communityUserPost.getUserId());
        setUserName(communityUserPost.getUserName());
        setCommunityName(communityUserPost.getCommunityName());
    }

}
