package ru.radom.kabinet.dao.bio;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.bio.FingerToken;
import ru.radom.kabinet.model.bio.TokenStatus;

@Repository("fingerTokenDao")
public class FingerTokenDao extends Dao<FingerToken> {

	public FingerToken get(String ikp, String requestId) {
		Criteria criteria = getCriteria(Order.desc("initDate"), 0, 1);
		criteria.createAlias("user", "userAlias");
		criteria.add(Restrictions.eq("userAlias.ikp", ikp));
		criteria.add(Restrictions.eq("requestId", requestId));
		return (FingerToken) criteria.uniqueResult();
	}

	public FingerToken get(Long userId, String value) {
		return findFirst(Order.desc("getDate"), Restrictions.eq("user.id", userId), Restrictions.eq("value", value));
	}

    public FingerToken getActiveByUserIdAndIp(Long userId, String ip) {
        Criteria criteria = getCriteria(Order.desc("initDate"), 0, 1);
		criteria.createAlias("user", "userAlias");
		criteria.add(Restrictions.eq("userAlias.id", userId));
		criteria.add(Restrictions.eq("ip", ip));
        criteria.add(Restrictions.eq("status", TokenStatus.INIT));
		return (FingerToken) criteria.uniqueResult();
    }
}
