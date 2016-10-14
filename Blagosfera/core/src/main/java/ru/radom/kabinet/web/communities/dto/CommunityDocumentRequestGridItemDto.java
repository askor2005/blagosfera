package ru.radom.kabinet.web.communities.dto;

import ru.askor.blagosfera.domain.community.CommunityDocumentRequest;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 22.07.2016.
 */
public class CommunityDocumentRequestGridItemDto {

    public Long id;

    public String communityName;

    public CommunityDocumentRequestGridItemDto(CommunityDocumentRequest communityDocumentRequest) {
        id = communityDocumentRequest.getId();
        communityName = communityDocumentRequest.getCommunity().getFullRuName();
    }

    public static List<CommunityDocumentRequestGridItemDto> toDtoList(List<CommunityDocumentRequest> communityDocumentRequests) {
        List<CommunityDocumentRequestGridItemDto> result = null;
        if (communityDocumentRequests != null) {
            result = new ArrayList<>();
            for (CommunityDocumentRequest communityDocumentRequest : communityDocumentRequests) {
                result.add(new CommunityDocumentRequestGridItemDto(communityDocumentRequest));
            }
        }
        return result;
    }
}
