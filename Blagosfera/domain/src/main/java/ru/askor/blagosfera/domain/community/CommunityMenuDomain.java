package ru.askor.blagosfera.domain.community;

import lombok.Data;
import ru.askor.blagosfera.domain.account.Account;
import ru.askor.blagosfera.domain.account.Sharebook;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * Created by vgusev on 13.03.2016.
 */
@Data
public class CommunityMenuDomain implements Serializable {

    public static final long serialVersionUID = 1L;

    /**
     * Книжка пайщика (если объединение не КУч или не ПО то null)
     */
    private Sharebook sharerBookAccount;

    /**
     * Баланс пайщиков ПО или КУч
     */
    private BigDecimal communityBookAccountsBalance;

    /**
     * Счета объединения
     */
    private List<Account> accounts;

    /**
     * Разделы объединения
     */
    private List<CommunitySectionDomain> communitySections;

    /**
     * Текущий пользователь является директором объединения
     */
    private boolean isDirector;
}
