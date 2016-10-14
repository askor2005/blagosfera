package ru.radom.kabinet.model.payment;

import ru.askor.blagosfera.data.jpa.entities.account.AccountEntity;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;

@Entity
@DiscriminatorValue(value = "account")
public class AccountPayment extends Payment {

	@JoinColumn(name = "account_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private AccountEntity account;

	public AccountEntity getAccount() {
		return account;
	}

	public void setAccount(AccountEntity account) {
		this.account = account;
	}

	@Override
	public String getComment() {
		if (account.getOwner() instanceof UserEntity) {
			UserEntity userEntity = (UserEntity) account.getOwner();
			switch (getType()) {
			case INCOMING:
				return "Пополнение счёта [" + account.getType().getName() + "] участника [" + userEntity.getFullName() + "] в системе R@MERA";
			case OUTGOING:
				return "Вывод средств со счёта [" + account.getType().getName() + "] участника [" + userEntity.getFullName() + "] системы R@MERA";
			}
		}
		return null;
	}

}
