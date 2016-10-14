package ru.radom.kabinet.model.ecoadvisor;

import ru.askor.blagosfera.domain.ecoadvisor.AdvisorProduct;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "eco_advisor_products")
public class AdvisorProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eco_advisor_products_id_generator")
    @SequenceGenerator(name = "eco_advisor_products_id_generator", sequenceName = "eco_advisor_products_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "count", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal count;

    @Column(name = "unit_of_measure", nullable = false)
    private String unitOfMeasure;

    @Column(name = "wholesale_price", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal wholesalePrice;

    @Column(name = "wholesale_price_with_vat", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal wholesalePriceWithVat;

    @Column(name = "wholesale_currency", nullable = false)
    private String wholesaleCurrency;

    @Column(name = "final_price", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal finalPrice;

    @Column(name = "final_price_with_vat", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal finalPriceWithVat;

    @Column(name = "final_currency", nullable = false)
    private String finalCurrency;

    @Column(name = "margin", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal margin;

    @Column(name = "margin_percentage", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal marginPercentage;

    @Column(name = "vat", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal vat;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "updated_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "eco_advisor_parameters_id", nullable = false)
    private AdvisorParametersEntity parameters;

    @ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="group_id", nullable = true)
    private AdvisorProductGroupEntity group;

    public AdvisorProductEntity() {
    }

    public AdvisorProduct toDomain() {
        AdvisorProduct advisorProduct = new AdvisorProduct();
        advisorProduct.setId(getId());
        advisorProduct.setName(getName());
        advisorProduct.setCode(getCode());
        advisorProduct.setCount(getCount());
        advisorProduct.setUnitOfMeasure(getUnitOfMeasure());
        advisorProduct.setWholesalePrice(getWholesalePrice());
        advisorProduct.setWholesaleCurrency(getWholesaleCurrency());
        advisorProduct.setFinalPrice(getFinalPrice());
        advisorProduct.setFinalCurrency(getFinalCurrency());
        advisorProduct.setMargin(getMargin());
        advisorProduct.setMarginPercentage(getMarginPercentage());
        advisorProduct.setVat(getVat());
        advisorProduct.setCreatedDate(getCreatedDate());
        advisorProduct.setUpdatedDate(getUpdatedDate());
        return advisorProduct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdvisorProductEntity)) return false;

        AdvisorProductEntity that = (AdvisorProductEntity) o;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public BigDecimal getWholesalePrice() {
        return wholesalePrice;
    }

    public void setWholesalePrice(BigDecimal wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

    public BigDecimal getWholesalePriceWithVat() {
        return wholesalePriceWithVat;
    }

    public void setWholesalePriceWithVat(BigDecimal wholesalePriceWithVat) {
        this.wholesalePriceWithVat = wholesalePriceWithVat;
    }

    public String getWholesaleCurrency() {
        return wholesaleCurrency;
    }

    public void setWholesaleCurrency(String wholesaleCurrency) {
        this.wholesaleCurrency = wholesaleCurrency;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public BigDecimal getFinalPriceWithVat() {
        return finalPriceWithVat;
    }

    public void setFinalPriceWithVat(BigDecimal finalPriceWithVat) {
        this.finalPriceWithVat = finalPriceWithVat;
    }

    public String getFinalCurrency() {
        return finalCurrency;
    }

    public void setFinalCurrency(String finalCurrency) {
        this.finalCurrency = finalCurrency;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public BigDecimal getMarginPercentage() {
        return marginPercentage;
    }

    public void setMarginPercentage(BigDecimal marginPercentage) {
        this.marginPercentage = marginPercentage;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public AdvisorParametersEntity getParameters() {
        return parameters;
    }

    public void setParameters(AdvisorParametersEntity parameters) {
        this.parameters = parameters;
    }

    public AdvisorProductGroupEntity getGroup() {
        return group;
    }

    public void setGroup(AdvisorProductGroupEntity group) {
        this.group = group;
    }
}
