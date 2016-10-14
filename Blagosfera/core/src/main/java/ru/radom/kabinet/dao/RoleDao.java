package ru.radom.kabinet.dao;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.Role;

import java.util.List;

@Repository("roleDao")
public class RoleDao extends Dao<Role> {

	public List<Role> getAll() {
		return findAll(Order.asc("id"));
	}

	public Role getByName(String name) {
		return findFirst(Restrictions.eq("name", name));
	}

}
