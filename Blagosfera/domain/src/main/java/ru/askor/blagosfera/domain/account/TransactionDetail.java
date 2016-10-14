package ru.askor.blagosfera.domain.account;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Maxim Nikitin on 22.03.2016.
 */
public class TransactionDetail implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;
    private Long transactionId;
    private TransactionDetailType type;
    private BigDecimal amount;
    private Long accountId;

    public TransactionDetail() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionDetailType getType() {
        return type;
    }

    public void setType(TransactionDetailType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
