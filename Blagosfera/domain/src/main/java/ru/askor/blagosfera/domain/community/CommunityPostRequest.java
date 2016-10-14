package ru.askor.blagosfera.domain.community;

import lombok.Data;

/**
 *
 * Created by vgusev on 21.03.2016.
 */
@Data
public class CommunityPostRequest {

    private Long id;

    private CommunityMember sender;

    private CommunityMember receiver;

    private Community community;

    private CommunityPost communityPost;

    private CommunityPostRequestStatus status;
}
