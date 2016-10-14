package ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto;

import ru.radom.kabinet.model.ecoadvisor.AdvisorProductGroupEntity;

public class ProductGroupDto {

    public Long id;
    public String name;

    public ProductGroupDto() {
    }

    public ProductGroupDto(AdvisorProductGroupEntity productGroup) {
        id = productGroup.getId();
        name = productGroup.getName();
    }
}
