package ru.askor.blagosfera.domain.cashbox;

import java.math.BigDecimal;

public class CashboxBasketItem {

    private Long id;
    private String name;
    private String code;
    private BigDecimal count;
    private BigDecimal baseCount;
    private String unitOfMeasure;
    private BigDecimal wholesalePrice;
    private BigDecimal wholesalePriceWithVat;
    private String wholesaleCurrency;
    private BigDecimal finalPrice;
    private BigDecimal finalPriceWithVat;
    private String finalCurrency;
    private BigDecimal vat;

    public CashboxBasketItem() {
    }

    public CashboxBasketItem(Long id, String name, String code,
                             BigDecimal count, BigDecimal baseCount, String unitOfMeasure,
                             BigDecimal wholesalePrice, BigDecimal wholesalePriceWithVat, String wholesaleCurrency,
                             BigDecimal finalPrice, BigDecimal finalPriceWithVat, String finalCurrency, BigDecimal vat) {
        this.id = id;
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
}
