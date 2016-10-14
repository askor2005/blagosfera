package ru.radom.kabinet.dao.payment;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.payment.Payment;
import ru.radom.kabinet.model.payment.PaymentStatus;
import ru.radom.kabinet.model.payment.PaymentType;

import java.util.List;

@Repository("paymentDao")
public class PaymentDao extends Dao<Payment> {

	public List<Payment> getList(PaymentStatus status, PaymentType type) {
		return find(Restrictions.eq("status", status), Restrictions.eq("type", type));
	}

}
