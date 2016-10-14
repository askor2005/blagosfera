package ru.askor.blagosfera.data.jpa.entities.account;

import ru.askor.blagosfera.domain.account.AccountType;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account_types")
public class AccountTypeEntity extends LongIdentifiable {

	@Column(nullable = false, unique = true)
	private int position;

	@Column(length = 100, unique = true, nullable = false)
	private String name;

	@Column(name = "owner_discriminator", length = 100, nullable = false)
	private String ownerDiscriminator;

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

	public String getOwnerDiscriminator() {
		return ownerDiscriminator;
	}

	public void setOwnerDiscriminator(String ownerDiscriminator) {
		this.ownerDiscriminator = ownerDiscriminator;
	}

	public AccountType toDomain() {
		AccountType result = new AccountType();
		result.setId(getId());
		result.setName(getName());
        result.setDiscriminator(getOwnerDiscriminator());
		return result;
	}

	public static List<AccountType> toDomainList(List<AccountTypeEntity> accountTypes) {
		List<AccountType> result = null;
		if (accountTypes != null) {
			result = new ArrayList<>();
			for (AccountTypeEntity accountType : accountTypes) {
				result.add(accountType.toDomain());
			}
		}
		return result;
	}

}
