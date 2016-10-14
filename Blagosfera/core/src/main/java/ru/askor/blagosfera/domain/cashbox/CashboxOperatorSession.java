package ru.askor.blagosfera.domain.cashbox;

import ru.radom.kabinet.model.UserEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CashboxOperatorSession {

    private Long id;
    private UserEntity operator;
    private String workplaceId;
    private Date createdDate;
    private Date endDate;
    private boolean active;
    private CashboxWorkplace cashboxWorkplace;
    private Integer exchangesCount = null;
    private BigDecimal exchangesTotal = null;
    private List<Map<String, Object>> basketItems = new ArrayList<>();

    public CashboxOperatorSession() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getOperator() {
        return operator;
    }

    public void setOperator(UserEntity operator) {
        this.operator = operator;
    }

    public String getWorkplaceId() {
        return workplaceId;
    }

    public void setWorkplaceId(String workplaceId) {
        this.workplaceId = workplaceId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public CashboxWorkplace getWorkplace() {
        return cashboxWorkplace;
    }

    public CashboxOperatorSession setWorkplace(CashboxWorkplace cashboxWorkplace) {
        this.cashboxWorkplace = cashboxWorkplace;
        return this;
    }

    public Integer getExchangesCount() {
        return exchangesCount;
    }

    public void setExchangesCount(Integer exchangesCount) {
        this.exchangesCount = exchangesCount;
    }

    public BigDecimal getExchangesTotal() {
        return exchangesTotal;
    }

    public void setExchangesTotal(BigDecimal exchangesTotal) {
        this.exchangesTotal = exchangesTotal;
    }

    public List<Map<String, Object>> getBasketItems() {
        return basketItems;
    }
}
