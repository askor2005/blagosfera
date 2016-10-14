package ru.askor.blagosfera.data.jpa.services.account;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.askor.blagosfera.data.jpa.entities.account.AccountEntity;
import ru.askor.blagosfera.data.jpa.entities.account.AccountTypeEntity;
import ru.askor.blagosfera.data.jpa.entities.account.TransactionDetailEntity;
import ru.askor.blagosfera.data.jpa.entities.account.TransactionEntity;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.account.AccountRepository;
import ru.askor.blagosfera.data.jpa.repositories.account.AccountTypeRepository;
import ru.askor.blagosfera.data.jpa.repositories.account.TransactionRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.account.Account;
import ru.askor.blagosfera.domain.account.Transaction;
import ru.askor.blagosfera.domain.account.TransactionDetailType;
import ru.askor.blagosfera.domain.account.TransactionState;
import ru.askor.blagosfera.domain.community.Community;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.services.communities.CommunityDataService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис по работе со счетами пользователей
 */
@Transactional
@Service("accountDataService")
public class AccountDataServiceImpl implements AccountDataService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityDataService communityDataService;

    public AccountDataServiceImpl() {
    }

    @Override
    public AccountTypeEntity getPrimaryAccountType(String discriminator) {
        return accountTypeRepository.findFirstByOwnerDiscriminatorOrderByPositionAsc(discriminator);
    }

    @Override
    public AccountEntity getUserAccountEntity(Long userId, Long typeId) {
        AccountEntity account = accountRepository.getUserAccount(userId, typeId);

        if (account == null) {
            account = new AccountEntity();
            account.setOwner(userRepository.findOne(userId));
            account.setType(accountTypeRepository.findOne(typeId));
            account.setBalance(BigDecimal.ZERO);
            account.setHoldBalance(BigDecimal.ZERO);
            account = accountRepository.saveAndFlush(account);
        }

        return account;
    }

    @Override
    public AccountEntity getCommunityAccountEntity(Long communityId, Long typeId) {
        AccountEntity account = accountRepository.getCommunityAccount(communityId, typeId);

        if (account == null) {
            Community community = communityDataService.getByIdFullData(communityId);
            Assert.isTrue(ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.equals(community.getCommunityType()), "Организациии оформленные вне рамок юр.лица не могут иметь счёт.");

            account = new AccountEntity();
            account.setOwner(communityRepository.getOne(communityId));
            account.setType(accountTypeRepository.getOne(typeId));
            account.setBalance(BigDecimal.ZERO);
            account.setHoldBalance(BigDecimal.ZERO);
            account = accountRepository.saveAndFlush(account);
        }

        return account;
    }

    @Override
    public Transaction getTransaction(Long transactionId) {
        TransactionEntity entity = transactionRepository.findOne(transactionId);
        return entity != null ? entity.toDomain(true, true) : null;
    }

    @Override
    public Transaction getTransaction(Long transactionId, Long userId) {
        TransactionEntity transactionEntity = transactionRepository.findOne(transactionId);
        boolean found = false;

        for (TransactionDetailEntity transactionDetailEntity : transactionEntity.getDetails()) {
            Object accountOwner = transactionDetailEntity.getAccount().getOwner();

            if ((accountOwner instanceof UserEntity) && (((UserEntity) accountOwner).getId().equals(userId))) {
                found = true;
            }
        }

        if (found) return transactionEntity.toDomain(true, true);
        return null;
    }

    @Override
    public TransactionEntity postTransaction(TransactionEntity transactionEntity) {
        Assert.notNull(transactionEntity);
        Assert.isTrue(transactionEntity.getState() == TransactionState.HOLD);

        transactionEntity.setState(TransactionState.POST);
        transactionEntity.setPostDate(LocalDateTime.now());

        for (TransactionDetailEntity detailEntity : transactionEntity.getDetails()) {
            AccountEntity accountEntity = detailEntity.getAccount();

            if (detailEntity.getType() == TransactionDetailType.DEBIT) {
                accountEntity.setHoldBalance(accountEntity.getHoldBalance().subtract(detailEntity.getAmount()));
            } else {
                accountEntity.setBalance(accountEntity.getBalance().add(detailEntity.getAmount()));
            }

            accountRepository.save(accountEntity);
        }

        transactionEntity = transactionRepository.save(transactionEntity);
        return transactionEntity;
    }

    @Override
    public TransactionEntity rejectTransaction(TransactionEntity transactionEntity) {
        Assert.notNull(transactionEntity);
        Assert.isTrue(transactionEntity.getState() == TransactionState.HOLD);
        //if (transactionEntity.getState() != TransactionState.HOLD) return transactionEntity;

        transactionEntity.setState(TransactionState.REJECT);
        transactionEntity.setPostDate(LocalDateTime.now());

        for (TransactionDetailEntity detailEntity : transactionEntity.getDetails()) {
            AccountEntity accountEntity = detailEntity.getAccount();

            if (detailEntity.getType() == TransactionDetailType.DEBIT) {
                accountEntity.setHoldBalance(accountEntity.getHoldBalance().subtract(detailEntity.getAmount()));
                accountEntity.setBalance(accountEntity.getBalance().add(detailEntity.getAmount()));
                accountRepository.save(accountEntity);
            }
        }

        transactionEntity = transactionRepository.save(transactionEntity);
        return transactionEntity;
    }

    @Override
    public List<Account> getAccountsByIds(List<Long> accountIds) {
        return AccountEntity.toDomainList(accountRepository.findAll(accountIds));
    }
}
