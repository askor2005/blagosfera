package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityInventoryUnitType;
import ru.askor.blagosfera.domain.community.CommunityMember;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 31.03.2016.
 */
@Data
public class CommunityInventoryPageDataDto {

    private String defaultPhoto;

    private List<CommunityInventoryUnitTypeDto> types;

    private CommunityAnyPageDto community;

    public CommunityInventoryPageDataDto(
            String defaultPhoto, List<CommunityInventoryUnitType> communityInventoryUnitTypes,
            Community community,
            CommunityMember selfMember) {
        setDefaultPhoto(defaultPhoto);
        if (communityInventoryUnitTypes != null) {
            setTypes(communityInventoryUnitTypes.stream().map(CommunityInventoryUnitTypeDto::new).collect(Collectors.toList()));
        }
        setCommunity(CommunityAnyPageDto.toDto(community, selfMember));
    }
}
