package ru.askor.blagosfera.core.services.robokassa;

import ru.askor.blagosfera.domain.account.Transaction;

import java.math.BigDecimal;

/**
 * Created by max on 22.07.16.
 */
public interface RobokassaService {

    void checkPayment(Long invoiceId);

    void checkPayments();

    Transaction initPayment(Long userId, Long accountTypeId, BigDecimal amount, String description);
}
