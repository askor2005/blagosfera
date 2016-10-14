package ru.askor.blagosfera.domain.account;

import org.springframework.util.Assert;

import java.math.BigDecimal;

/**
 * Created by Maxim Nikitin on 22.03.2016.
 */
public class TransactionDetailBuilder {

    private Long id;
    private Long transactionId;
    private TransactionDetailType type;
    private BigDecimal amount;
    private Long accountId;

    public TransactionDetailBuilder() {
    }

    public TransactionDetailBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public TransactionDetailBuilder setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public TransactionDetailBuilder setType(TransactionDetailType type) {
        this.type = type;
        return this;
    }

    public TransactionDetailBuilder setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TransactionDetailBuilder setAccountId(Long accountId) {
        this.accountId = accountId;
        return this;
    }

    public TransactionDetail build() {
        Assert.notNull(type);
        Assert.notNull(amount);
        Assert.isTrue(amount.compareTo(BigDecimal.ZERO) > 0);
        Assert.notNull(accountId);

        TransactionDetail detail = new TransactionDetail();
        detail.setId(id);
        detail.setTransactionId(transactionId);
        detail.setType(type);
        detail.setAmount(amount);
        detail.setAccountId(accountId);
        return detail;
    }
}
