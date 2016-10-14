package ru.askor.blagosfera.data.jpa.entities.account;

import org.hibernate.annotations.Type;
import java.time.LocalDateTime;
import ru.askor.blagosfera.domain.account.Transaction;
import ru.askor.blagosfera.domain.account.TransactionState;
import ru.askor.blagosfera.domain.account.TransactionType;
import ru.radom.kabinet.document.model.DocumentFolderEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_generator")
    @SequenceGenerator(name = "transaction_id_generator", sequenceName = "transaction_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal amount;

    @Column(name = "description")
    @Type(type="text")
    private String description;

    @Column(name="submit_date", nullable = false)
    private LocalDateTime submitDate;

    @Column(name="post_date")
    private LocalDateTime postDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TransactionDetailEntity> details = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_folder_id")
    private DocumentFolderEntity documentFolder;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @ElementCollection(fetch = FetchType.LAZY)
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @Type(type="text")
    @CollectionTable(name = "transaction_parameters", joinColumns = @JoinColumn(name = "transaction_id"))
    private Map<String, String> parameters = new HashMap<>();

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionState state;

    public TransactionEntity() {
    }

    public TransactionEntity(Transaction transaction) {
        id = transaction.getId();
        amount = transaction.getAmount();
        description = transaction.getDescription();
        submitDate = transaction.getSubmitDate();
        postDate = transaction.getPostDate();
        transactionType = transaction.getTransactionType();
        parameters.putAll(transaction.getParameters());
        state = transaction.getState();
    }

    public Transaction toDomain(boolean withDocuments, boolean withParticipants) {
        Transaction transaction = new Transaction();
        transaction.setId(getId());
        transaction.setAmount(getAmount());
        transaction.setDescription(getDescription());
        transaction.setSubmitDate(getSubmitDate());
        transaction.setPostDate(getPostDate());

        for (TransactionDetailEntity detail : getDetails()) {
            transaction.getDetails().add(detail.toDomain());
        }

        if (getDocumentFolder() != null) {
            transaction.setDocumentFolder(getDocumentFolder().toDomain(withDocuments, withParticipants));
        }
        transaction.setTransactionType(getTransactionType());
        transaction.getParameters().putAll(getParameters());
        transaction.setState(getState());
        return transaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionEntity)) return false;

        TransactionEntity that = (TransactionEntity) o;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(LocalDateTime submitDate) {
        this.submitDate = submitDate;
    }

    public LocalDateTime getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDateTime postDate) {
        this.postDate = postDate;
    }

    public Set<TransactionDetailEntity> getDetails() {
        return details;
    }

    public DocumentFolderEntity getDocumentFolder() {
        return documentFolder;
    }

    public void setDocumentFolder(DocumentFolderEntity documentFolder) {
        this.documentFolder = documentFolder;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public TransactionState getState() {
        return state;
    }

    public void setState(TransactionState state) {
        this.state = state;
    }
}
