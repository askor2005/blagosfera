package ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StoreResponseDto {

    public long total = 0L;
    public List<ProductDto> data = new ArrayList<>();

    public BigDecimal directCosts = null;
    public BigDecimal finalCosts = null;
}
