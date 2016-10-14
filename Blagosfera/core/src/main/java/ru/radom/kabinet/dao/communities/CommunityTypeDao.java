package ru.radom.kabinet.dao.communities;

import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.communities.CommunityType;

import java.util.List;

@Repository("communityTypeDao")
public class CommunityTypeDao extends Dao<CommunityType> {
	
	public List<CommunityType> getAll() {
		return findAll(Order.asc("position"));
	}

}
