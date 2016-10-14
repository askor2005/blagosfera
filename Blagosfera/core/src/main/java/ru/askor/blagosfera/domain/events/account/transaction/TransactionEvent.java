package ru.askor.blagosfera.domain.events.account.transaction;

import ru.askor.blagosfera.domain.account.Transaction;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;

public class TransactionEvent extends BlagosferaEvent {

    private Transaction transaction;

    public TransactionEvent(Object source, Transaction transaction) {
        super(source);
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
