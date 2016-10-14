package ru.radom.kabinet.model.cashbox;

import ru.askor.blagosfera.domain.cashbox.CashboxBasketItem;
import ru.askor.blagosfera.domain.cashbox.CashboxExchangeTotals;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cashbox_exchange_totals")
public class CashboxExchangeTotalsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cashbox_exchange_totals_id_generator")
    @SequenceGenerator(name = "cashbox_exchange_totals_id_generator", sequenceName = "cashbox_exchange_totals_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id", updatable = false, nullable = false)
    private CashboxExchangeEntity exchangeOperation;

    @Column(name = "total_wholesale_amount", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal totalWholesaleAmount;

    @Column(name = "membership_fee", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal membershipFee;

    @Column(name = "total_final_amount", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal totalFinalAmount;

    @Column(name = "payment_amount", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal paymentAmount;

    @Column(name = "change_amount", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal changeAmount;

    @Column(name = "total_margin", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal totalMargin;

    @Column(name = "total_profit", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal totalProfit;

    @Column(name = "cash", nullable = false)
    private boolean cash;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="totals", cascade = {CascadeType.ALL})
    private List<CashboxBasketItemEntity> basketItems = new ArrayList<>();

    public CashboxExchangeTotalsEntity() {
    }

    public CashboxExchangeTotalsEntity(CashboxExchangeTotals cashboxExchangeTotals) {
        id = cashboxExchangeTotals.getId();
        totalWholesaleAmount = cashboxExchangeTotals.getTotalWholesaleAmount();
        membershipFee = cashboxExchangeTotals.getMembershipFee();
        totalFinalAmount = cashboxExchangeTotals.getTotalFinalAmount();
        paymentAmount = cashboxExchangeTotals.getPaymentAmount();
        changeAmount = cashboxExchangeTotals.getChangeAmount();
        totalMargin = cashboxExchangeTotals.getTotalMargin();
        totalProfit = cashboxExchangeTotals.getTotalProfit();
        cash = cashboxExchangeTotals.isCash();

        for (CashboxBasketItem basketItem : cashboxExchangeTotals.getBasketItems()) {
            CashboxBasketItemEntity basketItemEntity = new CashboxBasketItemEntity(basketItem);
            basketItemEntity.setTotals(this);
            basketItems.add(basketItemEntity);
        }
    }

    public CashboxExchangeTotals toDomain() {
        CashboxExchangeTotals exchangeTotals = new CashboxExchangeTotals();
        exchangeTotals.setId(getId());
        exchangeTotals.setTotalWholesaleAmount(getTotalWholesaleAmount());
        exchangeTotals.setMembershipFee(getMembershipFee());
        exchangeTotals.setTotalFinalAmount(getTotalFinalAmount());
        exchangeTotals.setPaymentAmount(getPaymentAmount());
        exchangeTotals.setChangeAmount(getChangeAmount());
        exchangeTotals.setTotalMargin(getTotalMargin());
        exchangeTotals.setTotalProfit(getTotalProfit());
        exchangeTotals.setCash(isCash());

        for (CashboxBasketItemEntity basketItemEntity : basketItems) {
            exchangeTotals.getBasketItems().add(basketItemEntity.toDomain());
        }

        return exchangeTotals;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CashboxExchangeEntity getExchangeOperation() {
        return exchangeOperation;
    }

    public void setExchangeOperation(CashboxExchangeEntity exchangeOperation) {
        this.exchangeOperation = exchangeOperation;
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

    public List<CashboxBasketItemEntity> getBasketItems() {
        return basketItems;
    }
}
