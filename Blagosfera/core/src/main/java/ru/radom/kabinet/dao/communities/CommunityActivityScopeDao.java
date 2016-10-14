package ru.radom.kabinet.dao.communities;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.communities.CommunityActivityScope;

import java.util.List;

@Repository("communityActivityScopeDao")
public class CommunityActivityScopeDao extends Dao<CommunityActivityScope> {

	public List<CommunityActivityScope> getAll() {
		return findAll(Order.asc("name"));
	}

	public boolean checkName(CommunityActivityScope scope) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("name", scope.getName()));
		if (scope.getId() != null) {
			conjunction.add(Restrictions.ne("id", scope.getId()));
		}
		return count(conjunction) == 0;
	}

}
