package ru.radom.kabinet.web.communities.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.collections.map.HashedMap;
import ru.askor.blagosfera.core.util.DateUtils;
import ru.askor.blagosfera.domain.account.*;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.systemaccount.SystemAccount;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.json.BigDecimalSimpleSerializer;
import ru.radom.kabinet.json.FullDateSerializer;
import ru.radom.kabinet.model.Discriminators;

import java.math.BigDecimal;
import java.util.*;

/**
 *
 * Created by vgusev on 12.05.2016.
 */
public class CommunityTransactionDto {

    public Long id;

    public String description;

    public TransactionState state;

    @JsonSerialize(using = FullDateSerializer.class)
    public Date date;

    @JsonSerialize(using = BigDecimalSimpleSerializer.class)
    public BigDecimal amount;

    public String otherLink;

    public String otherIkp;

    public String otherName;

    public String accountOwnerType;

    public String otherSharebookType;

    public TransactionDetailType detailType;

    public CommunityTransactionDto(Transaction transaction, Map<Long, Account> accountMap, List<Long> selfAccounts) {
        id = transaction.getId();
        description = transaction.getDescription();
        state = transaction.getState();
        if (transaction.getSubmitDate() != null) {
            date = DateUtils.toDate(transaction.getSubmitDate());
        }
        amount = transaction.getAmount();
        if (transaction.getDetails() != null) {
            for (TransactionDetail transactionDetail : transaction.getDetails()) {
                Account account = null;
                if (accountMap != null) {
                    account = accountMap.get(transactionDetail.getAccountId());
                }
                if (account.getOwner() != null) {
                    String link = null;
                    String name = null;
                    String ikp = null;
                    String sharebookType = null;
                    if (!selfAccounts.contains(transactionDetail.getAccountId())) {
                        switch (account.getOwnerType()) {
                            case Discriminators.SHARER: {
                                link = ((User) account.getOwner()).getLink();
                                name = ((User) account.getOwner()).getName();
                                ikp = ((User) account.getOwner()).getIkp();
                                break;
                            }
                            case Discriminators.COMMUNITY: {
                                link = ((Community) account.getOwner()).getLink();
                                name = ((Community) account.getOwner()).getName();
                                break;
                            }
                            case Discriminators.SHARER_BOOK: {
                                Object sharebookOwner = ((Sharebook) account.getOwner()).getSharebookOwner();
                                String sharebookOwnerType = ((Sharebook) account.getOwner()).getSharebookOwnerType();
                                sharebookType = sharebookOwnerType;
                                if (Discriminators.SHARER.equals(sharebookOwnerType)) {
                                    link = ((User) sharebookOwner).getLink();
                                    name = ((User) sharebookOwner).getName();
                                    ikp = ((User) sharebookOwner).getIkp();
                                } else if (Discriminators.COMMUNITY.equals(sharebookOwnerType)) {
                                    link = ((Community) sharebookOwner).getLink();
                                    name = ((Community) sharebookOwner).getName();
                                }
                                break;
                            }
                            case Discriminators.SYSTEM_ACCOUNT: {
                                link = ((SystemAccount) account.getOwner()).getLink();
                                name = ((SystemAccount) account.getOwner()).getName();
                                break;
                            }
                        }

                        otherLink = link;
                        otherName = name;
                        otherIkp = ikp;
                        accountOwnerType = account.getOwnerType();
                        otherSharebookType = sharebookType;
                    } else {
                        detailType = transactionDetail.getType();
                    }
                }
            }
        }
    }

    public static List<CommunityTransactionDto> toListDto(List<Transaction> transactions, List<Account> accounts, List<Long> selfAccounts) {
        List<CommunityTransactionDto> result = null;
        if (transactions != null) {
            Map<Long, Account> accountMap = new HashMap<>();
            if (accounts != null) {
                for (Account account : accounts) {
                    accountMap.put(account.getId(), account);
                }
            }
            result = new ArrayList<>();
            for (Transaction transaction : transactions) {
                result.add(new CommunityTransactionDto(transaction, accountMap, selfAccounts));
            }
        }
        return result;
    }

}
