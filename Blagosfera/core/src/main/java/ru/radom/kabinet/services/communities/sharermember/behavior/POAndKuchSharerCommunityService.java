package ru.radom.kabinet.services.communities.sharermember.behavior;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.data.jpa.entities.account.AccountEntity;
import ru.askor.blagosfera.data.jpa.entities.account.AccountTypeEntity;
import ru.askor.blagosfera.data.jpa.entities.account.SharebookEntity;
import ru.askor.blagosfera.data.jpa.services.account.AccountDataService;
import ru.askor.blagosfera.domain.account.Account;
import ru.askor.blagosfera.domain.account.Transaction;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.community.CommunityCooperativeLeaveEvent;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.account.AccountTypeDao;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.sharer.UserSettingsService;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.VarUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vgusev on 30.10.2015.
 */
@Service
@Transactional
public class POAndKuchSharerCommunityService {

    @Autowired
    private AccountTypeDao accountTypeDao;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountDataService accountDataService;

    /*@Autowired
    private CommunityMemberDao communityMemberDao;*/

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    // Префикс ключа настройки - ИД документа заявления вступления в ПО
    private static final String ENTRANCE_DOCUMENT_SHARER_TO_COMMUNITY_SETTING_KEY_PREFIX = "entranceSharerDocument";

    // Префикс настройки пользователя - ИД странзакций, которые создаются при взносе в ступлении в ПО
    private static final String SHARER_TO_COMMUNITY_PAY_TRANSACTIONS_KEY_PREFIX = "SHARER_TO_COMMUNITY_PAY_TRANSACTIONS_KEY_PREFIX";

    // Префикс ключа настройки - ИД документа заявления вступления в ПО
    private static final String LEAVE_STATEMENT_DOCUMENT_SHARER_FROM_COMMUNITY_SETTING_KEY_PREFIX = "leaveSharerFromCommDocument";

    // Префикс настройки пользователя - ИД странзакций, которые создаются при выходе из ПО
    private static final String SHARER_LEAVE_FROM_COMMUNITY_PAY_TRANSACTIONS_KEY_PREFIX = "SHARER_LEAVE_FROM_COMMUNITY_PAY_";

    //------------------------------------------------------------------------------------------

    /**
     * Получить ссылку на заявление о выходе пайщика из ПО
     *
     * @param member
     * @return
     */
    public String getDocumentForLeaveSharerFromCooperativeLink(CommunityMember member) {
        String documentId = userSettingsService.get(member.getUser(), getLeaveDocumentIdSettingsKey(member.getCommunity().getId()));
        return DocumentEntity.getLink(documentId);
    }

    /**
     * Получить ссылку на заявление о вступлении пайщика в ПО
     *
     * @param member
     * @return
     */
    public String getDocumentForEntranceSharerToCooperativeLink(CommunityMember member) {
        String documentId = userSettingsService.get(member.getUser(), getEntranceDocumentIdSettingKey(member.getCommunity().getId()));
        return DocumentEntity.getLink(documentId);
    }

    /**
     * Сумма вступительного платежа в ПО
     *
     * @param community
     * @return
     */
    public Double getEntranceSharerToCooperativeAmount(Community community) {
        // Вступительный взнос
        Double entranceShareFees = VarUtils.getDouble(community.getCommunityData().getFieldValueByInternalName(FieldConstants.ENTRANCE_SHARE_FEES_FIELD_NAME), 0d);
        // Минимальный паевой взнос
        Double minShareFees = VarUtils.getDouble(community.getCommunityData().getFieldValueByInternalName(FieldConstants.MIN_SHARE_FEES_FIELD_NAME), 0d);

        return entranceShareFees + minShareFees;
    }

    /**
     * Проверка вступления кандидата в ПО
     *
     * @param community
     */
    public void checkEntranceSharerToCooperative(Community community) {
        // Вступительный взнос
        Double entranceShareFees = VarUtils.getDouble(community.getCommunityData().getFieldValueByInternalName(FieldConstants.ENTRANCE_SHARE_FEES_FIELD_NAME), 0d);
        // Минимальный паевой взнос
        Double minShareFees = VarUtils.getDouble(community.getCommunityData().getFieldValueByInternalName(FieldConstants.MIN_SHARE_FEES_FIELD_NAME), 0d);

        // нулевые суммы взносов не возможны
        if (entranceShareFees == 0d || minShareFees == 0d) {
            throw new RuntimeException("ПО не принимает заявки на вступление от кандидатов, так как ещё не определилось с размерами взносов");
        }
    }

    // Ключ - ИД документа на заявление вступления в ПО и в КУч
    public static String getEntranceDocumentIdSettingKey(Long communityId) {
        return ENTRANCE_DOCUMENT_SHARER_TO_COMMUNITY_SETTING_KEY_PREFIX + "_" + communityId;
    }

    // Ключ параметра - транзакции участника
    private static String getTransactionsSettingsKey(Long communityId) {
        return SHARER_TO_COMMUNITY_PAY_TRANSACTIONS_KEY_PREFIX + "_" + communityId;
    }

    // Создать платёж на основе обычных счетов
    private JSONObject createEntranceCooperativePayTransactions(Double paySum, User user, Account sharerAccount, Account communityAccount, String comment) {
        BigDecimal amount = new BigDecimal(paySum);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", amount);
        JSONArray transactionJsonArray = new JSONArray();
        //accountService.submitTransaction()
        transactionJsonArray.put(accountService.createTransaction(user.getId(), sharerAccount.getId(), communityAccount.getId(), amount, comment).getId());
        jsonObject.put("transactions", transactionJsonArray);
        return jsonObject;
    }

    // Создать платёж на основе счета и паевого счета
    private JSONObject createEntranceCooperativePayTransactions(Double paySum, Account sharerAccount, SharebookEntity shareBook, String comment) {
        BigDecimal amount = new BigDecimal(paySum);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", amount);
        JSONArray transactionJsonArray = new JSONArray();
        transactionJsonArray.put(accountService.createTransactionAccount2Sharebook(sharerAccount.getId(), shareBook.getId(), amount, comment).getId());
        jsonObject.put("transactions", transactionJsonArray);
        return jsonObject;
    }

    // Создать платёж выхода из ПО
    private JSONObject createLeaveFromCooperativePayTransactions(AccountEntity sharerAccount, SharebookEntity shareBook, String comment) {
        BigDecimal amount = shareBook.getAccount().getBalance();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", amount);
        JSONArray transactionJsonArray = new JSONArray();
        transactionJsonArray.put(accountService.createTransactionSharebook2Account(shareBook.getId(), sharerAccount.getId(), amount, comment, false).getId());
        jsonObject.put("transactions", transactionJsonArray);
        return jsonObject;
    }

    // Создать транзакции перевода средств для входа в ПО
    public void createJoinFees(CommunityMember member) {
        // Произвести списание средств в объединение, по окончанию списания - перевести участника в статус CONDITION_DONE

        // Получить баланс пайщика, получить значение суммы для вступления
        // Еслии денег не хватает, то кидаем исключение
        // При сохранении транзакции баланс проверяется, так что проверка не нужна

        AccountTypeEntity communityAccountType = accountTypeDao.getDefaultAccountType(CommunityEntity.class);
        AccountTypeEntity sharerAccountType = accountTypeDao.getDefaultAccountType(UserEntity.class);

        Community community = communityDomainService.getByIdFullData(member.getCommunity().getId());
        //community.getMembers().add(member);
        User user = member.getUser();

        // Проверяем возможность вступления кандидата в ПО
        checkEntranceSharerToCooperative(community);

        AccountEntity communityAccount = accountDataService.getCommunityAccountEntity(community.getId(), communityAccountType.getId());
        AccountEntity sharerAccount = accountDataService.getUserAccountEntity(user.getId(), sharerAccountType.getId());
        SharebookEntity shareBook = accountService.getSharebook(user, community.getId());

        // Создаются транзакции с блокируемой суммой обеспечения для вступление в ПО
        // После того, как приняли физ лицо в пайщики, обновляем время создания транзакций и привязываем докумект

        // Вступительный взнос
        Double entranceShareFees = VarUtils.getDouble(community.getCommunityData().getFieldValueByInternalName(FieldConstants.ENTRANCE_SHARE_FEES_FIELD_NAME), 0d);
        // Минимальный паевой взнос
        Double minShareFees = VarUtils.getDouble(community.getCommunityData().getFieldValueByInternalName(FieldConstants.MIN_SHARE_FEES_FIELD_NAME), 0d);

        // Ищем значения полей - платежи в ПО
        JSONArray transactionsJsonArray = new JSONArray();

        // Вступительный взнос
        transactionsJsonArray.put(createEntranceCooperativePayTransactions(entranceShareFees, user, sharerAccount.toDomain(), communityAccount.toDomain(), "Вступительный взнос"));
        // Минимальный паевой взнос
        // Пай переводится на паевую книжку
        transactionsJsonArray.put(createEntranceCooperativePayTransactions(minShareFees, sharerAccount.toDomain(), shareBook, "Минимальный паевой взнос"));

        // Запоминаем в настройках пользователя ИД транзакций
        userSettingsService.set(user, getTransactionsSettingsKey(community.getId()), transactionsJsonArray.toString());
    }

    // Создать транзакции перевода средств для выхода из ПО
    public void createLeaveFees(CommunityMember member) {
        // Произвести блокировку средств с книжки пайщика в ЛС пользователя в системе
        // Выставить статус пайщика как "Выполнен запрос на выход из объединения"
        // Пушнуть событие - участник хочет выйти из ПО

        AccountTypeEntity sharerAccountType = accountTypeDao.getDefaultAccountType(UserEntity.class);
        SharebookEntity sharebook = accountService.getSharebook(member.getUser(), member.getCommunity().getId());

        if (sharebook.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            AccountEntity sharerAccount = accountDataService.getUserAccountEntity(member.getUser().getId(), sharerAccountType.getId());

            // Создаём транзакцию с блокированными средствами по выходу пайщика из ПО
            JSONObject transactionObject = createLeaveFromCooperativePayTransactions(sharerAccount, sharebook, "Выдача паевых накоплений при выходе из Потребительского Общества");

            // Запоминаем в настройках пользователя ИД транзакции
            userSettingsService.set(member.getUser(), getLeaveFromCommunityTransactionsSettingsKey(member.getCommunity().getId()), transactionObject.toString());
        }
    }

    /**
     * Установить статус транзакций - перевода средств для вступления в ПО или КУч и удалить информацию о них из настроек участника
     *
     * @param member
     * @param post
     */
    public void setStatusJoinToCommunityTransactionsAndClearFromSettings(CommunityMember member, boolean post) {
        // Получить транзакции из настроек участника
        // Очистить настройки участника
        // Обновить даты создания транзакций

        String key = getTransactionsSettingsKey(member.getCommunity().getId());

        String transactionJson = userSettingsService.get(member.getUser(), key);
        // Коммент - участник может отменить запрос на вступление до то того как заблокируются средства
        //SharerCommunityMemberService.check(transactionJsonSharerSetting == null, "Не сохранены в настройках блокированные транзакции.");
        if (transactionJson != null) {
            //String transactionJson = transactionJsonSharerSetting.getValue();

            if (!transactionJson.equals("")) {
                JSONArray transactionsJsonArray = new JSONArray(transactionJson);
                for (int i = 0; i < transactionsJsonArray.length(); i++) {
                    JSONObject row = transactionsJsonArray.getJSONObject(i);

                    JSONArray jsonArray = row.getJSONArray("transactions");

                    List<Transaction> transactions = new ArrayList<>();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        Long transactionId = jsonArray.getLong(j);
                        Transaction transaction = accountDataService.getTransaction(transactionId);
                        transactions.add(transaction);
                    }

                    // Завершаем проводки
                    if (post) {
                        accountService.postTransactions(transactions);
                    } else {
                        accountService.rejectTransactions(transactions);
                    }
                }
            }

            // Удаляем настройки с блокированными транзакциями
            userSettingsService.delete(member.getUser(), key);
            // Удаляем настройки с ссылками на заявления
            /*SharerSetting documentIdSharerSetting = sharerSettingDao.getByKey(getEntranceDocumentIdSettingKey(member.getCommunity().getId()),);
            sharerSettingDao.delete(documentIdSharerSetting);*/
            userSettingsService.delete(member.getUser(), getEntranceDocumentIdSettingKey(member.getCommunity().getId()));
        }
    }

    // Подтверждаем заблокированные проводки средств пайшика в ПО
    public void finishJoinSharerToCommunityTransactions(CommunityMember member) {
        setStatusJoinToCommunityTransactionsAndClearFromSettings(member, true);
    }

    // Вернуть блокированные средства, которые были отправлены при вступлении в ПО или КУч при отмене вступления в объединения
    public void cancelAllBlockedFeesInJoinToCommunity(CommunityMember member) {
        // Те же самые действия, что и при подтверждении снятия средств, только статус транзакций - отменено
        setStatusJoinToCommunityTransactionsAndClearFromSettings(member, false);
    }

    /**
     * Установить статус транзакций - перевода средств для выхода из ПО или КУч и удалить информацию о них из настроек участника
     *
     * @param member
     * @param post
     */
    public Double setStatusLeaveFromCommunityTransactionsAndClearFromSettings(CommunityMember member, boolean post) {
        // Получить транзакции из настроек участника
        // Очистить настройки участника
        // Обновить даты создания транзакций
        String key = getLeaveFromCommunityTransactionsSettingsKey(member.getCommunity().getId());

        // Сумма, которая вернулась участнику из книжки пайщика
        Double bookAccountAmount = 0d;

        String transactionJson = userSettingsService.get(member.getUser(), key);
        if (transactionJson != null) {
            //SharerCommunityMemberService.check(transactionJson == null, "Не сохранены в настройках блокированные транзакции.");
            if (!transactionJson.equals("")) {
                JSONObject transactionsJsonObject = new JSONObject(transactionJson);

                JSONArray jsonArray = transactionsJsonObject.getJSONArray("transactions");

                List<Transaction> transactions = new ArrayList<>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    Long transactionId = jsonArray.getLong(j);
                    Transaction transaction = accountDataService.getTransaction(transactionId);

                    if (transaction == null) continue;

                    if (transaction.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                        bookAccountAmount = transaction.getAmount().doubleValue();
                    }

                    transactions.add(transaction);
                }

                // Завершаем проводки
                if (transactions.size() > 0) {
                    if (post) {
                        accountService.postTransactions(transactions);
                    } else {
                        accountService.rejectTransactions(transactions);
                    }
                }
            }
        }

        // Удаляем настройки с блокированными транзакциями
        userSettingsService.delete(member.getUser(), key);
        // Удаляем настройки с ссылками на заявления
        /*SharerSetting documentIdSharerSetting = sharerSettingDao.getByKey(getLeaveDocumentIdSettingsKey(member.getCommunity().getId()), member.getUser());
        if (documentIdSharerSetting != null) {
        }*/
        userSettingsService.delete(member.getUser(), getLeaveDocumentIdSettingsKey(member.getCommunity().getId()));

        return bookAccountAmount;
    }

    // Подтверждаем заблокированные проводки средств бывшего пайшика в ПО
    public void finishLeaveSharerFromCommunityTransactions(CommunityMember member) {
        Double bookAccountAmount = setStatusLeaveFromCommunityTransactionsAndClearFromSettings(member, true);
        // Бывшему Пайщику приходит соответствующее уведомление и в систему и на почту с указанием суммы которая была ему выдана в качестве возврата пая.
        blagosferaEventPublisher.publishEvent(new CommunityCooperativeLeaveEvent(this, CommunityEventType.LEAVE_FROM_COOPERATIVE_IS_DONE, member.getCommunity(), bookAccountAmount, member.getUser()));
    }

    // Вернуть блокированные средства, которые были отправлены при выходе из ПО или КУч при отмене выхода из объединения
    public void cancelAllBlockedFeesInLeaveFromCommunity(CommunityMember member) {
        setStatusLeaveFromCommunityTransactionsAndClearFromSettings(member, false);
    }

    /**
     * Ключ настройки пользователя - ИД документа заявления о выходе из ПО или КУч
     *
     * @return
     */
    public static String getLeaveDocumentIdSettingsKey(Long communityId) {
        return LEAVE_STATEMENT_DOCUMENT_SHARER_FROM_COMMUNITY_SETTING_KEY_PREFIX + "_" + communityId;
    }

    // Ключ настройки пользователя - ИД транзакции выхода из ПО
    private static String getLeaveFromCommunityTransactionsSettingsKey(Long communityId) {
        return SHARER_LEAVE_FROM_COMMUNITY_PAY_TRANSACTIONS_KEY_PREFIX + "_" + communityId;
    }
}
