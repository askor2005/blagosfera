package ru.askor.blagosfera.data.jpa.services.account;

import ru.askor.blagosfera.data.jpa.entities.account.AccountEntity;
import ru.askor.blagosfera.data.jpa.entities.account.AccountTypeEntity;
import ru.askor.blagosfera.data.jpa.entities.account.TransactionEntity;
import ru.askor.blagosfera.domain.account.Account;
import ru.askor.blagosfera.domain.account.Transaction;

import java.util.List;

/**
 *
 * Created by Maxim Nikitin on 04.03.2016.
 */
public interface AccountDataService {

    /**
     * получить основной тип счета по дискриминатору
     *
     * @param discriminator
     * @return
     */
    @Deprecated
    AccountTypeEntity getPrimaryAccountType(String discriminator);

    /**
     * получить транзакцию по id
     *
     * @param transactionId
     * @return
     */
    Transaction getTransaction(Long transactionId);

    /**
     * получить транзакцию для пользователя по id
     *
     * @param transactionId
     * @return
     */
    Transaction getTransaction(Long transactionId, Long userId);

    TransactionEntity postTransaction(TransactionEntity transactionEntity);

    TransactionEntity rejectTransaction(TransactionEntity transactionEntity);

    /**
     * получить счет пользователя
     *
     * @param userId
     * @return
     */
    AccountEntity getUserAccountEntity(Long userId, Long typeId);

    /**
     * получить счет объединения
     *
     * @param communityId
     * @return
     */
    AccountEntity getCommunityAccountEntity(Long communityId, Long typeId);

    List<Account> getAccountsByIds(List<Long> accountIds);
}
