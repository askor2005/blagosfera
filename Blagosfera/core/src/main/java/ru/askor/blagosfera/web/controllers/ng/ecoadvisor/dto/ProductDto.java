package ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto;

import ru.radom.kabinet.model.cashbox.CashboxBasketItemEntity;
import ru.radom.kabinet.model.ecoadvisor.AdvisorProductEntity;

import java.math.BigDecimal;

public class ProductDto {

    public Long id;
    public String name;
    public String code;
    public BigDecimal count;
    public BigDecimal baseCount;
    public String unitOfMeasure;
    public BigDecimal wholesalePrice;
    public BigDecimal wholesalePriceWithVat;
    public BigDecimal wholesalePriceTotal;
    public BigDecimal wholesalePriceWithVatTotal;
    public String wholesaleCurrency;
    public BigDecimal finalPrice;
    public BigDecimal finalPriceWithVat;
    public BigDecimal finalPriceTotal;
    public BigDecimal finalPriceWithVatTotal;
    public String finalCurrency;
    public BigDecimal vat;
    public boolean inStore;
    public BigDecimal margin;
    public BigDecimal marginPercentage;
    public ProductGroupDto group;

    public ProductDto() {
    }

    public ProductDto(CashboxBasketItemEntity basketItem) {
        id = basketItem.getId();
        name = basketItem.getName();
        code = basketItem.getCode();
        count = basketItem.getCount();
        baseCount = basketItem.getBaseCount();
        unitOfMeasure = basketItem.getUnitOfMeasure();
        wholesalePrice = basketItem.getWholesalePrice();
        wholesalePriceWithVat = basketItem.getWholesalePriceWithVat();
        wholesaleCurrency = basketItem.getWholesaleCurrency();
        finalPrice = basketItem.getFinalPrice();
        finalPriceWithVat = basketItem.getFinalPriceWithVat();
        finalCurrency = basketItem.getFinalCurrency();
        vat = basketItem.getVat();

        wholesalePriceTotal = wholesalePrice.multiply(count);
        wholesalePriceWithVatTotal = wholesalePriceWithVat.multiply(count);
        finalPriceTotal = finalPrice.multiply(count);
        finalPriceWithVatTotal = finalPriceWithVat.multiply(count);
    }

    public ProductDto(AdvisorProductEntity basketItem) {
        id = basketItem.getId();
        name = basketItem.getName();
        code = basketItem.getCode();
        count = basketItem.getCount();
        baseCount = basketItem.getCount();
        unitOfMeasure = basketItem.getUnitOfMeasure();
        wholesalePrice = basketItem.getWholesalePrice();
        wholesalePriceWithVat = basketItem.getWholesalePriceWithVat();
        wholesaleCurrency = basketItem.getWholesaleCurrency();
        finalPrice = basketItem.getFinalPrice();
        finalPriceWithVat = basketItem.getFinalPriceWithVat();
        finalCurrency = basketItem.getFinalCurrency();
        vat = basketItem.getVat();
        margin = basketItem.getMargin();
        marginPercentage = basketItem.getMarginPercentage();
        if (basketItem.getGroup() != null) group = new ProductGroupDto(basketItem.getGroup());
    }
}
