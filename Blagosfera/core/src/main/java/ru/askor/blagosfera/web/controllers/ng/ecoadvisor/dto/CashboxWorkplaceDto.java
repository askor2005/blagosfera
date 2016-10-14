package ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto;

import ru.askor.blagosfera.domain.community.CommunityInventoryUnit;

public class CashboxWorkplaceDto {

    public Long id;
    public String number;
    public String guid;
    public String description;
    public String photo;

    public CashboxWorkplaceDto() {
    }

    public CashboxWorkplaceDto(CommunityInventoryUnit inventoryUnit) {
        id = inventoryUnit.getId();
        number = inventoryUnit.getNumber();
        guid = inventoryUnit.getGuid();
        description = inventoryUnit.getDescription();
        photo = inventoryUnit.getPhoto();
    }
}
