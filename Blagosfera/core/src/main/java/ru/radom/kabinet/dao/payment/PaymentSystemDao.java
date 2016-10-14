package ru.radom.kabinet.dao.payment;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.payment.PaymentSystemEntity;

import java.util.List;

@Repository("paymentSystemDao")
public class PaymentSystemDao extends Dao<PaymentSystemEntity> {

	public List<PaymentSystemEntity> getList() {
		return findAll(Order.asc("position"));
	}

	public List<PaymentSystemEntity> getActiveList() {
		return find(Order.asc("position"), Restrictions.or(Restrictions.eq("active", true), Restrictions.isNull("active")));
	}
	
}
