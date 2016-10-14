package ru.radom.kabinet.dao.communities;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.*;
import org.hibernate.type.IntegerType;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.radom.kabinet.collections.CommunityMemberStatusList;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository("communityMemberDao")
public class CommunityMemberDao extends Dao<CommunityMemberEntity> {

	// TODO Переделать
	/*public int getPagesCount(CommunityEntity community, int perPage) {
		return (int) Math.ceil((double) community.getMembersCount() / perPage);
	}*/

	public List<CommunityMemberEntity> getPage(CommunityEntity community, int perPage, int page) {
		return find(getCriteria(Order.asc("id"), (page - 1) * perPage, perPage).add(Restrictions.eq("community", community)).add(Restrictions.eq("status", CommunityMemberStatus.MEMBER)).createAlias("user", "userAlias").add(Restrictions.eq("userAlias.deleted", false)));
	}

	private static final String MEMBERS_COUNT_SQL = "SELECT COUNT(CM.id)\n" +
			"FROM community_members CM\n" +
			"INNER JOIN sharers S ON S.id=CM.sharer_id\n" +
			"WHERE CM.community_id = :community_id AND CM.status IN (2,6,7) AND S.deleted = false";

	public int getMembersCount(CommunityEntity community) {
		Query query = getCurrentSession().createSQLQuery(MEMBERS_COUNT_SQL).addScalar("count", IntegerType.INSTANCE).setLong("community_id",community.getId());
		return (Integer)query.uniqueResult();
	}

	public List<CommunityMemberEntity> getList(CommunityEntity community, CommunityMemberStatusList statusList, int firstResult, int maxResults, String query, List<Long> excludeUserIds) {
		Criteria criteria = getCriteria(Order.asc("id"), firstResult, maxResults);
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("community", community));
		conjunction.add(Restrictions.in("status", statusList));
		conjunction.add(Restrictions.eq("userAlias.deleted", false));
		criteria.createAlias("user", "userAlias");

		if (StringUtils.hasLength(query)) {
			conjunction.add(Restrictions.ilike("userAlias.searchString", query, MatchMode.ANYWHERE));
		}
		if (excludeUserIds != null && excludeUserIds.size() > 0) {
			conjunction.add(Restrictions.not(Restrictions.in("userAlias.id", excludeUserIds)));
		}
		criteria.add(conjunction);
		return find(criteria);
		//return criteria.list();
	}

	public int count(Long userId, CommunityMemberStatus status) {
		return count(userId, status, null);
	}

	public int count(Long userId, CommunityMemberStatus status, Boolean creator) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("user.id", userId));
		conjunction.add(Restrictions.eq("status", status));
		if (creator != null) {
			conjunction.add(Restrictions.eq("creator", creator));
		}

		return ((Long) getCriteria().createAlias("community", "communityAlias").add(Restrictions.eq("communityAlias.deleted", false)).add(Restrictions.isNull("communityAlias.parent")).add(conjunction).setProjection(Projections.count("id")).uniqueResult()).intValue();
		// return count(conjunction);
	}

	public boolean exists(CommunityEntity community, UserEntity user, CommunityMemberStatus status) {
		return count(Restrictions.eq("community", community), Restrictions.eq("user", user), Restrictions.eq("status", status)) > 0;
	}

	public boolean exists(CommunityEntity community, UserEntity user) {
		return count(Restrictions.eq("community", community), Restrictions.eq("user", user)) > 0;
	}

	public CommunityMemberEntity get(CommunityEntity community, Long userId) {
		return findFirst(Restrictions.eq("community", community), Restrictions.eq("user.id", userId));
	}

    public CommunityMemberEntity get(Long communityId, Long userId) {
		return findFirst(Restrictions.eq("community.id", communityId), Restrictions.eq("user.id", userId));
	}

	public int getRequestsCount(Long userId) {
		return (int) createSQLQuery("select count(*) as c from community_members where (status = :request) and (sharer_id not in (select s.id from sharers as s where s.deleted)) and (community_id in (select id from communities where creator_id = :sharer_id))").addScalar("c", IntegerType.INSTANCE).setInteger("request", CommunityMemberStatus.REQUEST.ordinal()).setLong("sharer_id", userId).uniqueResult();
	}

	public List<CommunityMemberEntity> getRequests(Long userId, int firstResult, int maxResults) {
		Criteria criteria = getCriteria(Order.desc("id"), firstResult, maxResults);
		criteria.createAlias("community", "communityAlias");
		criteria.createAlias("user", "userAlias");
		criteria.add(Restrictions.eq("communityAlias.creator.id", userId));
		criteria.add(Restrictions.eq("userAlias.deleted", false));
		criteria.add(Restrictions.eq("status", CommunityMemberStatus.REQUEST));
		return find(criteria);
	}

	public List<CommunityMemberEntity> getByCommynityCreator(Long userId, List<CommunityMemberStatus> statusList, int page, int perPage) {
		Criteria criteria = getCriteria(Order.desc("id"), page * perPage, perPage);
		criteria.createAlias("community", "communityAlias");
		criteria.createAlias("user", "userAlias");
		criteria.add(Restrictions.eq("communityAlias.creator.id", userId));
		criteria.add(Restrictions.eq("userAlias.deleted", false));
		criteria.add(Restrictions.in("status", statusList));
		return find(criteria);
	}

	public int getInvitesCount(Long userId) {
		return count(Restrictions.eq("status", CommunityMemberStatus.INVITE), Restrictions.eq("user.id", userId));
	}

	public long getMyRequestsCount(Long userId) {
		return count(Restrictions.eq("status", CommunityMemberStatus.REQUEST), Restrictions.eq("user.id", userId));
	}

	// public List<CommunityMemberEntity> getNewsWriters(CommunityEntity community) {
	// return getCriteria().add(Restrictions.eq("community",
	// community)).add(Restrictions.eq("newsWriter",
	// true)).createAlias("sharer",
	// "sharerAlias").add(Restrictions.eq("sharerAlias.deleted", false)).list();
	// }
	//
	// public List<CommunityMemberEntity> getModerators(CommunityEntity community) {
	// return getCriteria().add(Restrictions.eq("community",
	// community)).add(Restrictions.eq("moderator", true)).createAlias("sharer",
	// "sharerAlias").add(Restrictions.eq("sharerAlias.deleted", false)).list();
	// }

	public List<CommunityMemberEntity> getList(Set<CommunityEntity> communities, Set<UserEntity> users) {
		if (communities.isEmpty() || users.isEmpty()) {
			return Collections.EMPTY_LIST;
		} else {
			return find(Restrictions.in("user", users), Restrictions.in("community", communities));
		}
	}

    public int getListCount(CommunityEntity community, boolean withContentUser, String searchString) {
		Criteria criteria = getCriteria(Order.asc("id"));
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("community", community));
		conjunction.add(Restrictions.eq("status", CommunityMemberStatus.MEMBER));
		conjunction.add(Restrictions.eq("userAlias.deleted", false));
		criteria.createAlias("user", "userAlias");

		if (StringUtils.hasLength(searchString))
		    conjunction.add(Restrictions.ilike("userAlias.searchString", searchString, MatchMode.ANYWHERE));

		if (!withContentUser)
			conjunction.add(Restrictions.not(Restrictions.eq("userAlias.id", SecurityUtils.getUser().getId())));

        criteria.add(conjunction);
		return find(criteria).size();
	}

	public List<CommunityMemberEntity> getList(CommunityEntity community, int perPage, int page, boolean withContentUser, String searchString) {
		Criteria criteria = getCriteria(Order.asc("id"), page * perPage, perPage);
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("community", community));
		conjunction.add(Restrictions.eq("status", CommunityMemberStatus.MEMBER));
		conjunction.add(Restrictions.eq("userAlias.deleted", false));
		criteria.createAlias("user", "userAlias");

		if (StringUtils.hasLength(searchString)) {
			conjunction.add(Restrictions.ilike("userAlias.searchString", searchString, MatchMode.ANYWHERE));
		}
		if (!withContentUser) {
			conjunction.add(Restrictions.not(Restrictions.eq("userAlias.id", SecurityUtils.getUser().getId())));
		}
		criteria.add(conjunction);
		return find(criteria);
	}

	// Получить участников по набору прав у них в объединении
	public List<CommunityMemberEntity> getByPermissions(Long communityId, List<String> permissions) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("community.id", communityId));
		criteria.add(Restrictions.eq("status", CommunityMemberStatus.MEMBER));
		criteria.createAlias("posts", "postsAlias");
		criteria.createAlias("postsAlias.permissions", "permissionsAlias");
		criteria.add(Restrictions.in("permissionsAlias.name", permissions));

		return find(criteria);
	}

}
