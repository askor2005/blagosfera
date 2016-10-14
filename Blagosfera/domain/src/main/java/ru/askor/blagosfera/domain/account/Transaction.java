package ru.askor.blagosfera.domain.account;

import java.time.LocalDateTime;
import ru.askor.blagosfera.domain.document.DocumentFolder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Maxim Nikitin on 22.03.2016.
 */
public class Transaction implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final String PARAMETER_USER_ID = "userId";
    public static final String PARAMETER_PAYMENT_ID = "paymentId";
    public static final String PARAMETER_PAYMENT_SYSTEM = "paymentSystem";
    public static final String PARAMETER_POST_ON_DOCUMENT_SIGNED = "postOnDocumentsSigned";

    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDateTime submitDate;
    private LocalDateTime postDate;
    private List<TransactionDetail> details = new ArrayList<>();
    private DocumentFolder documentFolder;
    private TransactionType transactionType;
    private Map<String, String> parameters = new HashMap<>();
    private TransactionState state;

    public Transaction() {
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

    public List<TransactionDetail> getDetails() {
        return details;
    }

    public DocumentFolder getDocumentFolder() {
        return documentFolder;
    }

    public void setDocumentFolder(DocumentFolder documentFolder) {
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
