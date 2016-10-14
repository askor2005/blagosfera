package ru.askor.blagosfera.domain.cashbox;

import java.util.Date;

public class CashboxExchangeProtocols {

    private String sharerContributionProtocolCode;
    private String sharerRefundProtocolCode;

    private Date sharerContributionProtocolCreatedDate;
    private Date sharerRefundProtocolCreatedDate;

    public CashboxExchangeProtocols() {
    }

    public String getSharerContributionProtocolCode() {
        return sharerContributionProtocolCode;
    }

    public void setSharerContributionProtocolCode(String sharerContributionProtocolCode) {
        this.sharerContributionProtocolCode = sharerContributionProtocolCode;
    }

    public String getSharerRefundProtocolCode() {
        return sharerRefundProtocolCode;
    }

    public void setSharerRefundProtocolCode(String sharerRefundProtocolCode) {
        this.sharerRefundProtocolCode = sharerRefundProtocolCode;
    }

    public Date getSharerContributionProtocolCreatedDate() {
        return sharerContributionProtocolCreatedDate;
    }

    public void setSharerContributionProtocolCreatedDate(Date sharerContributionProtocolCreatedDate) {
        this.sharerContributionProtocolCreatedDate = sharerContributionProtocolCreatedDate;
    }

    public Date getSharerRefundProtocolCreatedDate() {
        return sharerRefundProtocolCreatedDate;
    }

    public void setSharerRefundProtocolCreatedDate(Date sharerRefundProtocolCreatedDate) {
        this.sharerRefundProtocolCreatedDate = sharerRefundProtocolCreatedDate;
    }
}
