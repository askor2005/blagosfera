package ru.radom.kabinet.services.payment;

import ru.radom.kabinet.model.payment.Payment;
import ru.radom.kabinet.model.payment.PaymentStatus;
import ru.radom.kabinet.web.AutopostParameters;

public interface PaymentSystemBean {

	AutopostParameters initIncomingPayment(Payment payment);
	PaymentStatus checkIncomingPayment(Payment payment);
	void onPaymentComplete(Payment payment);
	Payment initOutgoingPayment(Payment payment) throws PaymentException;
	PaymentStatus checkOutgoingPayment(Payment payment);
	String getIdentifier();
	
}
