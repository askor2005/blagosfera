package ru.radom.kabinet.web.communities.dto;

import lombok.Data;

/**
 *
 * Created by vgusev on 10.03.2016.
 */
@Data
public class CommunityAnyPageDataDto {

    private CommunityAnyPageDto community;

    public static CommunityAnyPageDataDto toDto(CommunityAnyPageDto community) {
        CommunityAnyPageDataDto result = new CommunityAnyPageDataDto();
        result.setCommunity(community);
        return result;
    }

}
