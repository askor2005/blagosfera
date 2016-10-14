package ru.radom.kabinet.web.communities.dto;

import ru.askor.blagosfera.domain.community.CommunityDocumentRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by vgusev on 22.07.2016.
 */
public class CommunityDocumentRequestGridDto {

    public boolean success = true;

    public long total = 0;

    public List<CommunityDocumentRequestGridItemDto> items = new ArrayList<>();

    private CommunityDocumentRequestGridDto() {
    }

    public static CommunityDocumentRequestGridDto toSuccessDto(List<CommunityDocumentRequest> communityDocumentRequests, long total) {
        CommunityDocumentRequestGridDto result = new CommunityDocumentRequestGridDto();
        List<CommunityDocumentRequestGridItemDto> items = CommunityDocumentRequestGridItemDto.toDtoList(communityDocumentRequests);
        if (items != null) {
            result.items.addAll(items);
        }
        result.total = total;
        result.success = true;
        return result;
    }

    public static CommunityDocumentRequestGridDto toFailDto() {
        CommunityDocumentRequestGridDto result = new CommunityDocumentRequestGridDto();
        result.success = false;
        result.total = 0;
        return result;
    }
    
}
