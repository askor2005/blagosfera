package ru.askor.blagosfera.data.jpa.entities.account;

import ru.askor.blagosfera.domain.account.TransactionDetail;
import ru.askor.blagosfera.domain.account.TransactionDetailType;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Maxim Nikitin on 29.03.2016.
 */
@Entity
@Table(name = "transaction_detail")
public class TransactionDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_detail_id_generator")
    @SequenceGenerator(name = "transaction_detail_id_generator", sequenceName = "transaction_detail_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionEntity transaction;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionDetailType type;

    @Column(name = "amount", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    public TransactionDetailEntity() {
    }

    public TransactionDetail toDomain() {
        TransactionDetail detail = new TransactionDetail();
        detail.setId(getId());
        detail.setTransactionId(getTransaction().getId());
        detail.setType(getType());
        detail.setAmount(getAmount());
        detail.setAccountId(getAccount().getId());
        return detail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionDetailEntity)) return false;

        TransactionDetailEntity that = (TransactionDetailEntity) o;

        //return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionEntity getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionEntity transaction) {
        this.transaction = transaction;
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

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }
}
