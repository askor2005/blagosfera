package ru.radom.kabinet.model.payment;

import org.apache.commons.lang3.BooleanUtils;
import ru.askor.blagosfera.domain.account.PaymentSystem;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "payment_systems")
public class PaymentSystemEntity extends LongIdentifiable {

	@Column(nullable = false, unique = true)
	private int position;

	@Column(length = 100, unique = true, nullable = false)
	private String name;

	@Column(name = "bean_name", length = 100, unique = true, nullable = false)
	private String beanName;

	@Column(name = "system_incoming_comission", nullable = false)
	private double systemIncomingComission;

	@Column(name = "ramera_incoming_comission", nullable = false)
	private double rameraIncomingComission;

	@Column(name = "system_outgoing_comission", nullable = false)
	private double systemOutgoingComission;

	@Column(name = "ramera_outgoing_comission", nullable = false)
	private double rameraOutgoingComission;

	@Column(name = "active")
	private Boolean active;

	public PaymentSystemEntity() {
	}

	public PaymentSystem toDomain() {
		PaymentSystem paymentSystem = new PaymentSystem();
		paymentSystem.setId(getId());
		paymentSystem.setPosition(getPosition());
		paymentSystem.setName(getName());
		paymentSystem.setBeanName(getBeanName());
		paymentSystem.setSystemIncomingComission(getSystemIncomingComission());
		paymentSystem.setRameraIncomingComission(getRameraIncomingComission());
		paymentSystem.setSystemOutgoingComission(getSystemOutgoingComission());
		paymentSystem.setRameraOutgoingComission(getRameraOutgoingComission());
		paymentSystem.setActive(BooleanUtils.toBooleanDefaultIfNull(getActive(), true));
		return paymentSystem;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public double getSystemComission() {
		return systemIncomingComission;
	}

	public void setSystemComission(double systemComission) {
		this.systemIncomingComission = systemComission;
	}

	public double getSystemIncomingComission() {
		return systemIncomingComission;
	}

	public void setSystemIncomingComission(double systemIncomingComission) {
		this.systemIncomingComission = systemIncomingComission;
	}

	public double getRameraIncomingComission() {
		return rameraIncomingComission;
	}

	public void setRameraIncomingComission(double rameraIncomingComission) {
		this.rameraIncomingComission = rameraIncomingComission;
	}

	public double getSystemOutgoingComission() {
		return systemOutgoingComission;
	}

	public void setSystemOutgoingComission(double systemOutgoingComission) {
		this.systemOutgoingComission = systemOutgoingComission;
	}

	public double getRameraOutgoingComission() {
		return rameraOutgoingComission;
	}

	public void setRameraOutgoingComission(double rameraOutgoingComission) {
		this.rameraOutgoingComission = rameraOutgoingComission;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}
