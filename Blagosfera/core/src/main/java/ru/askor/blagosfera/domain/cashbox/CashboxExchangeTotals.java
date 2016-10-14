package ru.askor.blagosfera.domain.cashbox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CashboxExchangeTotals {

    private Long id;
    private BigDecimal totalWholesaleAmount;
    private BigDecimal membershipFee;
    private BigDecimal totalFinalAmount;
    private BigDecimal paymentAmount;
    private BigDecimal changeAmount;
    private BigDecimal totalMargin;
    private BigDecimal totalProfit;
    private boolean cash;
    private List<CashboxBasketItem> basketItems = new ArrayList<>();

    public CashboxExchangeTotals() {
    }

    public CashboxExchangeTotals(BigDecimal totalWholesaleAmount, BigDecimal membershipFee, BigDecimal totalFinalAmount,
                                 BigDecimal paymentAmount, BigDecimal changeAmount, BigDecimal totalMargin,
                                 BigDecimal totalProfit, boolean cash) {
        this.totalWholesaleAmount = totalWholesaleAmount;
        this.membershipFee = membershipFee;
        this.totalFinalAmount = totalFinalAmount;
        this.paymentAmount = paymentAmount;
        this.changeAmount = changeAmount;
        this.totalMargin = totalMargin;
        this.totalProfit = totalProfit;
        this.cash = cash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalWholesaleAmount() {
        return totalWholesaleAmount;
    }

    public void setTotalWholesaleAmount(BigDecimal totalWholesaleAmount) {
        this.totalWholesaleAmount = totalWholesaleAmount;
    }

    public BigDecimal getMembershipFee() {
        return membershipFee;
    }

    public void setMembershipFee(BigDecimal membershipFee) {
        this.membershipFee = membershipFee;
    }

    public BigDecimal getTotalFinalAmount() {
        return totalFinalAmount;
    }

    public void setTotalFinalAmount(BigDecimal totalFinalAmount) {
        this.totalFinalAmount = totalFinalAmount;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public BigDecimal getTotalMargin() {
        return totalMargin;
    }

    public void setTotalMargin(BigDecimal totalMargin) {
        this.totalMargin = totalMargin;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public boolean isCash() {
        return cash;
    }

    public void setCash(boolean cash) {
        this.cash = cash;
    }

    public List<CashboxBasketItem> getBasketItems() {
        return basketItems;
    }
}
