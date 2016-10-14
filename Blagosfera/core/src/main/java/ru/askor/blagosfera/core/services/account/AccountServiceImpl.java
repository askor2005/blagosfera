package ru.askor.blagosfera.core.services.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.askor.blagosfera.core.util.DateUtils;
import ru.askor.blagosfera.data.jpa.entities.account.*;
import ru.askor.blagosfera.data.jpa.repositories.SharebookRepository;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.account.AccountRepository;
import ru.askor.blagosfera.data.jpa.repositories.account.AccountTypeRepository;
import ru.askor.blagosfera.data.jpa.repositories.account.TransactionDetailRepository;
import ru.askor.blagosfera.data.jpa.repositories.account.TransactionRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentFolderRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentRepository;
import ru.askor.blagosfera.data.jpa.services.account.AccountDataService;
import ru.askor.blagosfera.data.jpa.specifications.transactions.TransactionSpecifications;
import ru.askor.blagosfera.domain.account.*;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.account.payment.PaymentStatusEvent;
import ru.askor.blagosfera.domain.events.account.transaction.TransactionEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEventType;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.payment.PaymentDao;
import ru.radom.kabinet.dao.payment.PaymentSystemDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.document.model.DocumentFolderEntity;
import ru.radom.kabinet.document.model.DocumentParticipantEntity;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.notifications.SystemAccountEntity;
import ru.radom.kabinet.model.payment.AccountPayment;
import ru.radom.kabinet.model.payment.Payment;
import ru.radom.kabinet.model.payment.PaymentStatus;
import ru.radom.kabinet.model.payment.PaymentSystemEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.services.TransactionException;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.utils.CommunityPermissionUtils;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.web.admin.dto.TransactionDetailPlainModel;
import ru.radom.kabinet.web.admin.dto.TransactionListDto;
import ru.radom.kabinet.web.admin.dto.TransactionPlainModel;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис по работе со счетами пользователей
 */
@Transactional
@Service("accountService")
public class AccountServiceImpl implements AccountService {
    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionDetailRepository transactionDetailRepository;

    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    @Autowired
    private SharebookRepository sharebookRepository;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentFolderRepository documentFolderRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private AccountDataService accountDataService;

    @Autowired
    private PaymentSystemDao paymentSystemDao;

    public AccountServiceImpl() {
    }

    /*@PostConstruct
    private void fixSharebooks() {
        List<SharebookEntity> existingSharebooks = sharebookRepository.findAll();
        Map<Long, Map<Long, List<SharebookEntity>>> communitySharebooks = new HashMap<>();

        for (SharebookEntity sharebook : existingSharebooks) {
            if (sharebook.getSharebookOwner() instanceof UserEntity) {
                Long communityId = sharebook.getCommunity().getId();
                Long userId = ((UserEntity) sharebook.getSharebookOwner()).getId();
                Map<Long, List<SharebookEntity>> userSharebooks = communitySharebooks.get(communityId);

                if (userSharebooks == null) {
                    userSharebooks = new HashMap<>();
                    communitySharebooks.put(communityId, userSharebooks);
                }

                List<SharebookEntity> sharebooks = userSharebooks.get(userId);

                if (sharebooks == null) {
                    sharebooks = new ArrayList<>();
                    userSharebooks.put(userId, sharebooks);
                }

                sharebooks.add(sharebook);
            }
        }

        for (Map.Entry<Long, Map<Long, List<SharebookEntity>>> entry : communitySharebooks.entrySet()) {
            Map<Long, List<SharebookEntity>> userSharebooks = entry.getValue();

            for (Map.Entry<Long, List<SharebookEntity>> entry2 : userSharebooks.entrySet()) {
                List<SharebookEntity> sharebooks = entry2.getValue();

                if (sharebooks.size() > 1) {
                    SharebookEntity mainSharebook = sharebooks.get(0);
                    AccountEntity mainAccount = mainSharebook.getAccount();
                    AccountEntity mainBonusAccount = mainSharebook.getBonusAccount();

                    for (int i = 1; i < sharebooks.size(); i++) {
                        SharebookEntity sharebook = sharebooks.get(i);
                        AccountEntity account = sharebook.getAccount();
                        AccountEntity bonusAccount = sharebook.getBonusAccount();

                        mainAccount.setTotalBalance(mainAccount.getTotalBalance().add(account.getTotalBalance()));
                        mainAccount.setHoldBalance(mainAccount.getHoldBalance().add(account.getHoldBalance()));
                        mainAccount = accountRepository.save(mainAccount);

                        mainBonusAccount.setTotalBalance(mainBonusAccount.getTotalBalance().add(bonusAccount.getTotalBalance()));
                        mainBonusAccount.setHoldBalance(mainBonusAccount.getHoldBalance().add(bonusAccount.getHoldBalance()));
                        mainBonusAccount = accountRepository.save(mainBonusAccount);

                        List<TransactionDetailEntity> transactionDetails = transactionDetailRepository.findAllByAccount_Id(account.getId());

                        for (TransactionDetailEntity transactionDetail : transactionDetails) {
                            transactionDetail.setAccount(mainAccount);
                            transactionDetailRepository.save(transactionDetail);
                        }

                        transactionDetails = transactionDetailRepository.findAllByAccount_Id(bonusAccount.getId());

                        for (TransactionDetailEntity transactionDetail : transactionDetails) {
                            transactionDetail.setAccount(mainBonusAccount);
                            transactionDetailRepository.save(transactionDetail);
                        }

                        sharebookRepository.delete(sharebook);
                    }
                }
            }
        }
    }*/

    //получение (или заведение новой) паевой книжки
    @Override
    public SharebookEntity getSharebook(Object shareBookOwner, Long communityId) {
        Long sharebookOwnerId = getSharebookOwnerId(shareBookOwner);
        Object shareBookOwnerEntity = getSharebookOwnerEntity(shareBookOwner);

        if (canHaveBookAccount(shareBookOwnerEntity, communityId)) {
            SharebookEntity sharebook = sharebookRepository.findOneByOwner_IdAndCommunity_Id(sharebookOwnerId, communityId);
            boolean save = false;

            if (sharebook == null) {
                sharebook = new SharebookEntity();
                sharebook.setSharebookOwner(shareBookOwnerEntity);
                sharebook.setCommunity(communityRepository.findOne(communityId));
                save = true;
            }

            if (sharebook.getAccount() == null) {
                AccountEntity account = new AccountEntity();
                account.setType(accountDataService.getPrimaryAccountType(Discriminators.SHARER_BOOK));
                account.setHoldBalance(new BigDecimal(0));
                account.setBalance(new BigDecimal(0));
                account = accountRepository.save(account);

                sharebook.setAccount(account);
                save = true;
            }

            if (sharebook.getBonusAccount() == null) {
                AccountEntity account = new AccountEntity();
                account.setType(accountDataService.getPrimaryAccountType(Discriminators.SHARER_BOOK));
                account.setHoldBalance(new BigDecimal(0));
                account.setBalance(new BigDecimal(0));
                account = accountRepository.save(account);

                sharebook.setBonusAccount(account);
                save = true;
            }

            if (save) {
                sharebook = sharebookRepository.save(sharebook);

                sharebook.getAccount().setOwner(sharebook);
                accountRepository.save(sharebook.getAccount());

                sharebook.getBonusAccount().setOwner(sharebook);
                accountRepository.save(sharebook.getBonusAccount());
            }

            return sharebook;
        }

        return null;
    }

    @Override
    public void createTransactionUser2Community(Long userId, Long userAccountTypeId, Long communityId, BigDecimal amount, String decription) {
        AccountTypeEntity communityAccountType = accountDataService.getPrimaryAccountType(Discriminators.COMMUNITY);
        Assert.notNull(communityAccountType);

        Account userAccount = getUserAccount(userId, userAccountTypeId);
        Assert.notNull(userAccount);

        Account communityAccount = getCommunityAccount(communityId, communityAccountType.getId());
        Assert.notNull(communityAccount);

        List<FlowOfDocumentStateEvent> stateEvents = Collections.singletonList(new FlowOfDocumentStateEvent(this, null, FlowOfDocumentStateEventType.DOCUMENT_SIGNED));
        DocumentEntity document = documentService.createSharerToCommunityMoveDocument(userRepository.findOne(userId), communityRepository.findOne(communityId), amount, stateEvents, userId);

        DocumentFolderEntity documentFolderEntity = new DocumentFolderEntity();
        documentFolderEntity.setName("user2community");
        documentFolderEntity = documentFolderRepository.save(documentFolderEntity);

        document.setFolder(documentFolderEntity);
        document = documentRepository.save(document);

        documentFolderEntity.getDocuments().add(document);
        documentFolderEntity = documentFolderRepository.save(documentFolderEntity);

        TransactionDetail debitDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.DEBIT)
                .setAmount(amount)
                .setAccountId(userAccount.getId())
                .build();

        TransactionDetail creditDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.CREDIT)
                .setAmount(amount)
                .setAccountId(communityAccount.getId())
                .build();

        Transaction transaction = new TransactionBuilder()
                .setAmount(amount)
                .setDescription(decription)
                .addDetail(debitDetail)
                .addDetail(creditDetail)
                .setDocumentFolder(documentFolderEntity.toDomain(false, false))
                .setTransactionType(TransactionType.USER_COMMUNITY)
                .setParameter(Transaction.PARAMETER_POST_ON_DOCUMENT_SIGNED, "true")
                .build();

        submitTransaction(transaction);
    }

    @Override
    public void createTransactionCommunity2User(Long userId, Long communityId, Long recipientUserId, BigDecimal amount, String decription) {
        AccountTypeEntity userAccountType = accountDataService.getPrimaryAccountType(Discriminators.SHARER);
        Assert.notNull(userAccountType);

        AccountTypeEntity communityAccountType = accountDataService.getPrimaryAccountType(Discriminators.COMMUNITY);
        Assert.notNull(communityAccountType);

        Account recipientUserAccount = getUserAccount(recipientUserId, userAccountType.getId());
        Assert.notNull(recipientUserAccount);

        Account communityAccount = getCommunityAccount(communityId, communityAccountType.getId());
        Assert.notNull(communityAccount);

        List<FlowOfDocumentStateEvent> stateEvents = Collections.singletonList(new FlowOfDocumentStateEvent(this, null, FlowOfDocumentStateEventType.DOCUMENT_SIGNED));
        DocumentEntity document = documentService.createCommunityToSharerMoveDocument(communityRepository.findOne(communityId), userRepository.findOne(recipientUserId), amount, stateEvents, userId);

        DocumentFolderEntity documentFolderEntity = new DocumentFolderEntity();
        documentFolderEntity.setName("community2user");
        documentFolderEntity = documentFolderRepository.save(documentFolderEntity);

        document.setFolder(documentFolderEntity);
        document = documentRepository.save(document);

        documentFolderEntity.getDocuments().add(document);
        documentFolderEntity = documentFolderRepository.save(documentFolderEntity);

        TransactionDetail debitDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.DEBIT)
                .setAmount(amount)
                .setAccountId(communityAccount.getId())
                .build();

        TransactionDetail creditDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.CREDIT)
                .setAmount(amount)
                .setAccountId(recipientUserAccount.getId())
                .build();

        Transaction transaction = new TransactionBuilder()
                .setAmount(amount)
                .setDescription(decription)
                .addDetail(debitDetail)
                .addDetail(creditDetail)
                .setDocumentFolder(documentFolderEntity.toDomain(false, false))
                .setTransactionType(TransactionType.USER_COMMUNITY)
                .setParameter(Transaction.PARAMETER_POST_ON_DOCUMENT_SIGNED, "true")
                .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(userId))
                .build();

        submitTransaction(transaction);
    }

    @Override
    public void createTransactionCommunity2Community(Long userId, Long communityId, Long recipientCommunityId, BigDecimal amount, String decription) {
        AccountTypeEntity communityAccountType = accountDataService.getPrimaryAccountType(Discriminators.COMMUNITY);
        Assert.notNull(communityAccountType);

        Account communityAccount = getCommunityAccount(communityId, communityAccountType.getId());
        Assert.notNull(communityAccount);

        Account recipientCommunityAccount = getCommunityAccount(recipientCommunityId, communityAccountType.getId());
        Assert.notNull(recipientCommunityAccount);

        List<FlowOfDocumentStateEvent> stateEvents = Collections.singletonList(new FlowOfDocumentStateEvent(this, null, FlowOfDocumentStateEventType.DOCUMENT_SIGNED));
        DocumentEntity document = documentService.createCommunityToCommunityMoveDocument(communityRepository.findOne(communityId), communityRepository.findOne(recipientCommunityId), amount, stateEvents, userId);

        DocumentFolderEntity documentFolderEntity = new DocumentFolderEntity();
        documentFolderEntity.setName("community2community");
        documentFolderEntity = documentFolderRepository.save(documentFolderEntity);

        document.setFolder(documentFolderEntity);
        document = documentRepository.save(document);

        documentFolderEntity.getDocuments().add(document);
        documentFolderEntity = documentFolderRepository.save(documentFolderEntity);

        TransactionDetail debitDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.DEBIT)
                .setAmount(amount)
                .setAccountId(communityAccount.getId())
                .build();

        TransactionDetail creditDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.CREDIT)
                .setAmount(amount)
                .setAccountId(recipientCommunityAccount.getId())
                .build();

        Transaction transaction = new TransactionBuilder()
                .setAmount(amount)
                .setDescription(decription)
                .addDetail(debitDetail)
                .addDetail(creditDetail)
                .setDocumentFolder(documentFolderEntity.toDomain(false, false))
                .setTransactionType(TransactionType.COMMUNITY)
                .setParameter(Transaction.PARAMETER_POST_ON_DOCUMENT_SIGNED, "true")
                .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(userId))
                .build();

        submitTransaction(transaction);
    }

    @Override
    public Transaction createTransaction(Long userId, Long accountId, Long recipientAccountId, BigDecimal amount, String decription) {
        AccountEntity accountEntity = accountRepository.findOne(accountId);
        Assert.notNull(accountEntity);

        AccountEntity recipientAccountEntity = accountRepository.findOne(recipientAccountId);
        Assert.notNull(recipientAccountEntity);

        DocumentFolderEntity documentFolderEntity = new DocumentFolderEntity();
        documentFolderEntity.setName("transaction");
        documentFolderEntity = documentFolderRepository.save(documentFolderEntity);

        TransactionDetail debitDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.DEBIT)
                .setAmount(amount)
                .setAccountId(accountEntity.getId())
                .build();

        TransactionDetail creditDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.CREDIT)
                .setAmount(amount)
                .setAccountId(recipientAccountEntity.getId())
                .build();

        Transaction transaction = new TransactionBuilder()
                .setAmount(amount)
                .setDescription(decription)
                .addDetail(debitDetail)
                .addDetail(creditDetail)
                .setDocumentFolder(documentFolderEntity.toDomain(false, false))
                .setTransactionType(TransactionType.USER)
                .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(userId))
                .build();

        transaction = submitTransaction(transaction);
        //postTransaction(transaction); // TODO Подтверждать транзакцию нужно потом
        return transaction;
    }

    @Override
    public void createTransactionUser2User(Long userId, Long userAccountTypeId, Long recipientUserId, Long recipientUserAccountTypeId, BigDecimal amount, String decription) {
        Account userAccount = getUserAccount(userId, userAccountTypeId);
        Assert.notNull(userAccount);

        Account recipientUserAccount = getUserAccount(recipientUserId, recipientUserAccountTypeId);
        Assert.notNull(recipientUserAccount);

        DocumentFolderEntity documentFolderEntity = new DocumentFolderEntity();
        documentFolderEntity.setName("community2community");
        documentFolderEntity = documentFolderRepository.save(documentFolderEntity);

        TransactionDetail debitDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.DEBIT)
                .setAmount(amount)
                .setAccountId(userAccount.getId())
                .build();

        TransactionDetail creditDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.CREDIT)
                .setAmount(amount)
                .setAccountId(recipientUserAccount.getId())
                .build();

        Transaction transaction = new TransactionBuilder()
                .setAmount(amount)
                .setDescription(decription)
                .addDetail(debitDetail)
                .addDetail(creditDetail)
                .setDocumentFolder(documentFolderEntity.toDomain(false, false))
                .setTransactionType(TransactionType.USER)
                .setParameter(Transaction.PARAMETER_USER_ID, String.valueOf(userId))
                .build();

        transaction = submitTransaction(transaction);
        postTransaction(transaction.getId());
    }

    @Override
    public Transaction createTransactionSharebook2Account(Long sharebookId, Long recipientAccountId, BigDecimal amount, String decription) {
        return createTransactionSharebook2Account(sharebookId, recipientAccountId, amount, decription, true);
    }

    @Override
    public Transaction createTransactionSharebook2Account(Long sharebookId, Long recipientAccountId, BigDecimal amount, String decription, boolean checkMinAmount) {
        SharebookEntity sharebook = sharebookRepository.findOne(sharebookId);
        Assert.notNull(sharebook);

        AccountEntity sharebookAccount = sharebook.getAccount();
        Assert.notNull(sharebookAccount);

        AccountEntity recipientAccount = accountRepository.findOne(recipientAccountId);
        Assert.notNull(recipientAccount);

        if (checkMinAmount) {
            AccountEntity accountEntity = accountRepository.findOne(sharebookAccount.getId());

            SharebookEntity sharebookEntity = (SharebookEntity) accountEntity.getOwner();
            Community community = communityDomainService.getByIdFullData(sharebookEntity.getCommunity().getId());
            Field field;

            if (sharebookEntity.getSharebookOwner() instanceof UserEntity) {
                field = community.getCommunityData().getFieldByInternalName(FieldConstants.MIN_SHARE_FEES_FIELD_NAME);
            } else {
                field = community.getCommunityData().getFieldByInternalName(FieldConstants.COMMUNITY_MIN_SHARE_FEES_FIELD_NAME);
            }

            BigDecimal minDeposit = new BigDecimal(FieldsService.getFieldStringValue(field));

            if (accountEntity.getBalance().subtract(amount).compareTo(minDeposit) < 0) {
                throw new TransactionException("На паевой книжке должны оставаться средства в размере не менее чем [" + minDeposit + " Ра] (указанный в ПО минимальный паевой взнос)");
            }
        }

        DocumentFolderEntity documentFolderEntity = new DocumentFolderEntity();
        documentFolderEntity.setName("sharebook2account");
        documentFolderEntity = documentFolderRepository.save(documentFolderEntity);

        TransactionDetail debitDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.DEBIT)
                .setAmount(amount)
                .setAccountId(sharebookAccount.getId())
                .build();

        TransactionDetail creditDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.CREDIT)
                .setAmount(amount)
                .setAccountId(recipientAccount.getId())
                .build();

        Transaction transaction = new TransactionBuilder()
                .setAmount(amount)
                .setDescription(decription)
                .addDetail(debitDetail)
                .addDetail(creditDetail)
                .setDocumentFolder(documentFolderEntity.toDomain(false, false))
                .setTransactionType(TransactionType.SHAREBOOK)
                .build();

        transaction = submitTransaction(transaction);
        return transaction;
    }

    @Override
    public Transaction createTransactionAccount2Sharebook(Long accountId, Long recipientSharebookId, BigDecimal amount, String decription) {
        AccountEntity account = accountRepository.findOne(accountId);
        Assert.notNull(account);

        SharebookEntity recipientSharebook = sharebookRepository.findOne(recipientSharebookId);
        Assert.notNull(recipientSharebook);

        AccountEntity recipientSharebookAccount = recipientSharebook.getAccount();
        Assert.notNull(recipientSharebookAccount);

        DocumentFolderEntity documentFolderEntity = new DocumentFolderEntity();
        documentFolderEntity.setName("account2sharebook");
        documentFolderEntity = documentFolderRepository.save(documentFolderEntity);

        TransactionDetail debitDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.DEBIT)
                .setAmount(amount)
                .setAccountId(account.getId())
                .build();

        TransactionDetail creditDetail = new TransactionDetailBuilder()
                .setType(TransactionDetailType.CREDIT)
                .setAmount(amount)
                .setAccountId(recipientSharebookAccount.getId())
                .build();

        Transaction transaction = new TransactionBuilder()
                .setAmount(amount)
                .setDescription(decription)
                .addDetail(debitDetail)
                .addDetail(creditDetail)
                .setDocumentFolder(documentFolderEntity.toDomain(false, false))
                .setTransactionType(TransactionType.SHAREBOOK)
                .build();

        transaction = submitTransaction(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> getTransactions(List<Long> accountIds, int page, int size, Date dateFrom, Date dateTo, Long accountTypeId) {
        List<Transaction> result = new ArrayList<>();
        if (accountIds != null && !accountIds.isEmpty()) {
            Specifications<TransactionEntity> specifications = buildSearchSpecification(dateFrom, dateTo, accountIds, accountTypeId, null, null);

            PageRequest pageRequest = new PageRequest(page, size, new Sort(new Sort.Order(Sort.Direction.DESC, "id")));
            Page<TransactionEntity> transactions = transactionRepository.findAll(specifications, pageRequest);
            for (TransactionEntity transaction : transactions) {
                result.add(transaction.toDomain(true, false));
            }
        }
        return result;
    }

    @Override
    public List<Long> getCommunityAccountIds(Long communityId) {
        return Arrays.asList(communityRepository.getAccountIds(communityId));
    }

    @Override
    public List<Transaction> getCommunityTransactions(Long communityId, int page, int size, Date dateFrom, Date dateTo, Long accountTypeId) {
        List<Long> accountIds = getCommunityAccountIds(communityId);
        return getTransactions(accountIds, page, size, dateFrom, dateTo, accountTypeId);
    }


    @Override
    public List<Transaction> getTransactions(Long userId, int page, int size, Date dateFrom, Date dateTo, Long accountTypeId) {
        List<Long> accountIds = Arrays.asList(userRepository.getUserAccountIds(userId));
        return getTransactions(accountIds, page, size, dateFrom, dateTo, accountTypeId);
    }

    @Override
    public Transaction getTransaction(Long transctionId) {
        TransactionEntity transactionEntity = transactionRepository.findOne(transctionId);
        return transactionEntity != null ? transactionEntity.toDomain(true, true) : null;
    }

    @Override
    public TransactionListDto searchTransactions(Long userId, int pageNum, int size, Date dateFrom, Date dateTo,
                                                          Long accountTypeId, TransactionPlainModel.Type type, TransactionState state) {
        List<Long> accountIds = new ArrayList<>(Arrays.asList(userRepository.getUserAccountIds(userId)));
        accountIds.addAll(Arrays.asList(userRepository.getUserSharebookAccountIds(userId)));

        Specifications<TransactionEntity> specifications = buildSearchSpecification(dateFrom, dateTo, accountIds, accountTypeId, type, state);
        PageRequest pageRequest = new PageRequest(pageNum, size, new Sort(new Sort.Order(Sort.Direction.DESC, "id")));

        Page<TransactionEntity> page = transactionRepository.findAll(specifications, pageRequest);

        TransactionListDto result = new TransactionListDto();
        result.transactions.addAll(page.getContent().stream().map(transaction -> getTransactionPlainModel(transaction, accountIds)).collect(Collectors.toList()));
        result.number = page.getNumber();
        result.numberOfElements = page.getNumberOfElements();
        result.totalElements = page.getTotalElements();

        return result;
    }

    private Specifications<TransactionEntity> buildSearchSpecification(Date fromDate, Date toDate, List<Long> accountIds, Long accountTypeId,
                                                                       TransactionPlainModel.Type type, TransactionState state) {
        Specifications<TransactionEntity> result = Specifications.where(TransactionSpecifications.typeIs(type, accountIds, accountTypeId));

        if (fromDate != null) result = result.and(TransactionSpecifications.submitDateFrom(fromDate));
        if (toDate != null) result = result.and(TransactionSpecifications.submitDateTo(toDate));
        if (state != null) result = result.and(TransactionSpecifications.stateIs(state));

        return result;
    }

    @Override
    public List<Account> getUserAccounts(Long userId) {
        return AccountEntity.toDomainList(accountRepository.getUserAccounts(userId));
    }

    @Override
    public Account getUserAccount(Long userId, Long typeId) {
        return accountDataService.getUserAccountEntity(userId, typeId).toDomain();
    }

    @Override
    public List<Account> getCommunityAccounts(Long communityId) {
        return AccountEntity.toDomainList(accountRepository.getCommunityAccounts(communityId));
    }

    @Override
    public Account getCommunityAccount(Long communityId, Long typeId) {
        return accountDataService.getCommunityAccountEntity(communityId, typeId).toDomain();
    }

    @Override
    public Transaction submitTransaction(Transaction transaction) {
        Assert.notNull(transaction);
        Assert.isNull(transaction.getId());
        Assert.isNull(transaction.getState());

        TransactionEntity transactionEntity = new TransactionEntity(transaction);
        transactionEntity.setDocumentFolder(documentFolderRepository.findOne(transaction.getDocumentFolder().getId()));

        for (TransactionDetail detail : transaction.getDetails()) {
            AccountEntity accountEntity = accountRepository.findOne(detail.getAccountId());

            // TODO переделать, проверять статус счетов (счет должен иметь статус активен/заморожен, при удалении юзера или организации счет замораживается)
            if (accountEntity.getOwner() instanceof CommunityEntity) {
                CommunityEntity communityEntity = (CommunityEntity) accountEntity.getOwner();
                Assert.isTrue(!communityEntity.isDeleted());
            } else if (accountEntity.getOwner() instanceof UserEntity) {
                UserEntity userEntity = (UserEntity) accountEntity.getOwner();
                Assert.isTrue(!userEntity.isDeleted());
            } else if (accountEntity.getOwner() instanceof SharebookEntity) {
                SharebookEntity sharebookEntity = (SharebookEntity) accountEntity.getOwner();
                Assert.isTrue(!sharebookEntity.getCommunity().isDeleted());

                if (sharebookEntity.getAccount().getOwner() instanceof CommunityEntity) {
                    CommunityEntity communityEntity = (CommunityEntity) sharebookEntity.getAccount().getOwner();
                    Assert.isTrue(!communityEntity.isDeleted());
                } else if (sharebookEntity.getAccount().getOwner() instanceof UserEntity) {
                    UserEntity userEntity = (UserEntity) sharebookEntity.getAccount().getOwner();
                    Assert.isTrue(!userEntity.isDeleted());
                }
            }

            if (detail.getType() == TransactionDetailType.DEBIT) {
                if (accountEntity.getBalance().subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
                    throw new TransactionException("На счету недостаточно средств.");
                }

                if (accountEntity.getOwner() instanceof CommunityEntity) {
                    String userId = transaction.getParameters().get(Transaction.PARAMETER_USER_ID);
                    Assert.notNull(userId);

                    if (!communitiesService.hasPermission((CommunityEntity) accountEntity.getOwner(), Long.valueOf(userId), CommunityPermissionUtils.TRANSFER_MONEY_PERMISSION)) {
                        throw new TransactionException("Переводить денежные средства со счёта объединения могут только его члены имеющие соответствующее разрешение.");
                    }
                }

                accountEntity.setBalance(accountEntity.getBalance().subtract(detail.getAmount()));
                accountEntity.setHoldBalance(accountEntity.getHoldBalance().add(detail.getAmount()));
                accountRepository.save(accountEntity);
            }

            TransactionDetailEntity detailEntity = new TransactionDetailEntity();
            detailEntity.setTransaction(transactionEntity);
            detailEntity.setType(detail.getType());
            detailEntity.setAmount(detail.getAmount());
            detailEntity.setAccount(accountEntity);

            transactionEntity.getDetails().add(detailEntity);
        }

        transactionEntity.setState(TransactionState.HOLD);
        transactionEntity = transactionRepository.save(transactionEntity);

        blagosferaEventPublisher.publishEvent(new TransactionEvent(this, transactionEntity.toDomain(true, true)));
        return transactionEntity.toDomain(true, true);
    }

    @Override
    public void postTransaction(Long transactionId) {
        Assert.notNull(transactionId);

        TransactionEntity transactionEntity = transactionRepository.findOne(transactionId);
        accountDataService.postTransaction(transactionEntity);
        blagosferaEventPublisher.publishEvent(new TransactionEvent(this, transactionEntity.toDomain(true, true)));
    }

    @Override
    public void postTransactions(List<Transaction> transactions) {
        transactions.forEach(transaction -> postTransaction(transaction.getId()));
    }

    @Override
    public void rejectTransaction(Long transactionId) {
        Assert.notNull(transactionId);

        TransactionEntity transactionEntity = transactionRepository.findOne(transactionId);
        accountDataService.rejectTransaction(transactionEntity);
        blagosferaEventPublisher.publishEvent(new TransactionEvent(this, transactionEntity.toDomain(true, true)));
    }

    @Override
    public void rejectTransactions(List<Transaction> transactions) {
        transactions.forEach(transaction -> rejectTransaction(transaction.getId()));
    }

    @EventListener
    public void onPaymentStatusEvent(PaymentStatusEvent paymentStatusEvent) {
        if (paymentStatusEvent.getPayment() instanceof AccountPayment) {
            AccountPayment payment = (AccountPayment) paymentStatusEvent.getPayment();
            PaymentStatus fromStatus = paymentStatusEvent.getFromStatus();
            PaymentStatus toStatus = paymentStatusEvent.getToStatus();

            if (fromStatus.equals(PaymentStatus.NEW) && toStatus.equals(PaymentStatus.PROCESSING)) {
                DocumentFolderEntity documentFolderEntity = new DocumentFolderEntity();
                documentFolderEntity.setName("payment");
                documentFolderEntity = documentFolderRepository.save(documentFolderEntity);

                TransactionDetail detail = new TransactionDetailBuilder()
                        .setType(TransactionDetailType.CREDIT)
                        .setAmount(payment.getRaAmount())
                        .setAccountId(payment.getAccount().getId())
                        .build();

                Transaction transaction = new TransactionBuilder()
                        .setAmount(payment.getRaAmount())
                        .setDescription(payment.getComment())
                        .addDetail(detail)
                        .setDocumentFolder(documentFolderEntity.toDomain(false, false))
                        .setTransactionType(TransactionType.PAYMENT_SYSTEM)
                        .setParameter(Transaction.PARAMETER_PAYMENT_ID, String.valueOf(payment.getId()))
                        .build();

                submitTransaction(transaction);
            } else if (fromStatus.equals(PaymentStatus.PROCESSING) && toStatus.equals(PaymentStatus.SUCCESS)) {
                TransactionEntity transactionEntity = transactionRepository.findByParameterValue(Transaction.PARAMETER_PAYMENT_ID, String.valueOf(payment.getId()));
                postTransaction(transactionEntity.getId());
            } else if (fromStatus.equals(PaymentStatus.PROCESSING) && toStatus.equals(PaymentStatus.FAIL)) {
                TransactionEntity transactionEntity = transactionRepository.findByParameterValue(Transaction.PARAMETER_PAYMENT_ID, String.valueOf(payment.getId()));
                rejectTransaction(transactionEntity.getId());
            }
        }
    }

    @EventListener
    public void onDocumentEvent(FlowOfDocumentStateEvent documentEvent) {
        if (documentEvent.getStateEventType() == FlowOfDocumentStateEventType.DOCUMENT_SIGNED) {
            DocumentFolderEntity documentFolderEntity = documentRepository.findOne(documentEvent.getDocument().getId()).getFolder();

            if (documentFolderEntity == null) return;

            TransactionEntity transactionEntity = transactionRepository.findOneByDocumentFolder_Id(documentFolderEntity.getId());

            if (transactionEntity == null) return;

            if (transactionEntity.getParameters().get(Transaction.PARAMETER_POST_ON_DOCUMENT_SIGNED) != null) {
                boolean allSigned = true;

                loop:
                for (DocumentEntity documentEntity : documentFolderEntity.getDocuments()) {
                    for (DocumentParticipantEntity documentParticipantEntity : documentEntity.getParticipants()) {
                        if (documentParticipantEntity.getIsNeedSignDocument() && !documentParticipantEntity.getIsSigned()) {
                            allSigned = false;
                            break loop;
                        }
                    }
                }

                if (allSigned) postTransaction(transactionEntity.getId());
            }
        }
    }

    private Long getSharebookOwnerId(Object owner) {
        Long ownerId = 0L;

        if (owner instanceof UserEntity) ownerId = ((UserEntity) owner).getId();
        if (owner instanceof CommunityEntity) ownerId = ((CommunityEntity) owner).getId();

        if (owner instanceof User) ownerId = ((User) owner).getId();
        if (owner instanceof Community) ownerId = ((Community) owner).getId();

        return ownerId;
    }

    private boolean isSharebookOwnerEntity(Object owner) {
        boolean isEntity = false;

        if (owner instanceof UserEntity) isEntity = true;
        if (owner instanceof CommunityEntity) isEntity = true;
        if (owner instanceof SystemAccountEntity) isEntity = true;

        return isEntity;
    }

    private Object getSharebookOwnerEntity(Object owner) {
        if (isSharebookOwnerEntity(owner)) {
            return owner;
        } else {
            if (owner instanceof User) {
                return userRepository.findOne(((User) owner).getId());
            } else if (owner instanceof Community) {
                return communityRepository.findOne(((Community) owner).getId());
            }
        }

        return null;
    }

    // Может ли быть у пользователя {sharer} счёт в ПО {community}
    private boolean canHaveBookAccount(Object shareBookOwner, Long communityId) {
        RameraListEditorItem poAssociationForm = rameraListEditorItemDAO.getByCode(Community.COOPERATIVE_SOCIETY_LIST_ITEM_CODE);
        RameraListEditorItem kuchAssociationForm = rameraListEditorItemDAO.getByCode(Community.COOPERATIVE_PLOT_ASSOCIATION_FORM_CODE);

        Community community = communityDomainService.getByIdMediumData(communityId);

        boolean result = false;

        // Если форма объединения - не ПО или не КУч ПО, то в нём нельзя иметь паевый счёт
        if (community.getAssociationForm() != null &&
                (community.getAssociationForm().getId().equals(poAssociationForm.getId()) ||
                        community.getAssociationForm().getId().equals(kuchAssociationForm.getId()))) {
            // Только член ПО может иметь паевую книжку

            Long sharebookOwnerId = getSharebookOwnerId(shareBookOwner);

            if (shareBookOwner instanceof UserEntity) {
                result = communityDomainService.isSharerMember(communityId, sharebookOwnerId);
            }

            if (shareBookOwner instanceof CommunityEntity) {
                List<Community> members = community.getCommunitiesMembers();

                for (Community member : members) {
                    if (member.getId().equals(sharebookOwnerId)) {
                        return true;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public TransactionPlainModel getTransactionPlainModel(TransactionEntity transactionEntity, List<Long> accountsIds) {
        TransactionPlainModel transactionPlainModel = new TransactionPlainModel();
        transactionPlainModel.setId(transactionEntity.getId());
        transactionPlainModel.setAmount(transactionEntity.getAmount());
        transactionPlainModel.setSubmitDate(transactionEntity.getSubmitDate() != null ? DateUtils.toDate(transactionEntity.getSubmitDate()) : null);
        transactionPlainModel.setPostDate(transactionEntity.getPostDate() != null ? DateUtils.toDate(transactionEntity.getPostDate()) : null);
        transactionPlainModel.setDescription(transactionEntity.getDescription());
        transactionPlainModel.setState(transactionEntity.getState());

        if (transactionEntity.getDetails().stream().filter(transactionDetailEntity ->
                accountsIds.contains(transactionDetailEntity.getAccount().getId())).
                collect(Collectors.toList()).size() == transactionEntity.getDetails().size()) {
            transactionPlainModel.setType(TransactionPlainModel.Type.LOCAL);
        } else if (transactionEntity.getDetails().stream().filter(transactionDetailEntity ->
                accountsIds.contains(transactionDetailEntity.getAccount().getId()) && (transactionDetailEntity.getType().equals(TransactionDetailType.DEBIT))).
                collect(Collectors.toList()).size() > 0) {
            transactionPlainModel.setType(TransactionPlainModel.Type.DEBIT);
        } else {
            transactionPlainModel.setType(TransactionPlainModel.Type.CREDIT);
        }

        for (TransactionDetailEntity transactionDetailEntity : transactionEntity.getDetails()) {
            if (((transactionPlainModel.getType().equals(TransactionPlainModel.Type.CREDIT)) &&
                    (!accountsIds.contains(transactionDetailEntity.getAccount().getId())) &&
                    (transactionDetailEntity.getType().equals(TransactionDetailType.DEBIT)))) {
                setTransactionSender(transactionDetailEntity, transactionPlainModel);
            } else if (!((transactionPlainModel.getType().equals(TransactionPlainModel.Type.CREDIT)) &&
                    (!accountsIds.contains(transactionDetailEntity.getAccount().getId())))) {
                TransactionDetailPlainModel transactionDetailPlainModel = new TransactionDetailPlainModel();
                transactionDetailPlainModel.setAccountType(transactionDetailEntity.getAccount().getType().toDomain());
                transactionDetailPlainModel.setAmount(transactionDetailEntity.getAmount());
                transactionDetailPlainModel.setType(transactionDetailEntity.getType());

                if (transactionDetailEntity.getAccount().getOwner() instanceof UserEntity) {
                    UserEntity userEntity = (UserEntity) transactionDetailEntity.getAccount().getOwner();
                    transactionDetailPlainModel.setUser(userEntity.toDomain());
                }

                if (transactionDetailEntity.getAccount().getOwner() instanceof CommunityEntity) {
                    CommunityEntity communityEntity = (CommunityEntity) transactionDetailEntity.getAccount().getOwner();
                    transactionDetailPlainModel.setCommunity(communityEntity.toDomain());
                }

                if (transactionDetailEntity.getAccount().getOwner() instanceof SharebookEntity) {
                    SharebookEntity sharebookEntity = (SharebookEntity) transactionDetailEntity.getAccount().getOwner();
                    if (sharebookEntity.getSharebookOwner() instanceof UserEntity) {
                        UserEntity shareBookUser = (UserEntity) sharebookEntity.getSharebookOwner();
                        transactionDetailPlainModel.setUser(shareBookUser.toDomain());
                    } else if (sharebookEntity.getSharebookOwner() instanceof CommunityEntity) {
                        CommunityEntity sharebookCommunity = (CommunityEntity) sharebookEntity.getSharebookOwner();
                        transactionDetailPlainModel.setCommunity(sharebookCommunity.toDomain());
                    }
                    transactionDetailPlainModel.setShareBookCommunity(sharebookEntity.getCommunity().toDomain());
                }

                if (transactionDetailEntity.getAccount().getOwner() instanceof SystemAccountEntity) {

                }

                if (accountsIds.contains(transactionDetailEntity.getAccount().getId())) {
                    transactionPlainModel.getMyDetails().add(transactionDetailPlainModel);
                } else {
                    transactionPlainModel.getOthersDetails().add(transactionDetailPlainModel);
                }
            }
        }

        if (transactionEntity.getParameters().get(Transaction.PARAMETER_PAYMENT_ID) != null) {
            Long paymentId = Long.parseLong(transactionEntity.getParameters().get(Transaction.PARAMETER_PAYMENT_ID));
            Payment payment = paymentDao.getById(paymentId);
            transactionPlainModel.setPayment(payment.toDomain());
        }

        return transactionPlainModel;
    }

    private void setTransactionSender(TransactionDetailEntity transactionDetail, TransactionPlainModel transactionPlainModel) {
        if (transactionDetail.getAccount().getOwner() instanceof UserEntity) {
            UserEntity userEntity = (UserEntity) transactionDetail.getAccount().getOwner();
            transactionPlainModel.setSenderUser(userEntity.toDomain());
        }

        if (transactionDetail.getAccount().getOwner() instanceof CommunityEntity) {
            CommunityEntity communityEntity = (CommunityEntity) transactionDetail.getAccount().getOwner();
            transactionPlainModel.setSenderCommunity(communityEntity.toDomain());
        }

        if (transactionDetail.getAccount().getOwner() instanceof SharebookEntity) {
            SharebookEntity sharebookEntity = (SharebookEntity) transactionDetail.getAccount().getOwner();

            if (sharebookEntity.getSharebookOwner() instanceof UserEntity) {
                UserEntity shareBookUser = (UserEntity) sharebookEntity.getSharebookOwner();
                transactionPlainModel.setSenderUser(shareBookUser.toDomain());
            } else if (sharebookEntity.getSharebookOwner() instanceof CommunityEntity) {
                CommunityEntity sharebookCommunity = (CommunityEntity) sharebookEntity.getSharebookOwner();
                transactionPlainModel.setSenderCommunity(sharebookCommunity.toDomain());
            }
        }

        if (transactionDetail.getAccount().getOwner() instanceof SystemAccountEntity) {

        }
    }

    @Override
    public Long getUserAccountsCounts(Long id) {
        List<Long> accountIds = Arrays.asList(userRepository.getUserAccountIds(id));
        Specifications<TransactionEntity> specifications = buildSearchSpecification(null, null, accountIds, null, null, null);
        return transactionRepository.count(specifications);
    }

    @Override
    public List<PaymentSystem> getPaymentSystems() {
        List<PaymentSystemEntity> entities = paymentSystemDao.getActiveList();
        return entities.stream().map(PaymentSystemEntity::toDomain).collect(Collectors.toList());
    }
}
