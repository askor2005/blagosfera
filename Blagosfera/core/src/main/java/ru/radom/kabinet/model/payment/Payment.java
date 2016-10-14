package ru.radom.kabinet.model.payment;

import ru.askor.blagosfera.domain.account.PaymentDomain;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "payments")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "general")
public class Payment extends LongIdentifiable {

	@Column(name = "ra_amount", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
	private BigDecimal raAmount;

	@Column(name = "rur_amount", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
	private BigDecimal rurAmount;

	@Column(name = "ramera_comission", nullable = false)
	private double rameraComission;

	@Column(nullable = false)
	private PaymentStatus status;

	@Column(nullable = false)
	private PaymentType type;

	@Column(length = 100)
	private String sender;

	@Column(length = 100)
	private String receiver;

	@Column(length = 1000)
	private String additionalData;

	@JoinColumn(name = "payment_system_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private PaymentSystemEntity system;

	@Column(name = "created_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(name = "completed_at", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date completedAt;

	@Column(length = 1000, nullable = true)
	private String error;

	public BigDecimal getRaAmount() {
		return raAmount;
	}

	public void setRaAmount(BigDecimal raAmount) {
		this.raAmount = raAmount;
	}

	public BigDecimal getRurAmount() {
		return rurAmount;
	}

	public void setRurAmount(BigDecimal rurAmount) {
		this.rurAmount = rurAmount;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public String getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}

	public PaymentSystemEntity getSystem() {
		return system;
	}

	public void setSystem(PaymentSystemEntity system) {
		this.system = system;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Date completedAt) {
		this.completedAt = completedAt;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public PaymentType getType() {
		return type;
	}

	public void setType(PaymentType type) {
		this.type = type;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public double getRameraComission() {
		return rameraComission;
	}

	public void setRameraComission(double rameraComission) {
		this.rameraComission = rameraComission;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public BigDecimal getRameraComissionAmount() {
		return rurAmount.subtract(raAmount).abs();
	}
	
	public String getComment() {
		switch (type) {
		case INCOMING:
			return "Платеж в систему R@MERA";
		case OUTGOING:
			return "Вывод средств из системы R@MERA";
		}
		return null;
	}
	public PaymentDomain toDomain() {
		return new PaymentDomain(getRaAmount(),getRurAmount(),getRameraComission(),getSender(),getReceiver(),getAdditionalData(),getRameraComissionAmount(),getSystem() != null ? getSystem().getName() : null );
	}

}
