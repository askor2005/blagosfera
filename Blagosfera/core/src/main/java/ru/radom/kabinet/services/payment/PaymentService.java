package ru.radom.kabinet.services.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.entities.account.AccountEntity;
import ru.askor.blagosfera.domain.account.PaymentSystem;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.account.payment.PaymentStatusEvent;
import ru.radom.kabinet.dao.payment.PaymentDao;
import ru.radom.kabinet.dao.payment.PaymentSystemDao;
import ru.radom.kabinet.model.payment.*;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.web.AutopostParameters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

@Transactional
@Service("paymentService")
public class PaymentService implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private ApplicationContext applicationContext;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

	@Autowired
	private PaymentDao paymentDao;

	@Autowired
	private PaymentSystemDao paymentSystemDao;

	// GENERAL

	public AccountPayment createAccountPayment(BigDecimal raAmount, BigDecimal rurAmount, AccountEntity account, PaymentSystemEntity system, PaymentType type, String sender, String receiver) {

		if (raAmount == null && rurAmount == null) {
			throw new PaymentException("Не задана сумма");
		}

		if (system == null) {
			throw new PaymentException("Не выбрана система");
		}

		double rameraComission = type == PaymentType.INCOMING ? system.getRameraOutgoingComission() : system.getRameraIncomingComission();

		if (raAmount != null) {
			rurAmount = raAmount.multiply(new BigDecimal(100 + (type == PaymentType.INCOMING ? rameraComission : -rameraComission))).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
		} else {
			raAmount = rurAmount.multiply(new BigDecimal(100)).divide(new BigDecimal(100 + (type == PaymentType.INCOMING ? rameraComission : -rameraComission)), 2, RoundingMode.HALF_UP);
		}

		if (raAmount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new PaymentException("Сумма должна быть положительной");
		}
		if (account == null) {
			throw new PaymentException("Не задан счёт");
		}

		AccountPayment payment = new AccountPayment();
		payment.setAccount(account);
		payment.setRaAmount(raAmount);
		payment.setRurAmount(rurAmount);
		payment.setRameraComission(rameraComission);
		payment.setStatus(PaymentStatus.NEW);
		payment.setType(type);
		payment.setSystem(system);
		payment.setCreatedAt(new Date());
		payment.setSender(sender);
		payment.setReceiver(receiver);
		paymentDao.save(payment);
		return payment;
	}

	// INCOMING

	public AccountPayment createAccountIncomingPayment(BigDecimal raAmount, BigDecimal rurAmount, AccountEntity account, PaymentSystemEntity system) {
		PaymentSystemBean bean = getPaymentSystemBean(system);
		return createAccountPayment(raAmount, rurAmount, account, system, PaymentType.INCOMING, null, bean.getIdentifier());
	}

	public AutopostParameters initIncomingPayment(Payment payment) {
		PaymentSystemBean bean = getPaymentSystemBean(payment.getSystem());
		assert bean != null;
		AutopostParameters autopostParameters = bean.initIncomingPayment(payment);
		return autopostParameters;
	}

	public Payment checkIncomingPayment(Payment payment) {
		PaymentSystemBean bean = getPaymentSystemBean(payment.getSystem());
		PaymentStatus newStatus = bean.checkIncomingPayment(payment);
		if (!newStatus.equals(payment.getStatus())) {
			changeIncomingPaymentStatus(payment, newStatus);
		}
		return payment;
	}

	public void failIncomingPayment(Payment payment, String error) {
		payment.setError(error);
		changeIncomingPaymentStatus(payment, PaymentStatus.FAIL);
	}

	public void changeIncomingPaymentStatus(Payment payment, PaymentStatus newStatus) {
		PaymentStatus oldStatus = payment.getStatus();
		logger.info("changing payment #" + payment.getId() + " status " + oldStatus + " ---> " + newStatus);
		try {
			changeIncomingPaymentStatusInternal(payment, newStatus);
			logger.info("payment #" + payment.getId() + " status " + oldStatus + " ---> " + newStatus + " changed");
		} catch (Exception e) {
			logger.info("payment #" + payment.getId() + " status " + oldStatus + " ---> " + newStatus + " changing error");
			logger.error(e.getMessage(), e);
		}
		if (payment.getStatus().isComplete()) {
			PaymentSystemBean bean = getPaymentSystemBean(payment.getSystem());
			try {
				bean.onPaymentComplete(payment);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private void changeIncomingPaymentStatusInternal(Payment payment, PaymentStatus newStatus) {
		PaymentStatus oldStatus = payment.getStatus();
		payment.setStatus(newStatus);
		if (newStatus.isComplete()) {
			payment.setCompletedAt(new Date());
		}
		paymentDao.update(payment);
        blagosferaEventPublisher.publishEvent(new PaymentStatusEvent(this, payment, oldStatus, newStatus));
	}

	// OUTGOING

	public AccountPayment createAccountOutgoingPayment(BigDecimal raAmount, BigDecimal rurAmount, AccountEntity account, PaymentSystemEntity system, String receiver) {
		if (!StringUtils.hasLength(receiver)) {
			throw new PaymentException("Не задан номер кошелька");
		}
		PaymentSystemBean bean = getPaymentSystemBean(system);
		return createAccountPayment(raAmount, rurAmount, account, system, PaymentType.OUTGOING, bean.getIdentifier(), receiver);
	}

	public Payment initOutgoingPayment(Payment payment) {
		payment.setStatus(PaymentStatus.PROCESSING);
		paymentDao.update(payment);
        blagosferaEventPublisher.publishEvent(new PaymentStatusEvent(this, payment, PaymentStatus.NEW, PaymentStatus.PROCESSING));
		PaymentSystemBean bean = getPaymentSystemBean(payment.getSystem());
		payment = bean.initOutgoingPayment(payment);
		return payment;
	}

	public Payment checkOutgoingPayment(Payment payment) {
		PaymentSystemBean bean = getPaymentSystemBean(payment.getSystem());
		PaymentStatus oldStatus = payment.getStatus();
		PaymentStatus newStatus = DateUtils.isOlderThan(payment.getCreatedAt(), Calendar.MINUTE, 30) ? PaymentStatus.FAIL : bean.checkOutgoingPayment(payment);
		if (!newStatus.equals(oldStatus)) {
			payment.setStatus(newStatus);
			if (newStatus.isComplete()) {
				payment.setCompletedAt(new Date());
			}
			paymentDao.update(payment);
            blagosferaEventPublisher.publishEvent(new PaymentStatusEvent(this, payment, oldStatus, newStatus));
		}
		return payment;
	}

	// UTIL

	private PaymentSystemBean getPaymentSystemBean(PaymentSystemEntity paymentSystemEntity) {
		paymentSystemEntity = paymentSystemDao.getById(paymentSystemEntity.getId());
		PaymentSystem paymentSystem = paymentSystemEntity.toDomain();
		String beanName = paymentSystem.isActive() ? paymentSystem.getBeanName() : null;
		try {
			return (PaymentSystemBean) applicationContext.getBean(beanName);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void checkPayments() {
		for (Payment payment : paymentDao.getList(PaymentStatus.PROCESSING, PaymentType.INCOMING)) {
			if (DateUtils.isOlderThan(payment.getCreatedAt(), Calendar.MINUTE, 30)) {
				failIncomingPayment(payment, "");
			} else {
				checkIncomingPayment(payment);
			}
		}
		for (Payment payment : paymentDao.getList(PaymentStatus.NEW, PaymentType.INCOMING)) {
			if (DateUtils.isOlderThan(payment.getCreatedAt(), Calendar.MINUTE, 30)) {
				failIncomingPayment(payment, "");
			}
		}
		for (Payment payment : paymentDao.getList(PaymentStatus.PROCESSING, PaymentType.OUTGOING)) {
			checkOutgoingPayment(payment);
		}
	}

}
