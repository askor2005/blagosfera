package ru.radom.kabinet.services.communities;

import ru.askor.blagosfera.domain.community.CommunityInventoryUnit;

/**
 *
 * Created by vgusev on 31.03.2016.
 */
public interface CommunityInventoryService {

    CommunityInventoryUnit saveUnit(CommunityInventoryUnit unit, Long communityId);

    CommunityInventoryUnit deleteUnit(Long unitId, Long communityId);

    //CommunityInventoryUnitEntity getUnit(CommunityEntity community, CommunityInventoryUnitEntity unit);
}
