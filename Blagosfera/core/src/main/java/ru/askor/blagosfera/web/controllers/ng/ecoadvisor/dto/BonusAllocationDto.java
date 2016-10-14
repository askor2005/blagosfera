package ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto;

import ru.askor.blagosfera.domain.ecoadvisor.AdvisorBonusAllocation;
import ru.askor.blagosfera.domain.ecoadvisor.AdvisorBonusReceiverType;

import java.math.BigDecimal;

public class BonusAllocationDto {

    public Long id;
    public BigDecimal allocationPercent;
    public AdvisorBonusReceiverType receiverType;

    public BonusAllocationDto() {
    }

    public BonusAllocationDto(AdvisorBonusAllocation bonusAllocation) {
        id = bonusAllocation.getId();
        allocationPercent = bonusAllocation.getAllocationPercent();
        receiverType = bonusAllocation.getReceiverType();
    }
}
