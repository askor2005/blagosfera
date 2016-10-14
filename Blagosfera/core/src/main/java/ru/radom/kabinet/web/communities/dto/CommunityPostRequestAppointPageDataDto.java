package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityPostRequest;

/**
 * Обёртка данных для страницы принятия должности
 * Created by vgusev on 05.04.2016.
 */
@Data
public class CommunityPostRequestAppointPageDataDto {

    private String errorMessage;

    private Long requestId;

    private String communityLink;

    private String communityName;

    private String postName;

    private String userName;

    private boolean isUserSex;

    private String senderUserName;

    public CommunityPostRequestAppointPageDataDto(CommunityPostRequest communityPostRequest, Community community) {
        setRequestId(communityPostRequest.getId());
        if (community != null) {
            setCommunityLink(community.getLink());
            setCommunityName(community.getName());
        }
        setPostName(communityPostRequest.getCommunityPost().getName());

        if (communityPostRequest.getReceiver() != null && communityPostRequest.getReceiver().getUser() != null) {
            setUserName(communityPostRequest.getReceiver().getUser().getOfficialName());
            setUserSex(communityPostRequest.getReceiver().getUser().isSex());
        }
        if (communityPostRequest.getSender() != null && communityPostRequest.getSender().getUser() != null) {
            setSenderUserName(communityPostRequest.getSender().getUser().getName());
        }
    }

    public CommunityPostRequestAppointPageDataDto(String errorMessage) {
        setErrorMessage(errorMessage);
    }


}
