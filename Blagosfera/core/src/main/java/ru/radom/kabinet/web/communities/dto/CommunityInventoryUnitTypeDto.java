package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunityInventoryUnitType;

/**
 *
 * Created by vgusev on 31.03.2016.
 */
@Data
public class CommunityInventoryUnitTypeDto {

    private Long id;

    private String name;

    private String internalName;

    public CommunityInventoryUnitTypeDto(CommunityInventoryUnitType communityInventoryUnitType) {
        setId(communityInventoryUnitType.getId());
        setName(communityInventoryUnitType.getName());
        setInternalName(communityInventoryUnitType.getInternalName());
    }
}
