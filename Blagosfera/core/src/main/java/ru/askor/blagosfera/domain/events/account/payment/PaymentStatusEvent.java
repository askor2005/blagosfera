package ru.askor.blagosfera.domain.events.account.payment;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.radom.kabinet.model.payment.Payment;
import ru.radom.kabinet.model.payment.PaymentStatus;

public class PaymentStatusEvent extends BlagosferaEvent {

    private Payment payment;
    private PaymentStatus fromStatus;
    private PaymentStatus toStatus;


    public PaymentStatusEvent(Object source, Payment payment, PaymentStatus fromStatus, PaymentStatus toStatus) {
        super(source);
        this.payment = payment;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public PaymentStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(PaymentStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public PaymentStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(PaymentStatus toStatus) {
        this.toStatus = toStatus;
    }

}
