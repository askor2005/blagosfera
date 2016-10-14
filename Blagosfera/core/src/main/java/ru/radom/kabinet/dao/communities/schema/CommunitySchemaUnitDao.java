package ru.radom.kabinet.dao.communities.schema;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnitType;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.schema.CommunitySchemaUnitEntity;

import java.util.List;

@Repository("communitySchemaUnitDao")
public class CommunitySchemaUnitDao extends Dao<CommunitySchemaUnitEntity> {

	public List<CommunitySchemaUnitEntity> getList(CommunityEntity community, CommunitySchemaUnitType type) {
		return find(Order.asc("name"), Restrictions.eq("schema", community.getSchema()), Restrictions.eq("type", type));
	}

	public List<CommunitySchemaUnitEntity> getByCommunityId(Long communityId, CommunitySchemaUnitType type) {
		Criteria criteria = getCriteria();
		criteria.createAlias("schema", "schemaAlias");
		criteria.add(Restrictions.eq("schemaAlias.community.id", communityId));
		criteria.add(Restrictions.eq("type", type));
		criteria.addOrder(Order.asc("name"));

		return find(criteria);
	}
}
