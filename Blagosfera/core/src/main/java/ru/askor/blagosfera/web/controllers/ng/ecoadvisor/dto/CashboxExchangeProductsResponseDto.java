package ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CashboxExchangeProductsResponseDto {

    public long total = 0L;
    public List<ProductDto> data = new ArrayList<>();
    public BigDecimal wholesalePriceTotal = BigDecimal.ZERO;
    public BigDecimal wholesalePriceWithVatTotal = BigDecimal.ZERO;
    public BigDecimal finalPriceTotal = BigDecimal.ZERO;
    public BigDecimal finalPriceWithVatTotal = BigDecimal.ZERO;
}
