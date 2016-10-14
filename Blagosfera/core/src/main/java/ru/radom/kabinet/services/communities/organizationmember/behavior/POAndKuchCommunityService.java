package ru.radom.kabinet.services.communities.organizationmember.behavior;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.data.jpa.entities.account.AccountEntity;
import ru.askor.blagosfera.data.jpa.entities.account.AccountTypeEntity;
import ru.askor.blagosfera.data.jpa.entities.account.SharebookEntity;
import ru.askor.blagosfera.data.jpa.repositories.community.OrganizationCommunityMemberParameterRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.OrganizationCommunityMemberRepository;
import ru.askor.blagosfera.data.jpa.services.account.AccountDataService;
import ru.askor.blagosfera.domain.account.Account;
import ru.askor.blagosfera.domain.account.Transaction;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.account.AccountTypeDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.OrganizationCommunityMemberEntity;
import ru.radom.kabinet.model.communities.OrganizationCommunityMemberParameter;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.VarUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Общие методы для ПО и для КУч ПО при работе с участниками
 * Created by vgusev on 27.10.2015.
 */
@Service
@Transactional
public class POAndKuchCommunityService {

    // Параметр - транзакции с платежами для вступления в ПО
    private static final String ENTRANCE_FEES_TRANSACTIONS_PARAM_NAME = "ENTRANCE_FEES_TRANSACTIONS_PARAM_NAME";

    // Параметр - транзакции с минимальными членскими взносами в ПО
    private static final String MIN_FEES_TRANSACTIONS_PARAM_NAME = "MIN_FEES_TRANSACTIONS_PARAM_NAME";

    // Параметр - транзакции с суммой из паевой книжки при выходе из ПО
    private static final String ORGANIZATION_LEAVE_FROM_COMMUNITY_TRANSACTIONS_PARAM_NAME = "ORGANIZATION_LEAVE_FROM_COMMUNITY_TRANSACTIONS_PARAM_NAME";

    @Autowired
    private OrganizationCommunityMemberRepository organizationCommunityMemberRepository;

    @Autowired
    private OrganizationCommunityMemberParameterRepository organizationCommunityMemberParameterRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountDataService accountDataService;

    @Autowired
    private AccountTypeDao accountTypeDao;

    @Autowired
    private CommunityDataService communityDomainService;

    private static Gson gson = new Gson();

    /**
     * Проверка вступления кандидата в ПО
     * @param community
     */
    public void checkEntranceOrganizationToCooperative(Community community) {
        // Вступительный взнос

        Double entranceShareFees = VarUtils.getDouble(community.getCommunityData().getFieldValueByInternalName(FieldConstants.COMMUNITY_ENTRANCE_SHARE_FEES_FIELD_NAME), 0d);
        // Минимальный паевой взнос
        Double minShareFees = VarUtils.getDouble(community.getCommunityData().getFieldValueByInternalName(FieldConstants.COMMUNITY_MIN_SHARE_FEES_FIELD_NAME), 0d);

        // нулевые суммы взносов не возможны
        if (entranceShareFees == 0d || minShareFees == 0d) {
            throw new RuntimeException("ПО не принимает заявки на вступление от кандидатов, так как ещё не определилось с размерами взносов");
        }
    }

    // Создать платёж на основе обычных счетов
    
    private List<Long> createEntranceCooperativePayTransactions(Double paySum, UserEntity initiator, AccountEntity organizationAccount, AccountEntity communityAccount, String comment) {
        BigDecimal amount = new BigDecimal(paySum);
        List<Long> transactionList = new ArrayList<>();
        transactionList.add(accountService.createTransaction(initiator.getId(), organizationAccount.getId(), communityAccount.getId(), amount, comment).getId());
        return transactionList;
    }

    // Создать платёж на основе счета и паевого счета
    
    private List<Long> createEntranceCooperativePayTransactions(Double paySum, Account organizationAccount, SharebookEntity shareBook, String comment) {
        BigDecimal amount = new BigDecimal(paySum);
        List<Long> result = new ArrayList<>();
        result.add(accountService.createTransactionAccount2Sharebook(organizationAccount.getId(), shareBook.getId(), amount, comment).getId());
        return result;
    }

    // Создать платёж выхода из ПО
    
    private List<Long> createLeaveFromCooperativePayTransactions(AccountEntity organizationAccount, SharebookEntity shareBook, String comment) {
        BigDecimal amount = shareBook.getAccount().getBalance();
        List<Long> result = new ArrayList<>();
        result.add(accountService.createTransactionSharebook2Account(shareBook.getId(), organizationAccount.getId(), amount, comment).getId());
        return result;
    }

    // Создать транзакции для вступления в ПО
    
    public void createBlockedFeesInJoinToPO(Long memberId, User organizationDirector) {
        OrganizationCommunityMemberEntity organizationCommunityMember = organizationCommunityMemberRepository.findOne(memberId);

        // Получить баланс пайщика, получить значение суммы для вступления
        // Если денег не хватает, то кидаем исключение
        // При сохранении транзакции баланс проверяется, так что проверка не нужна

        AccountTypeEntity communityAccountType = accountTypeDao.getDefaultAccountType(CommunityEntity.class);

        CommunityEntity communityEntity = organizationCommunityMember.getCommunity();
        CommunityEntity organization = organizationCommunityMember.getOrganization();

        Community community = communityDomainService.getByIdFullData(communityEntity.getId());

        // Проверяем возможность вступления кандидата в ПО
        checkEntranceOrganizationToCooperative(community);

        AccountEntity communityAccount = accountDataService.getCommunityAccountEntity(communityEntity.getId(), communityAccountType.getId());
        AccountEntity organizationAccount = accountDataService.getCommunityAccountEntity(organization.getId(), communityAccountType.getId());
        SharebookEntity shareBook = accountService.getSharebook(organization, communityEntity.getId());

        // Создаются транзакции с блокируемой суммой обеспечения для вступления в ПО
        // После того, как приняли юр лицо в пайщики, обновляем время создания транзакций и привязываем документ

        // Вступительный взнос
        Double entranceShareFees = VarUtils.getDouble(community.getCommunityData().getFieldValueByInternalName(FieldConstants.COMMUNITY_ENTRANCE_SHARE_FEES_FIELD_NAME), 0d);
        // Минимальный паевой взнос
        Double minShareFees = VarUtils.getDouble(community.getCommunityData().getFieldValueByInternalName(FieldConstants.COMMUNITY_MIN_SHARE_FEES_FIELD_NAME), 0d);

        // Вступительный взнос
        List<Long> entranceFeesTransactions = createEntranceCooperativePayTransactions(entranceShareFees, organization.getCreator(), organizationAccount, communityAccount, "Вступительный взнос");
        // Минимальный паевой взнос
        // Пай переводится на паевую книжку
        List<Long> minFeesTransactions = createEntranceCooperativePayTransactions(minShareFees, organizationAccount.toDomain(), shareBook, "Минимальный паевой взнос");

        String entranceFeesTransactionsJson = gson.toJson(entranceFeesTransactions);
        String minFeesTransactionsJson = gson.toJson(minFeesTransactions);

        // Сохраняем ИДы транзакций в параметры участника юр лица
        // ИДы транзакций - вступительный взнос
        OrganizationCommunityMemberParameter organizationCommunityMemberParameter = new OrganizationCommunityMemberParameter();
        organizationCommunityMemberParameter.setOrganizationCommunityMember(organizationCommunityMember);
        organizationCommunityMemberParameter.setParamName(ENTRANCE_FEES_TRANSACTIONS_PARAM_NAME);
        organizationCommunityMemberParameter.setParamValue(entranceFeesTransactionsJson);
        organizationCommunityMemberParameterRepository.save(organizationCommunityMemberParameter);
        // ИДы транзакций - минимальный паевой взнос
        organizationCommunityMemberParameter = new OrganizationCommunityMemberParameter();
        organizationCommunityMemberParameter.setOrganizationCommunityMember(organizationCommunityMember);
        organizationCommunityMemberParameter.setParamName(MIN_FEES_TRANSACTIONS_PARAM_NAME);
        organizationCommunityMemberParameter.setParamValue(minFeesTransactionsJson);
        organizationCommunityMemberParameterRepository.save(organizationCommunityMemberParameter);
    }

    // Создать транзакции для выхода из ПО
    
    public void createBlockedFeesInLeaveFromPO(Long memberId) {
        OrganizationCommunityMemberEntity organizationCommunityMember = organizationCommunityMemberRepository.findOne(memberId);

        // Получить баланс с паевой книжки, создать транзакции по возврату средств на счет организации
        AccountTypeEntity communityAccountType = accountTypeDao.getDefaultAccountType(CommunityEntity.class);

        CommunityEntity community = organizationCommunityMember.getCommunity();
        CommunityEntity organization = organizationCommunityMember.getOrganization();

        AccountEntity organizationAccount = accountDataService.getCommunityAccountEntity(organization.getId(), communityAccountType.getId());
        SharebookEntity shareBook = accountService.getSharebook(organization, community.getId());

        // Вывод средств из ПО
        List<Long> transactionIds = createLeaveFromCooperativePayTransactions(organizationAccount, shareBook, "Выдача паевых накоплений при выходе из Потребительского Общества");

        String leaveFeesTransactionsJson = gson.toJson(transactionIds);

        // Сохраняем ИДы транзакций в параметры участника юр лица
        // ИДы транзакций - накопления на паевой книжке
        OrganizationCommunityMemberParameter organizationCommunityMemberParameter = new OrganizationCommunityMemberParameter();
        organizationCommunityMemberParameter.setOrganizationCommunityMember(organizationCommunityMember);
        organizationCommunityMemberParameter.setParamName(ORGANIZATION_LEAVE_FROM_COMMUNITY_TRANSACTIONS_PARAM_NAME);
        organizationCommunityMemberParameter.setParamValue(leaveFeesTransactionsJson);
        organizationCommunityMemberParameterRepository.save(organizationCommunityMemberParameter);
    }

    // Вернуть блокированные средства, которые были отправлены при вступлении в ПО при отмене вступления в ПО
    
    public void cancelAllBlockedFeesInJoinToPO(Long memberId) {
        OrganizationCommunityMemberEntity organizationCommunityMember = organizationCommunityMemberRepository.findOne(memberId);
        List<OrganizationCommunityMemberParameter> parameters = organizationCommunityMember.getOrganizationCommunityMemberParameters();
        Type listType = new TypeToken<ArrayList<Long>>() {}.getType();

        for (OrganizationCommunityMemberParameter parameter : parameters) {
            if (ENTRANCE_FEES_TRANSACTIONS_PARAM_NAME.equals(parameter.getParamName()) || MIN_FEES_TRANSACTIONS_PARAM_NAME.equals(parameter.getParamName())) {
                List<Long> transactionIds = gson.fromJson(parameter.getParamValue(), listType);
                List<Transaction> transactions = new ArrayList<>();
                for (Long transactionId : transactionIds) {
                    Transaction transaction = accountDataService.getTransaction(transactionId);
                    transactions.add(transaction);
                }
                // отменяем проводки
                accountService.rejectTransactions(transactions);
                // Удаляем параметры с проводками
                organizationCommunityMemberParameterRepository.delete(parameter);
            }
        }
    }

    // Подтвердить блокированные средства, которые были отправлены при вступлении в ПО при принятии в ПО
    
    public void acceptAllBlockedFeesInJoinToPO(Long memberId) {
        OrganizationCommunityMemberEntity organizationCommunityMember = organizationCommunityMemberRepository.findOne(memberId);
        List<OrganizationCommunityMemberParameter> parameters = organizationCommunityMember.getOrganizationCommunityMemberParameters();
        Type listType = new TypeToken<ArrayList<Long>>() {}.getType();

        for (OrganizationCommunityMemberParameter parameter : parameters) {
            if (ENTRANCE_FEES_TRANSACTIONS_PARAM_NAME.equals(parameter.getParamName()) || MIN_FEES_TRANSACTIONS_PARAM_NAME.equals(parameter.getParamName())) {
                List<Long> transactionIds = gson.fromJson(parameter.getParamValue(), listType);
                List<Transaction> transactions = new ArrayList<>();
                for (Long transactionId : transactionIds) {
                    Transaction transaction = accountDataService.getTransaction(transactionId);
                    transactions.add(transaction);
                }
                // Завершаем проводки
                accountService.postTransactions(transactions);
                // Удаляем параметры с проводками
                organizationCommunityMemberParameterRepository.delete(parameter);
            }
        }
    }

    // Вернуть блокированные средства, которые были отправлены при выходе из ПО при отмене выхода из ПО
    
    public void cancelAllBlockedFeesInLeaveFromPO(Long memberId) {
        OrganizationCommunityMemberEntity organizationCommunityMember = organizationCommunityMemberRepository.findOne(memberId);
        List<OrganizationCommunityMemberParameter> parameters = organizationCommunityMember.getOrganizationCommunityMemberParameters();
        Type listType = new TypeToken<ArrayList<Long>>() {}.getType();

        for (OrganizationCommunityMemberParameter parameter : parameters) {
            if (ORGANIZATION_LEAVE_FROM_COMMUNITY_TRANSACTIONS_PARAM_NAME.equals(parameter.getParamName())) {
                List<Long> transactionIds = gson.fromJson(parameter.getParamValue(), listType);
                List<Transaction> transactions = new ArrayList<>();
                for (Long transactionId : transactionIds) {
                    Transaction transaction = accountDataService.getTransaction(transactionId);
                    transactions.add(transaction);
                }
                // отменяем проводки
                accountService.rejectTransactions(transactions);
                // Удаляем параметры с проводками
                organizationCommunityMemberParameterRepository.delete(parameter);
            }
        }
    }

    // Подтвердить блокированные средства, которые были отправлены при выходе из ПО
    
    public void acceptAllBlockedFeesInLeaveFromPO(Long memberId) {
        OrganizationCommunityMemberEntity organizationCommunityMember = organizationCommunityMemberRepository.findOne(memberId);
        List<OrganizationCommunityMemberParameter> parameters = organizationCommunityMember.getOrganizationCommunityMemberParameters();
        Type listType = new TypeToken<ArrayList<Long>>() {}.getType();

        for (OrganizationCommunityMemberParameter parameter : parameters) {
            if (ORGANIZATION_LEAVE_FROM_COMMUNITY_TRANSACTIONS_PARAM_NAME.equals(parameter.getParamName())) {
                List<Long> transactionIds = gson.fromJson(parameter.getParamValue(), listType);
                List<Transaction> transactions = new ArrayList<>();
                for (Long transactionId : transactionIds) {
                    Transaction transaction = accountDataService.getTransaction(transactionId);
                    transactions.add(transaction);
                }
                // Завершаем проводки
                accountService.postTransactions(transactions);

                // Удаляем параметры с проводками
                organizationCommunityMemberParameterRepository.delete(parameter);
            }
        }
    }
}
