package ru.askor.blagosfera.logging.domain;

import ru.askor.blagosfera.domain.cashbox.CashboxOperationStatus;

import java.util.Date;

public class CashboxOperationLogItem {

    private Long id;
    private CashboxOperation operation;
    private CashboxOperationStatus status;
    private String operatorIkp;
    private String workplaceId;
    private Date createdDate;
    private String requestPayload;
    private String responsePayload;
    private String exceptionMessage;
    private String exceptionStacktrace;

    public CashboxOperationLogItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CashboxOperation getOperation() {
        return operation;
    }

    public void setOperation(CashboxOperation operation) {
        this.operation = operation;
    }

    public CashboxOperationStatus getStatus() {
        return status;
    }

    public void setStatus(CashboxOperationStatus status) {
        this.status = status;
    }

    public String getOperatorIkp() {
        return operatorIkp;
    }

    public void setOperatorIkp(String operatorIkp) {
        this.operatorIkp = operatorIkp;
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

    public String getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionStacktrace() {
        return exceptionStacktrace;
    }

    public void setExceptionStacktrace(String exceptionStacktrace) {
        this.exceptionStacktrace = exceptionStacktrace;
    }
}
