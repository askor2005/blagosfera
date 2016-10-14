package ru.radom.kabinet.model.cashbox;

import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cashbox_exchange_log")
public class CashboxExchangeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cashbox_exchange_log_id_generator")
    @SequenceGenerator(name = "cashbox_exchange_log_id_generator", sequenceName = "cashbox_exchange_log_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "request_id")
    private String requestId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sharer_id", nullable = true)
    private UserEntity userEntity;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "session_id", updatable = false, nullable = false)
    private CashboxOperatorSessionEntity operatorSession;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "accepted_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date acceptedDate;

    @Column(name = "sharer_contribution_statement_document_id", nullable = false)
    private Long sharerContributionStatementDocumentId;

    @Column(name = "shop_contribution_statement_document_id", nullable = false)
    private Long shopContributionStatementDocumentId;

    @Column(name = "sharer_contribution_protocol_document_id", nullable = false)
    private Long sharerContributionProtocolDocumentId;

    @Column(name = "community_contribution_protocol_document_id", nullable = false)
    private Long shopContributionProtocolDocumentId;

    @Column(name = "sharer_refund_statement_document_id", nullable = false)
    private Long sharerRefundStatementDocumentId;

    @Column(name = "shop_refund_statement_document_id", nullable = false)
    private Long shopRefundStatementDocumentId;

    @Column(name = "sharer_refund_protocol_document_id", nullable = false)
    private Long sharerRefundProtocolDocumentId;

    @Column(name = "community_refund_protocol_document_id", nullable = false)
    private Long shopRefundProtocolDocumentId;

    @Column(name = "shareholder_membership_fee_statement_document_id", nullable = false)
    private Long sharerMembershipFeeStatementDocumentId;

    @Column(name = "shareholder_membership_fee_protocol_document_id", nullable = false)
    private Long sharerMembershipFeeProtocolDocumentId;

    @OneToOne(fetch = FetchType.LAZY, mappedBy="exchangeOperation", cascade = {CascadeType.ALL})
    private CashboxExchangeTotalsEntity totals;

    public CashboxExchangeEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CashboxExchangeEntity)) return false;

        CashboxExchangeEntity that = (CashboxExchangeEntity) o;

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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public CashboxOperatorSessionEntity getOperatorSession() {
        return operatorSession;
    }

    public void setOperatorSession(CashboxOperatorSessionEntity operatorSession) {
        this.operatorSession = operatorSession;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(Date acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public Long getSharerContributionStatementDocumentId() {
        return sharerContributionStatementDocumentId;
    }

    public void setSharerContributionStatementDocumentId(Long sharerContributionStatementDocumentId) {
        this.sharerContributionStatementDocumentId = sharerContributionStatementDocumentId;
    }

    public Long getShopContributionStatementDocumentId() {
        return shopContributionStatementDocumentId;
    }

    public void setShopContributionStatementDocumentId(Long shopContributionStatementDocumentId) {
        this.shopContributionStatementDocumentId = shopContributionStatementDocumentId;
    }

    public Long getSharerContributionProtocolDocumentId() {
        return sharerContributionProtocolDocumentId;
    }

    public void setSharerContributionProtocolDocumentId(Long sharerContributionProtocolDocumentId) {
        this.sharerContributionProtocolDocumentId = sharerContributionProtocolDocumentId;
    }

    public Long getShopContributionProtocolDocumentId() {
        return shopContributionProtocolDocumentId;
    }

    public void setShopContributionProtocolDocumentId(Long communityContributionProtocolDocumentId) {
        this.shopContributionProtocolDocumentId = communityContributionProtocolDocumentId;
    }

    public Long getSharerRefundStatementDocumentId() {
        return sharerRefundStatementDocumentId;
    }

    public void setSharerRefundStatementDocumentId(Long sharerRefundStatementDocumentId) {
        this.sharerRefundStatementDocumentId = sharerRefundStatementDocumentId;
    }

    public Long getShopRefundStatementDocumentId() {
        return shopRefundStatementDocumentId;
    }

    public void setShopRefundStatementDocumentId(Long shopRefundStatementDocumentId) {
        this.shopRefundStatementDocumentId = shopRefundStatementDocumentId;
    }

    public Long getSharerRefundProtocolDocumentId() {
        return sharerRefundProtocolDocumentId;
    }

    public void setSharerRefundProtocolDocumentId(Long sharerRefundProtocolDocumentId) {
        this.sharerRefundProtocolDocumentId = sharerRefundProtocolDocumentId;
    }

    public Long getShopRefundProtocolDocumentId() {
        return shopRefundProtocolDocumentId;
    }

    public void setShopRefundProtocolDocumentId(Long communityRefundProtocolDocumentId) {
        this.shopRefundProtocolDocumentId = communityRefundProtocolDocumentId;
    }

    public Long getSharerMembershipFeeStatementDocumentId() {
        return sharerMembershipFeeStatementDocumentId;
    }

    public void setSharerMembershipFeeStatementDocumentId(Long sharerMembershipFeeStatementDocumentId) {
        this.sharerMembershipFeeStatementDocumentId = sharerMembershipFeeStatementDocumentId;
    }

    public Long getSharerMembershipFeeProtocolDocumentId() {
        return sharerMembershipFeeProtocolDocumentId;
    }

    public void setSharerMembershipFeeProtocolDocumentId(Long sharerMembershipFeeProtocolDocumentId) {
        this.sharerMembershipFeeProtocolDocumentId = sharerMembershipFeeProtocolDocumentId;
    }

    public CashboxExchangeTotalsEntity getTotals() {
        return totals;
    }

    public void setTotals(CashboxExchangeTotalsEntity totals) {
        this.totals = totals;
        totals.setExchangeOperation(this);
    }
}
