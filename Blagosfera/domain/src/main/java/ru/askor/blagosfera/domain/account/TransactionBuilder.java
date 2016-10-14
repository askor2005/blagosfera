package ru.askor.blagosfera.domain.account;

import java.time.LocalDateTime;
import org.springframework.util.Assert;
import ru.askor.blagosfera.domain.document.DocumentFolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Maxim Nikitin on 22.03.2016.
 */
public class TransactionBuilder {

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

    public TransactionBuilder() {
    }

    public TransactionBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public TransactionBuilder setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TransactionBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TransactionBuilder setSubmitDate(LocalDateTime submitDate) {
        this.submitDate = submitDate;
        return this;
    }

    public TransactionBuilder setPostDate(LocalDateTime postDate) {
        this.postDate = postDate;
        return this;
    }

    public TransactionBuilder addDetail(TransactionDetail newDetail) {
        Assert.notNull(newDetail);
        Assert.isTrue(!newDetail.getAmount().equals(BigDecimal.ZERO));

        boolean found = false;

        for (TransactionDetail detail : details) {
            if (detail.getAccountId().equals(newDetail.getAccountId())) {
                found = true;
                break;
            }
        }

        Assert.isTrue(!found);

        details.add(newDetail);
        return this;
    }

    public TransactionBuilder setDocumentFolder(DocumentFolder documentFolder) {
        this.documentFolder = documentFolder;
        return this;
    }

    public TransactionBuilder setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public TransactionBuilder setParameter(String key, String value) {
        this.parameters.put(key, value);
        return this;
    }

    public TransactionBuilder setState(TransactionState state) {
        this.state = state;
        return this;
    }

    public Transaction build() {
        Assert.notNull(amount);
        Assert.isTrue(amount.compareTo(BigDecimal.ZERO) > 0);
        Assert.notNull(documentFolder);
        Assert.notNull(transactionType);

        BigDecimal debitAmount = BigDecimal.ZERO;
        BigDecimal creditAmount = BigDecimal.ZERO;

        for (TransactionDetail detail : details) {
            switch (detail.getType()) {
                case DEBIT:
                    debitAmount = debitAmount.add(detail.getAmount());
                    break;
                case CREDIT:
                    creditAmount = creditAmount.add(detail.getAmount());
                    break;
            }
        }

        Assert.isTrue(debitAmount.equals(BigDecimal.ZERO) || debitAmount.equals(amount));
        Assert.isTrue(creditAmount.equals(BigDecimal.ZERO) || creditAmount.equals(amount));

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setSubmitDate(submitDate == null ? LocalDateTime.now() : submitDate);
        transaction.setPostDate(postDate);
        transaction.getDetails().addAll(details);
        transaction.setDocumentFolder(documentFolder);
        transaction.setTransactionType(transactionType);
        transaction.getParameters().putAll(parameters);
        transaction.setState(state);
        return transaction;
    }
}
