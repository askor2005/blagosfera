package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunityInventoryUnit;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 01.04.2016.
 */
@Data
public class CommunityInventoryUnitDto {

    private Long id;

    private String guid;

    private String number;

    private String photo;

    private String description;

    private CommunityInventoryUnitTypeDto type;

    private CommunityMemberDto responsible;

    private Long ownerCommunityId;

    private String ownerCommunityName;

    private Long leasedToCommunityId;

    private String leasedToCommunityName;

    public CommunityInventoryUnitDto(CommunityInventoryUnit communityInventoryUnit) {
        setId(communityInventoryUnit.getId());
        setGuid(communityInventoryUnit.getGuid());
        setNumber(communityInventoryUnit.getNumber());
        setPhoto(communityInventoryUnit.getPhoto());
        setDescription(communityInventoryUnit.getDescription());
        setType(new CommunityInventoryUnitTypeDto(communityInventoryUnit.getType()));
        setResponsible(new CommunityMemberDto(communityInventoryUnit.getResponsible()));
        if (communityInventoryUnit.getCommunity() != null) {
            setOwnerCommunityId(communityInventoryUnit.getCommunity().getId());
            setOwnerCommunityName(communityInventoryUnit.getCommunity().getName());
        }
        if (communityInventoryUnit.getLeasedTo() != null) {
            setLeasedToCommunityId(communityInventoryUnit.getLeasedTo().getId());
            setLeasedToCommunityName(communityInventoryUnit.getLeasedTo().getName());
        }
    }

    public static List<CommunityInventoryUnitDto> toDtoList(List<CommunityInventoryUnit> communityInventoryUnits) {
        List<CommunityInventoryUnitDto> result = null;
        if (communityInventoryUnits != null) {
            result = new ArrayList<>();
            for (CommunityInventoryUnit communityInventoryUnit : communityInventoryUnits) {
                result.add(new CommunityInventoryUnitDto(communityInventoryUnit));
            }
        }
        return result;
    }

}
