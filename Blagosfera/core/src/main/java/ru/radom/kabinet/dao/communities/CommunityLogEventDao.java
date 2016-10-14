package ru.radom.kabinet.dao.communities;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityLogEventEntity;
import ru.radom.kabinet.utils.DateUtils;

import java.util.Date;
import java.util.List;

@Repository("communityLogEventDao")
public class CommunityLogEventDao extends Dao<CommunityLogEventEntity>{
	
	public List<CommunityLogEventEntity> getList(CommunityEntity community, UserEntity user, CommunityEventType type, Date fromDate, Date toDate, CommunityLogEventEntity lastLoaded, int maxResults) {
		assert community != null;
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("community", community));
		if (user != null) {
			conjunction.add(Restrictions.eq("user", user));
		}
		if (type != null) {
			conjunction.add(Restrictions.eq("type", type));
		}
		if (lastLoaded != null) {
			conjunction.add(Restrictions.lt("id", lastLoaded.getId()));
		}
		if (fromDate != null) {
			conjunction.add(Restrictions.ge("date", DateUtils.getDayBegin(fromDate)));
		}
		if (toDate != null) {
			conjunction.add(Restrictions.le("date", DateUtils.getDayEnd(toDate)));
		}
		return find(Order.desc("date"), 0, maxResults, conjunction);
	}

}
