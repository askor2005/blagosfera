package ru.radom.kabinet.model.ecoadvisor;

import ru.askor.blagosfera.domain.ecoadvisor.AdvisorBonusAllocation;
import ru.askor.blagosfera.domain.ecoadvisor.AdvisorBonusReceiverType;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "eco_advisor_bonus_allocation")
public class AdvisorBonusAllocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eco_advisor_bonus_allocation_id_generator")
    @SequenceGenerator(name = "eco_advisor_bonus_allocation_id_generator", sequenceName = "eco_advisor_bonus_allocation_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "eco_advisor_parameters_id", nullable = false)
    private AdvisorParametersEntity parameters;

    @Column(name = "allocation_percent", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal allocationPercent;

    @Column(name="target_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdvisorBonusReceiverType receiverType;

    public AdvisorBonusAllocationEntity() {
    }

    public AdvisorBonusAllocation toDomain() {
        AdvisorBonusAllocation bonusAllocation = new AdvisorBonusAllocation();
        bonusAllocation.setId(getId());
        bonusAllocation.setAllocationPercent(getAllocationPercent());
        bonusAllocation.setReceiverType(getReceiverType());
        return bonusAllocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdvisorBonusAllocationEntity)) return false;

        AdvisorBonusAllocationEntity that = (AdvisorBonusAllocationEntity) o;

        //return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AdvisorParametersEntity getParameters() {
        return parameters;
    }

    public void setParameters(AdvisorParametersEntity parameters) {
        this.parameters = parameters;
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
