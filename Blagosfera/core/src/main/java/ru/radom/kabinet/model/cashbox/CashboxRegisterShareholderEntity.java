package ru.radom.kabinet.model.cashbox;


import ru.askor.blagosfera.domain.cashbox.CashboxRegisterShareholder;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cashbox_register_shareholder")
public class CashboxRegisterShareholderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cashbox_register_shareholder_id_generator")
    @SequenceGenerator(name = "cashbox_register_shareholder_id_generator", sequenceName = "cashbox_register_shareholder_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "sharer_ikp", nullable = false)
    private String sharerIkp;

    @Column(name = "community_id", nullable = false)
    private Long communityId;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestCreatedDate;

    @Column(name = "request_document_id", nullable = false)
    private Long requestDocumentId;

    @Column(name = "accepted_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestAcceptedDate;

    @Column(name = "accept_document_id")
    private Long acceptDocumentId;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "request_session_id", updatable = false, nullable = false)
    private CashboxOperatorSessionEntity requestOperatorSession;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "accept_session_id")
    private CashboxOperatorSessionEntity acceptOperatorSession;

    public CashboxRegisterShareholderEntity() {
    }

    public CashboxRegisterShareholder toDomain() {
        CashboxRegisterShareholder result = new CashboxRegisterShareholder();
        result.setId(getId());
        result.setSharerIkp(getSharerIkp());
        result.setCommunityId(getCommunityId());
        result.setRequestCreatedDate(getRequestCreatedDate());
        result.setRequestDocumentId(getRequestDocumentId());
        result.setRequestAcceptedDate(getRequestAcceptedDate());
        result.setAcceptDocumentId(getAcceptDocumentId());
        result.setRequestOperatorSession(getRequestOperatorSession().toDomain());
        result.setAcceptOperatorSession(getAcceptOperatorSession() == null ? null : getAcceptOperatorSession().toDomain());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CashboxRegisterShareholderEntity)) return false;

        CashboxRegisterShareholderEntity that = (CashboxRegisterShareholderEntity) o;

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

    public CashboxOperatorSessionEntity getRequestOperatorSession() {
        return requestOperatorSession;
    }

    public void setRequestOperatorSession(CashboxOperatorSessionEntity requestOperatorSession) {
        this.requestOperatorSession = requestOperatorSession;
    }

    public CashboxOperatorSessionEntity getAcceptOperatorSession() {
        return acceptOperatorSession;
    }

    public void setAcceptOperatorSession(CashboxOperatorSessionEntity acceptOperatorSession) {
        this.acceptOperatorSession = acceptOperatorSession;
    }
}
