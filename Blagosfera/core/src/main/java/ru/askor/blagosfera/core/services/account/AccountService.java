package ru.askor.blagosfera.core.services.account;

import ru.askor.blagosfera.data.jpa.entities.account.SharebookEntity;
import ru.askor.blagosfera.data.jpa.entities.account.TransactionEntity;
import ru.askor.blagosfera.domain.account.Account;
import ru.askor.blagosfera.domain.account.PaymentSystem;
import ru.askor.blagosfera.domain.account.Transaction;
import ru.askor.blagosfera.domain.account.TransactionState;
import ru.radom.kabinet.web.admin.dto.TransactionListDto;
import ru.radom.kabinet.web.admin.dto.TransactionPlainModel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by Maxim Nikitin on 04.03.2016.
 */
public interface AccountService {

    @Deprecated
    void createTransactionUser2Community(Long userId, Long userAccountTypeId, Long communityId, BigDecimal amount, String decription);

    @Deprecated
    void createTransactionCommunity2User(Long userId, Long communityId, Long recipientUserId, BigDecimal amount, String decription);

    @Deprecated
    void createTransactionCommunity2Community(Long userId, Long communityId, Long recipientCommunityId, BigDecimal amount, String decription);

    @Deprecated
    Transaction createTransaction(Long userId, Long accountId, Long recipientAccountId, BigDecimal amount, String decription);

    @Deprecated
    void createTransactionUser2User(Long userId, Long userAccountTypeId, Long recipientUserId, Long recipientUserAccountTypeId, BigDecimal amount, String decription);

    @Deprecated
    Transaction createTransactionSharebook2Account(Long sharebookId, Long recipientAccountId, BigDecimal amount, String decription);

    @Deprecated
    Transaction createTransactionSharebook2Account(Long sharebookId, Long recipientAccountId, BigDecimal amount, String decription, boolean checkMinAmount);

    @Deprecated
    Transaction createTransactionAccount2Sharebook(Long accountId, Long recipientSharebookId, BigDecimal amount, String decription);

    SharebookEntity getSharebook(Object sharebookOwner, Long communityId);

    List<Transaction> getTransactions(List<Long> accountIds, int page, int size, Date dateFrom, Date dateTo, Long accountTypeId);

    List<Long> getCommunityAccountIds(Long communityId);

    List<Transaction> getCommunityTransactions(Long communityId, int page, int size, Date dateFrom, Date dateTo, Long accountTypeId);

    List<Transaction> getTransactions(Long userId, int page, int size, Date dateFrom, Date dateTo, Long accountTypeId);

    Transaction getTransaction(Long transctionId);

    /**
     * создать новую транзакцию. В результате выполнения данного метода создается новая транзакция со статусом HOLD, и происходит замораживание суммы транзакции на дебитовых счетах
     *
     * @param transaction параметры транзакции
     * @return новая транзакция со статусом HOLD
     */
    Transaction submitTransaction(Transaction transaction);

    /**
     * подтвердить транзакцию. В результате выполнения данного метода транзакция переходит из статуса HOLD в статус POST, и происходит размораживание и зачисление суммы транзакции на кредитные счета
     *
     * @param transactionId идентификатор транзакция в статусе HOLD
     */
    void postTransaction(Long transactionId);

    /**
     * подтвердить все транзакции из списка
     *
     * @param transactions список транзакций в статусе HOLD
     */
    void postTransactions(List<Transaction> transactions);

    /**
     * отменить транзакцию. В результате выполнения данного метода транзакция переходит из статуса HOLD в статус REJECT, и происходит размораживание и возврат суммы транзакции на дебитные счета
     *
     * @param transactionId идентификатор транзакция в статусе HOLD
     */
    void rejectTransaction(Long transactionId);

    /**
     * отменить все транзакции из списка
     *
     * @param transactions список транзакций в статусе HOLD
     */
    void rejectTransactions(List<Transaction> transactions);

    TransactionListDto searchTransactions(Long userId, int page, int size,
                                          Date dateFrom, Date dateTo, Long accountTypeId,
                                          TransactionPlainModel.Type type, TransactionState state);

    /**
     * получить все счета пользователя
     *
     * @param userId
     * @return
     */
    List<Account> getUserAccounts(Long userId);

    /**
     * получить счет пользователя
     *
     * @param userId
     * @return
     */
    Account getUserAccount(Long userId, Long typeId);

    /**
     * получить все счета объединения
     *
     * @param communityId
     * @return
     */
    List<Account> getCommunityAccounts(Long communityId);

    /**
     * получить счет объединения
     *
     * @param communityId
     * @return
     */
    Account getCommunityAccount(Long communityId, Long typeId);

    TransactionPlainModel getTransactionPlainModel(TransactionEntity transactionEntity, List<Long> accountsIds);

    Long getUserAccountsCounts(Long id);

    List<PaymentSystem> getPaymentSystems();
}
