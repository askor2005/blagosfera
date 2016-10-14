package ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto;

import ru.radom.kabinet.model.cashbox.CashboxExchangeTotalsEntity;

import java.math.BigDecimal;

public class CashboxExchangeOperationTotalsDto {

    public BigDecimal paymentAmount;
    public BigDecimal changeAmount;
    public BigDecimal totalWholesaleAmount;
    public BigDecimal totalFinalAmount;
    public BigDecimal membershipFee;
    public BigDecimal totalMargin;
    public BigDecimal totalProfit;
    public boolean cash;

    public CashboxExchangeOperationTotalsDto() {
    }

    public CashboxExchangeOperationTotalsDto(CashboxExchangeTotalsEntity totals) {
        paymentAmount = totals.getPaymentAmount().add(totals.getChangeAmount());
        changeAmount = totals.getChangeAmount();
        totalWholesaleAmount = totals.getTotalWholesaleAmount();
        totalFinalAmount = totals.getTotalFinalAmount();
        membershipFee = totals.getMembershipFee();
        totalMargin = totals.getTotalMargin();
        totalProfit = totals.getTotalProfit();
        cash = totals.isCash();
    }
}
