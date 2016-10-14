package ru.askor.blagosfera.domain.cashbox;

public class CashboxException extends Exception {

    public CashboxException(String message) {
        super(message);
    }

    public CashboxException(String message, Throwable cause) {
        super(message, cause);
    }
}
