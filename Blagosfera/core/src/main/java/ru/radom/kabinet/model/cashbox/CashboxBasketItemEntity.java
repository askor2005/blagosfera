package ru.radom.kabinet.model.cashbox;

import ru.askor.blagosfera.domain.cashbox.CashboxBasketItem;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cashbox_basket_item")
public class CashboxBasketItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cashbox_basket_item_id_generator")
    @SequenceGenerator(name = "cashbox_basket_item_id_generator", sequenceName = "cashbox_basket_item_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "count", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal count;

    @Column(name = "base_count", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal baseCount;

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

    @Column(name = "vat", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal vat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_totals_id", updatable = false, nullable = false)
    private CashboxExchangeTotalsEntity totals;

    public CashboxBasketItemEntity() {
    }

    public CashboxBasketItemEntity(String name, String code, BigDecimal count,
                                   BigDecimal baseCount, String unitOfMeasure, BigDecimal wholesalePrice,
                                   BigDecimal wholesalePriceWithVat, String wholesaleCurrency, BigDecimal finalPrice,
                                   BigDecimal finalPriceWithVat, String finalCurrency, BigDecimal vat) {
        this.name = name;
        this.code = code;
        this.count = count;
        this.baseCount = baseCount;
        this.unitOfMeasure = unitOfMeasure;
        this.wholesalePrice = wholesalePrice;
        this.wholesalePriceWithVat = wholesalePriceWithVat;
        this.wholesaleCurrency = wholesaleCurrency;
        this.finalPrice = finalPrice;
        this.finalPriceWithVat = finalPriceWithVat;
        this.finalCurrency = finalCurrency;
        this.vat = vat;
    }

    public CashboxBasketItemEntity(CashboxBasketItem cashboxBasketItem) {
        id = cashboxBasketItem.getId();
        name = cashboxBasketItem.getName();
        code = cashboxBasketItem.getCode();
        count = cashboxBasketItem.getCount();
        baseCount = cashboxBasketItem.getBaseCount();
        unitOfMeasure = cashboxBasketItem.getUnitOfMeasure();
        wholesalePrice = cashboxBasketItem.getWholesalePrice();
        wholesalePriceWithVat = cashboxBasketItem.getWholesalePriceWithVat();
        wholesaleCurrency = cashboxBasketItem.getWholesaleCurrency();
        finalPrice = cashboxBasketItem.getFinalPrice();
        finalPriceWithVat = cashboxBasketItem.getFinalPriceWithVat();
        finalCurrency = cashboxBasketItem.getFinalCurrency();
        vat = cashboxBasketItem.getVat();
    }

    public CashboxBasketItem toDomain() {
        return new CashboxBasketItem(getId(), getName(), getCode(),
                getCount(), getBaseCount(), getUnitOfMeasure(),
                getWholesalePrice(), getWholesalePriceWithVat(), getWholesaleCurrency(),
                getFinalPrice(), getFinalPriceWithVat(), getFinalCurrency(), getVat());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CashboxBasketItemEntity)) return false;

        CashboxBasketItemEntity that = (CashboxBasketItemEntity) o;

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

    public BigDecimal getBaseCount() {
        return baseCount;
    }

    public void setBaseCount(BigDecimal baseCount) {
        this.baseCount = baseCount;
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

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    public CashboxExchangeTotalsEntity getTotals() {
        return totals;
    }

    public void setTotals(CashboxExchangeTotalsEntity totals) {
        this.totals = totals;
    }
}
