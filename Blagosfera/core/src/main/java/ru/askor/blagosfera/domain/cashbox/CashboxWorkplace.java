package ru.askor.blagosfera.domain.cashbox;

import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.inventory.CommunityInventoryUnitEntity;

public class CashboxWorkplace {

    private CommunityInventoryUnitEntity workplace;

    public CashboxWorkplace(CommunityInventoryUnitEntity workplace) {
        this.workplace = workplace;
    }

    public CommunityInventoryUnitEntity getWorkplace() {
        return workplace;
    }

    public CommunityEntity getShop() {
        return workplace.getCommunity();
    }

    public CommunityEntity getCooperativeDepartment() {
        return workplace.getLeasedTo();
    }

    public CommunityEntity getCooperative() {
        return workplace.getLeasedTo().getParent();
    }

    public String getWorkplaceId() {
        return workplace.getGuid();
    }
}
