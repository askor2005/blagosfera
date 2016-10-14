package ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto;

import ru.radom.kabinet.model.cashbox.CashboxExchangeEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CashboxExchangeOperationDto {

    public Long id;
    public String requestId;
    public Date createdDate;
    public Date acceptedDate;
    public CashboxUserDto customer;
    public List<DocumentDto> documents = new ArrayList<>();
    public CashboxExchangeOperationTotalsDto totals;

    public CashboxExchangeOperationDto() {
    }

    public CashboxExchangeOperationDto(CashboxExchangeEntity exchange) {
        id = exchange.getId();
        requestId = exchange.getRequestId();
        createdDate = exchange.getCreatedDate();
        acceptedDate = exchange.getAcceptedDate();
        customer = new CashboxUserDto(exchange.getUserEntity());
        totals = new CashboxExchangeOperationTotalsDto(exchange.getTotals());
    }
}
