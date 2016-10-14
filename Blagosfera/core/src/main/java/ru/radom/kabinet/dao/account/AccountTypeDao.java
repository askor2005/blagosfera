package ru.radom.kabinet.dao.account;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.data.jpa.entities.account.AccountTypeEntity;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;

import java.util.List;

@Repository("accountTypeDao")
public class AccountTypeDao extends Dao<AccountTypeEntity> {

	public List<AccountTypeEntity> getList(Class<? extends Object> ownerClass) {
		return find(Order.asc("position"), Restrictions.eq("ownerDiscriminator", Discriminators.get(ownerClass)));
	}

	/**
	 * Получить счет по умолчанию
	 * @return
	 */
	public AccountTypeEntity getDefaultAccountType(Class<?> clazz) {
		if (!clazz.equals(UserEntity.class) && !clazz.equals(CommunityEntity.class)) {
			throw new RuntimeException("Счета могут иметь только объединения и участники системы!");
		}
		return findFirst(Order.asc("id"), Restrictions.eq("ownerDiscriminator", Discriminators.get(clazz)));
	}
	
}
