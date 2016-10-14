package ru.radom.kabinet.dao.communities.inventory;

import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.communities.inventory.CommunityInventoryUnitTypeEntity;

import java.util.List;

@Repository
public class CommunityInventoryUnitTypeDao extends Dao<CommunityInventoryUnitTypeEntity>{

	public List<CommunityInventoryUnitTypeEntity> getAll() {
		return findAll(Order.asc("name"));
	}
	
}
