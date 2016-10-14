package ru.radom.kabinet.model.ecoadvisor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "eco_advisor_parameters")
public class AdvisorParametersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eco_advisor_parameters_id_generator")
    @SequenceGenerator(name = "eco_advisor_parameters_id_generator", sequenceName = "eco_advisor_parameters_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "community_id", nullable = false, unique = true)
    private Long communityId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parameters", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AdvisorProductEntity> products = new HashSet<>();

    // Общехозяйственные расходы (ОХР), %
    @Column(name = "general_running_costs", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal generalRunningCosts;

    // Зарплата вместе со ВСЕМИ зарплатными налогами составляет по отношению к ОХР, не более, %
    @Column(name = "wage", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal wage;

    // Ставка налога на добавленную стоимость, %
    @Column(name = "vat", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal vat;

    // Ставка налога на прибыль, %
    @Column(name = "tax_on_profits", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal taxOnProfits;

    // Ставка подоходного налога (НДФЛ), %
    @Column(name = "income_tax", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal incomeTax;

    // Доля Собственника в Уставном капитале, %
    @Column(name = "proprietorship_interest", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal proprietorshipInterest;

    // Ставка обложения выплачиваемых дивидендов для доли собственника физического лица, %
    @Column(name = "tax_on_dividends", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal taxOnDividends;

    // Доля чистой прибыли, остающаяся в расп. Компании
    @Column(name = "company_profit", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal companyProfit;

    // НАЦЕНКА
    @Column(name = "margin", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal margin;

    // оценка товара как паевого взноса, % от разницы между минимальной и максимальной оценкой
    @Column(name = "share_value", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal shareValue;

    // Доля средств, которую Собственник оставляет в КУч
    @Column(name = "department_part", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal departmentPart;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parameters", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AdvisorBonusAllocationEntity> bonusAllocations = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parameters", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AdvisorProductGroupEntity> productGroups = new HashSet<>();

    public AdvisorParametersEntity() {
    }

    public AdvisorParametersEntity(AdvisorParametersEntity source) {
        this.communityId = source.getCommunityId();
        this.generalRunningCosts = source.getGeneralRunningCosts();
        this.wage = source.getWage();
        this.vat = source.getVat();
        this.taxOnProfits = source.getTaxOnProfits();
        this.incomeTax = source.getIncomeTax();
        this.proprietorshipInterest = source.getProprietorshipInterest();
        this.taxOnDividends = source.getTaxOnDividends();
        this.companyProfit = source.getCompanyProfit();
        this.margin = source.getMargin();
        this.departmentPart = source.getDepartmentPart();
        this.shareValue = source.getShareValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdvisorParametersEntity)) return false;

        AdvisorParametersEntity that = (AdvisorParametersEntity) o;

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

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Set<AdvisorProductEntity> getProducts() {
        return products;
    }

    public BigDecimal getGeneralRunningCosts() {
        return generalRunningCosts;
    }

    public void setGeneralRunningCosts(BigDecimal generalRunningCosts) {
        this.generalRunningCosts = generalRunningCosts;
    }

    public BigDecimal getWage() {
        return wage;
    }

    public void setWage(BigDecimal wage) {
        this.wage = wage;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    public BigDecimal getTaxOnProfits() {
        return taxOnProfits;
    }

    public void setTaxOnProfits(BigDecimal taxOnProfits) {
        this.taxOnProfits = taxOnProfits;
    }

    public BigDecimal getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(BigDecimal incomeTax) {
        this.incomeTax = incomeTax;
    }

    public BigDecimal getProprietorshipInterest() {
        return proprietorshipInterest;
    }

    public void setProprietorshipInterest(BigDecimal proprietorshipInterest) {
        this.proprietorshipInterest = proprietorshipInterest;
    }

    public BigDecimal getTaxOnDividends() {
        return taxOnDividends;
    }

    public void setTaxOnDividends(BigDecimal taxOnDividends) {
        this.taxOnDividends = taxOnDividends;
    }

    public BigDecimal getCompanyProfit() {
        return companyProfit;
    }

    public void setCompanyProfit(BigDecimal companyProfit) {
        this.companyProfit = companyProfit;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public BigDecimal getShareValue() {
        return shareValue;
    }

    public void setShareValue(BigDecimal shareValue) {
        this.shareValue = shareValue;
    }

    public BigDecimal getDepartmentPart() {
        return departmentPart;
    }

    public void setDepartmentPart(BigDecimal departmentPart) {
        this.departmentPart = departmentPart;
    }

    public Set<AdvisorBonusAllocationEntity> getBonusAllocations() {
        return bonusAllocations;
    }

    public Set<AdvisorProductGroupEntity> getProductGroups() {
        return productGroups;
    }
}
