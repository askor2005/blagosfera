package ru.radom.kabinet.dao;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.ContactsGroupEntity;

import java.util.List;

@Repository("contactsGroupDao")
public class ContactsGroupDao extends Dao<ContactsGroupEntity> {
	public List<ContactsGroupEntity> getBySharer(Long userId) {
		return find(Order.asc("name"), Restrictions.eq("user.id", userId));
	}

	public boolean checkName(Long userId, ContactsGroupEntity group) {
		return findFirst(Restrictions.eq("user.id", userId), Restrictions.eq("name", group.getName()), Restrictions.ne("id", group.getId())) == null;
	}
}