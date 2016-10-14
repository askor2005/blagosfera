package ru.askor.blagosfera.domain.cashbox;

import java.util.Date;

public class CashboxRegisterShareholder {

    private Long id;
    private String sharerIkp;
    private Long communityId;
    private Date requestCreatedDate;
    private Long requestDocumentId;
    private Date requestAcceptedDate;
    private Long acceptDocumentId;
    private CashboxOperatorSession requestOperatorSession;
    private CashboxOperatorSession acceptOperatorSession;

    public CashboxRegisterShareholder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSharerIkp() {
        return sharerIkp;
    }

    public void setSharerIkp(String sharerIkp) {
        this.sharerIkp = sharerIkp;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Date getRequestCreatedDate() {
        return requestCreatedDate;
    }

    public void setRequestCreatedDate(Date requestCreatedDate) {
        this.requestCreatedDate = requestCreatedDate;
    }

    public Long getRequestDocumentId() {
        return requestDocumentId;
    }

    public void setRequestDocumentId(Long requestDocumentId) {
        this.requestDocumentId = requestDocumentId;
    }

    public Date getRequestAcceptedDate() {
        return requestAcceptedDate;
    }

    public void setRequestAcceptedDate(Date requestAcceptedDate) {
        this.requestAcceptedDate = requestAcceptedDate;
    }

    public Long getAcceptDocumentId() {
        return acceptDocumentId;
    }

    public void setAcceptDocumentId(Long acceptDocumentId) {
        this.acceptDocumentId = acceptDocumentId;
    }

    public CashboxOperatorSession getRequestOperatorSession() {
        return requestOperatorSession;
    }

    public void setRequestOperatorSession(CashboxOperatorSession requestOperatorSession) {
        this.requestOperatorSession = requestOperatorSession;
    }

    public CashboxOperatorSession getAcceptOperatorSession() {
        return acceptOperatorSession;
    }

    public void setAcceptOperatorSession(CashboxOperatorSession acceptOperatorSession) {
        this.acceptOperatorSession = acceptOperatorSession;
    }
}
