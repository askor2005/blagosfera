package ru.radom.kabinet.model.notifications;

import ru.askor.blagosfera.domain.RadomAccount;
import ru.askor.blagosfera.domain.systemaccount.SystemAccount;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "system_accounts")
public class SystemAccountEntity extends LongIdentifiable implements RadomAccount {

	public static final long BLAGOSFERA_ID = 1L;

	public static final long NTC_ASCOR_ID = 2l;

	@Column(nullable = false)
	private String name;

	@Column(name = "avatar", nullable = false)
	private String avatar;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public String getLink() {
		return "#";
	}

	@Override
	public String getObjectType() {
		return Discriminators.SYSTEM_ACCOUNT;
	}

	@Override
	public String getIkp() {
		return String.valueOf(getId());
	}

	public SystemAccount toDomain() {
		SystemAccount result = new SystemAccount();
		result.setId(getId());
		result.setName(getName());
		result.setAvatar(getAvatar());
		return result;
	}

	public static SystemAccount toDomainSafe(SystemAccountEntity entity) {
		SystemAccount result = null;
		if (entity != null) {
			result = entity.toDomain();
		}
		return result;
	}
}
