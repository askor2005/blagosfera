package ru.askor.blagosfera.core.services.robokassa;

import ru.askor.blagosfera.domain.xml.robokassa.OperationStateResponse;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

/**
 * Created by max on 22.07.16.
 */
public interface RobokassaClient {

    OperationStateResponse getTransactionState(Long transId) throws UnsupportedEncodingException;

    String calculateCrc(BigDecimal amount, Long invoiceId, boolean usePass1, boolean useLogin) throws UnsupportedEncodingException;

    String getLogin();

    String getTest();

    void init();
}
