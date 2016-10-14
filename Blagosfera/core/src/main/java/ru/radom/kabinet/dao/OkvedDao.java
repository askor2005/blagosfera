package ru.radom.kabinet.dao;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.OkvedEntity;
import ru.radom.kabinet.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository("okvedDao")
public class OkvedDao extends Dao<OkvedEntity> {

	public List<OkvedEntity> getRoots() {
		return find(Order.asc("id"), Restrictions.isNull("parent"));
	}

	public List<OkvedEntity> getChildren(OkvedEntity parent, List<Long> allowedIds) {

		if (allowedIds != null && allowedIds.size() == 0) {
			return Collections.emptyList();
		} else {
			Conjunction conjunction = new Conjunction();
			if (parent == null) {
				conjunction.add(Restrictions.isNull("parent"));
			} else {
				conjunction.add(Restrictions.eq("parent", parent));
			}
			if (allowedIds != null) {
				conjunction.add(Restrictions.in("id", allowedIds));
			}
			return find(Order.asc("id"), conjunction);
		}
	}

	public List<OkvedEntity> getByQuery(String query) {
		return find(Restrictions.ilike("longName", query, MatchMode.ANYWHERE));
	}

	public List<OkvedEntity> getByStringValue(String stringValue) {
		if (StringUtils.hasLength(stringValue)) {
			List<Long> ids = new ArrayList<Long>();
			for (String id : stringValue.split(";")) {
				ids.add(Long.parseLong(id));
			}
			if (ids.size() > 0) {
				return find(Order.asc("id"), Restrictions.in("id", ids));
			} else {
				return Collections.emptyList();
			}
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Поиск по запросу и по ИД, которые не должны попасть в результат
	 * @param query
	 * @param firstResult
	 * @param maxResults
	 * @param excludeIds
	 * @return
	 */
	public List<OkvedEntity> search(String query, int firstResult, int maxResults, List<Long> excludeIds) {
		String orderBy = "longName";
		boolean asc = true;
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.ilike("longName", query, MatchMode.ANYWHERE));
		if (excludeIds != null && excludeIds.size() > 0) {
			conjunction.add(Restrictions.not(Restrictions.in("id", excludeIds)));
		}

		return find(asc ? Order.asc(orderBy) : Order.desc(orderBy), firstResult, maxResults, conjunction);
	}

}
