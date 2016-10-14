package ru.askor.blagosfera.core.services.robokassa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.data.jpa.entities.account.TransactionEntity;
import ru.askor.blagosfera.data.jpa.repositories.account.TransactionRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentFolderRepository;
import ru.askor.blagosfera.domain.account.*;
import ru.askor.blagosfera.domain.xml.robokassa.OperationStateResponse;
import ru.radom.kabinet.document.model.DocumentFolderEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by max on 22.07.16.
 */
@Transactional
@Service("robokassaService")
public class RobokassaServiceImpl implements RobokassaService {

    public static final String ROBOKASSA = "robokassa";

    @Autowired
    private AccountService accountService;

    @Autowired
    private DocumentFolderRepository documentFolderRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RobokassaClient robokassaClient;

    public RobokassaServiceImpl() {
    }

    @Async
    @Override
    public void checkPayment(Long invoiceId) {
        try {
            OperationStateResponse response = robokassaClient.getTransactionState(invoiceId);

            switch (response.getResult().getCode()) {
                case 0:
                    switch (response.getState().getCode()) {
                        case 5: // операция только инициализирована, деньги от покупателя не получены
                            break;
                        case 10: // операция отменена, деньги от покупателя не были получены
                            accountService.rejectTransaction(invoiceId);
                            break;
                        case 50: // деньги от покупателя получены, производится зачисление денег на счет магазина
                            break;
                        case 60: // деньги после получения были возвращены покупателю
                            accountService.rejectTransaction(invoiceId);
                            break;
                        case 80: // исполнение операции приостановлено
                            break;
                        case 100: // операция выполнена, завершена успешно
                            accountService.postTransaction(invoiceId);
                            break;
                    }

                    break;
                case 1: // неверная цифровая подпись запроса
                    break;
                case 3: // информация об операции с таким InvoiceID не найдена
                    break;
                case 4: // найдено две операции с таким InvoiceID. Такая ошибка возникает когда есть тестовая оплата с тем же InvoiceID
                    break;
            }
        } catch (Throwable ignored) {
        }
    }

    @Async
    @Override
    public void checkPayments() {
        List<TransactionEntity> payments = transactionRepository.findByParameterValueAndState(
                Transaction.PARAMETER_PAYMENT_SYSTEM, ROBOKASSA, TransactionState.HOLD);

        for (TransactionEntity payment : payments) {
            checkPayment(payment.getId());
        }
    }

    @Override
    public Transaction initPayment(Long userId, Long accountTypeId, BigDecimal amount, String description) {
        Account account = accountService.getUserAccount(userId, accountTypeId);

        if (account == null) return null;

        DocumentFolderEntity documentFolderEntity = new DocumentFolderEntity();
        documentFolderEntity.setName("payment");
        documentFolderEntity = documentFolderRepository.save(documentFolderEntity);

        TransactionDetail detail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.CREDIT)
                .setAmount(amount)
                .setAccountId(account.getId())
                .build();

        Transaction transaction = new TransactionBuilder()
                .setAmount(amount)
                .setDescription(description)
                .addDetail(detail)
                .setDocumentFolder(documentFolderEntity.toDomain(false, false))
                .setTransactionType(TransactionType.PAYMENT_SYSTEM)
                .setParameter(Transaction.PARAMETER_PAYMENT_SYSTEM, ROBOKASSA)
                .build();

        return accountService.submitTransaction(transaction);
    }
}
