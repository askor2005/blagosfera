package ru.radom.kabinet.model.ecoadvisor;

import ru.askor.blagosfera.domain.ecoadvisor.AdvisorSystemParameters;

import javax.persistence.*;

@Entity
@Table(name = "eco_advisor_system_parameters")
public class AdvisorSystemParametersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eco_advisor_system_parameters_id_generator")
    @SequenceGenerator(name = "eco_advisor_system_parameters_id_generator", sequenceName = "eco_advisor_system_parameters_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "system_bonus_account_id", nullable = false)
    private Long systemBonusAccountId;

    public AdvisorSystemParametersEntity() {
    }

    public AdvisorSystemParameters toDomain() {
        AdvisorSystemParameters advisorSystemParameters = new AdvisorSystemParameters();
        advisorSystemParameters.setId(getId());
        advisorSystemParameters.setSystemBonusAccountId(getSystemBonusAccountId());
        return advisorSystemParameters;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSystemBonusAccountId() {
        return systemBonusAccountId;
    }

    public void setSystemBonusAccountId(Long systemBonusAccountId) {
        this.systemBonusAccountId = systemBonusAccountId;
    }
}
