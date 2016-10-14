package ru.radom.kabinet.web.admin.dto;

import ru.askor.blagosfera.domain.account.AccountType;
import ru.askor.blagosfera.domain.account.TransactionDetailType;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.user.User;

import java.math.BigDecimal;

/**
 * Created by vtarasenko on 12.05.2016.
 */
public class TransactionDetailPlainModel {

    private AccountType accountType;
    private TransactionDetailType type;
    private BigDecimal amount;
    private User user;
    private Community community;
    private Community shareBookCommunity; //сообщество паевой книжки или юзера или сообщества

    public TransactionDetailPlainModel() {
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public TransactionDetailType getType() {
        return type;
    }

    public void setType(TransactionDetailType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Community getShareBookCommunity() {
        return shareBookCommunity;
    }

    public void setShareBookCommunity(Community shareBookCommunity) {
        this.shareBookCommunity = shareBookCommunity;
    }
}
