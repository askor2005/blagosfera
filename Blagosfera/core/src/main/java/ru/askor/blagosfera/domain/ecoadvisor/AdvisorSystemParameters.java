package ru.askor.blagosfera.domain.ecoadvisor;

public class AdvisorSystemParameters {

    private Long id;
    private Long systemBonusAccountId;

    public AdvisorSystemParameters() {
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
