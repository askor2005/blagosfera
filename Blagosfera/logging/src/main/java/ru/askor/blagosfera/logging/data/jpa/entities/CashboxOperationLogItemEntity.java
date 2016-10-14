package ru.askor.blagosfera.logging.data.jpa.entities;

import org.hibernate.annotations.Type;
import ru.askor.blagosfera.domain.cashbox.CashboxOperationStatus;
import ru.askor.blagosfera.logging.domain.CashboxOperation;
import ru.askor.blagosfera.logging.domain.CashboxOperationLogItem;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cashbox_operations_log")
public class CashboxOperationLogItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cashbox_operations_log_id_generator")
    @SequenceGenerator(name = "cashbox_operations_log_id_generator", sequenceName = "cashbox_operations_log_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name="operation", nullable = false)
    @Enumerated(EnumType.STRING)
    private CashboxOperation operation;

    @Column(name="status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CashboxOperationStatus status;

    @Column(name = "operator_ikp", nullable = true)
    @Type(type="text")
    private String operatorIkp;

    @Column(name = "workplace_id", nullable = false)
    @Type(type="text")
    private String workplaceId;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "request_payload", nullable = true)
    @Type(type="text")
    private String requestPayload;

    @Column(name = "response_payload", nullable = true)
    @Type(type="text")
    private String responsePayload;

    @Column(name = "exception_message", nullable = true)
    @Type(type="text")
    private String exceptionMessage;

    @Column(name = "exception_stacktrace", nullable = true)
    @Type(type="text")
    private String exceptionStacktrace;

    public CashboxOperationLogItemEntity() {
    }

    public CashboxOperationLogItemEntity(CashboxOperationLogItem cashboxOperationLogItem) {
        id = cashboxOperationLogItem.getId();
        operation = cashboxOperationLogItem.getOperation();
        status = cashboxOperationLogItem.getStatus();
        operatorIkp = cashboxOperationLogItem.getOperatorIkp();
        workplaceId = cashboxOperationLogItem.getWorkplaceId();
        createdDate = cashboxOperationLogItem.getCreatedDate();
        requestPayload = cashboxOperationLogItem.getRequestPayload();
        responsePayload = cashboxOperationLogItem.getResponsePayload();
        exceptionMessage = cashboxOperationLogItem.getExceptionMessage();
        exceptionStacktrace = cashboxOperationLogItem.getExceptionStacktrace();
    }

    public CashboxOperationLogItem toDomain() {
        CashboxOperationLogItem cashboxOperationLogItem = new CashboxOperationLogItem();
        cashboxOperationLogItem.setId(getId());
        cashboxOperationLogItem.setOperation(getOperation());
        cashboxOperationLogItem.setStatus(getStatus());
        cashboxOperationLogItem.setOperatorIkp(getOperatorIkp());
        cashboxOperationLogItem.setWorkplaceId(getWorkplaceId());
        cashboxOperationLogItem.setCreatedDate(getCreatedDate());
        cashboxOperationLogItem.setRequestPayload(getRequestPayload());
        cashboxOperationLogItem.setResponsePayload(getResponsePayload());
        cashboxOperationLogItem.setExceptionMessage(getExceptionMessage());
        cashboxOperationLogItem.setExceptionStacktrace(getExceptionStacktrace());
        return cashboxOperationLogItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CashboxOperationLogItemEntity)) return false;

        CashboxOperationLogItemEntity that = (CashboxOperationLogItemEntity) o;

        //return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
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
