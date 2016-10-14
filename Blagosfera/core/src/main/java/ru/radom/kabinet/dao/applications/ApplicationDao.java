package ru.radom.kabinet.dao.applications;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.applications.Application;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.web.Section;
import ru.radom.kabinet.utils.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Repository
public class ApplicationDao extends Dao<Application> {

	public List<Application> search(Section section, String query, boolean includeNotSharers, boolean includeDownloaded, boolean includeBought, boolean includeInstalled, Long userId, int firstResult, int maxResults) {
		Criteria criteria = getCriteria(Order.asc("name"), firstResult, maxResults);
		if (section != null) {
			criteria.add(Restrictions.eq("featuresLibrarySection", section));
		}
		if (StringUtils.hasLength(query)) {
			criteria.add(Restrictions.ilike("name", query, MatchMode.ANYWHERE));
		}
		criteria.createAlias("sharerApplications", "sharerApplicationsAlias", includeNotSharers ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN, Restrictions.eq("sharerApplicationsAlias.userEntity.id", userId));

		if (!includeDownloaded) {
			Disjunction disjunction = Restrictions.disjunction(Restrictions.ne("cost", BigDecimal.ZERO), Restrictions.eq("sharerApplicationsAlias.installed", true));
			if (includeNotSharers) {
				disjunction.add(Restrictions.isNull("sharerApplicationsAlias.id"));
			}
			criteria.add(disjunction);
		}
		if (!includeBought) {
			Disjunction disjunction = Restrictions.disjunction(Restrictions.eq("cost", BigDecimal.ZERO), Restrictions.eq("sharerApplicationsAlias.installed", true));
			if (includeNotSharers) {
				disjunction.add(Restrictions.isNull("sharerApplicationsAlias.id"));
			}
			criteria.add(disjunction);
		}
		if (!includeInstalled) {
			Disjunction disjunction = Restrictions.disjunction(Restrictions.ne("sharerApplicationsAlias.installed", true));
			if (includeNotSharers) {
				disjunction.add(Restrictions.isNull("sharerApplicationsAlias.id"));
			}
			criteria.add(disjunction);
		}
		return find(criteria);
	}

	public List<Application> getBySection(Section section) {
		return find(Order.asc("name"), Restrictions.eq("featuresLibrarySection", section));
	}

	public Application getByClientId(String clientId) {
		return findFirst(Restrictions.eq("clientId", clientId));
	}

	public boolean checkClientId(String clientId) {
		return !exists(Restrictions.eq("clientId", clientId));
	}

	public int getCountBySection(Section section) {
		return count(Restrictions.eq("featuresLibrarySection", section));
	}

	public List<Application> getByCommunity(CommunityEntity community) {
		// TODO Переделать запрос
		/*if (community.getAssociationForm() != null) {
			return find(getCriteria(Order.asc("name")).createAlias("communityAssociationForms", "communityAssociationFormsAlias").add(Restrictions.eq("communityAssociationFormsAlias.id", community.getAssociationForm().getId())));
		} else {*/
			return Collections.EMPTY_LIST;
		//}
	}

}
