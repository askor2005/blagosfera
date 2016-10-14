package ru.radom.kabinet.web.admin.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.askor.blagosfera.domain.account.PaymentDomain;
import ru.askor.blagosfera.domain.account.TransactionState;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.json.TimeStampDateSerializer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vtarasenko on 25.04.2016.
 */
public class TransactionPlainModel {

    public enum Type {
        DEBIT, CREDIT, LOCAL
    }

    private Type type;
    private Long id;
    private BigDecimal amount;
    private String description;
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date submitDate;
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date postDate;
    private TransactionState state;
    private Community senderCommunity; // отправитель или сообщество или юзер если есть всегда один
    private User senderUser;
    private List<TransactionDetailPlainModel> myDetails = new ArrayList<>();
    private List<TransactionDetailPlainModel> othersDetails = new ArrayList<>();
    private PaymentDomain payment;

    public TransactionPlainModel() {
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public TransactionState getState() {
        return state;
    }

    public void setState(TransactionState state) {
        this.state = state;
    }

    public Community getSenderCommunity() {
        return senderCommunity;
    }

    public void setSenderCommunity(Community senderCommunity) {
        this.senderCommunity = senderCommunity;
    }

    public User getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(User senderUser) {
        this.senderUser = senderUser;
    }

    public List<TransactionDetailPlainModel> getMyDetails() {
        return myDetails;
    }

    public List<TransactionDetailPlainModel> getOthersDetails() {
        return othersDetails;
    }

    public PaymentDomain getPayment() {
        return payment;
    }

    public void setPayment(PaymentDomain payment) {
        this.payment = payment;
    }
}
