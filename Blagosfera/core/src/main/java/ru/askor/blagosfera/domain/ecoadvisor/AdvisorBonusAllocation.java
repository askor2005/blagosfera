package ru.askor.blagosfera.domain.ecoadvisor;

import java.math.BigDecimal;

public class AdvisorBonusAllocation {

    private Long id;
    private BigDecimal allocationPercent;
    private AdvisorBonusReceiverType receiverType;

    public AdvisorBonusAllocation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAllocationPercent() {
        return allocationPercent;
    }

    public void setAllocationPercent(BigDecimal allocationPercent) {
        this.allocationPercent = allocationPercent;
    }

    public AdvisorBonusReceiverType getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(AdvisorBonusReceiverType receiverType) {
        this.receiverType = receiverType;
    }
}
