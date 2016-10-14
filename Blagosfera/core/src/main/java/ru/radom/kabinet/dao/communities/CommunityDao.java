package ru.radom.kabinet.dao.communities;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.*;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.domain.community.CommunityAccessType;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.dao.communities.dto.FieldValueParameterDto;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.OkvedEntity;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.utils.VarUtils;

import java.math.BigInteger;
import java.util.*;

@Repository("communityDao")
public class CommunityDao extends Dao<CommunityEntity> {
	@Autowired
	private FieldDao fieldDao;

	@Autowired
	private FieldValueDao fieldValueDao;

	// выдает список объединений в которых состоит member для показа
	// requester'у, с учетом невидимых объединений
	public List<CommunityEntity> getList(UserEntity member, UserEntity requester) {
		return createQuery("from communities as c where (c.parent is null) and (c.deleted = false) and (c.id in (select community from community_members where sharer = :member_id and status = :status)) and ((c.invisible = false) or (c.id in (select community from community_members where sharer = :requester_id)))").setLong("member_id", member.getId()).setLong("requester_id", requester.getId()).setInteger("status", CommunityMemberStatus.MEMBER.ordinal()).list();
	}

	private Criteria getListCriteria(Long userId, boolean isAdmin, List<CommunityMemberStatus> statusList, Boolean creator, String query, CommunityAccessType accessType, Long activityScopeId, CommunityEntity parent, boolean checkParent, Boolean deleted) {
		Criteria criteria = getCriteria();

		if (userId == null) {
			if (!isAdmin) {
				criteria.add(Restrictions.eq("invisible", false));
			}
		} else {
			criteria.createAlias("members", "membersAlias");
			criteria.add(Restrictions.eq("membersAlias.user.id", userId));
			if (!isAdmin) {
				criteria.add(Restrictions.or(Restrictions.eq("invisible", false), Restrictions.eq("creator.id", userId), Restrictions.in("membersAlias.status", new CommunityMemberStatus[]{CommunityMemberStatus.MEMBER, CommunityMemberStatus.INVITE})));
			}
		}

		if (creator != null) {
			criteria.add(Restrictions.eq("membersAlias.creator", creator));
		}
		if (statusList != null) {
			Disjunction disjunction = Restrictions.or();
			for (CommunityMemberStatus status : statusList) {
				disjunction.add(Restrictions.eq("membersAlias.status", status));
			}
			criteria.add(disjunction);
			//criteria.add(Restrictions.eq("membersAlias.status", status));
		}
		if (StringUtils.hasLength(query)) {
			criteria.add(Restrictions.ilike("name", query, MatchMode.ANYWHERE));
		}
		if (accessType != null) {
			criteria.add(Restrictions.eq("accessType", accessType));
		}
		if (activityScopeId != null) {
			criteria.createAlias("rameraActivityScopes", "rameraActivityScopesAlias");
			criteria.add(Restrictions.eq("rameraActivityScopesAlias.id", activityScopeId));
		}
		if (checkParent) {
			if (parent == null) {
				criteria.add(Restrictions.isNull("parent"));
			} else {
				criteria.add(Restrictions.eq("parent", parent));
			}
		}
		if (deleted != null) {
			criteria.add(Restrictions.eq("deleted", deleted));
		}
		return criteria;
	}

	public List<CommunityEntity> getList(Long userId, boolean isAdmin, List<CommunityMemberStatus> statusList, Boolean creator, int firstResult, int maxResults, String query, CommunityAccessType accessType, String communityType, Long activityScopeId, CommunityEntity parent, boolean checkParent, Boolean deleted, String orderBy, boolean asc) {
		Criteria criteria = getListCriteria(userId, isAdmin, statusList, creator, query, accessType, activityScopeId, parent, checkParent, deleted);

		if (communityType != null) {
			if (!communityType.equals("")) {
				FieldEntity field = fieldDao.getByInternalName("COMMUNITY_TYPE");
				Criteria ownerCriteria = getCurrentSession().createCriteria(FieldValueEntity.class);
				ownerCriteria.setProjection(Property.forName("object.id"));
				ownerCriteria.add(Restrictions.eq("field", field));
				ownerCriteria.add(Restrictions.eq("stringValue", communityType));
				criteria.add(Restrictions.in("id", ownerCriteria.list()));
			}
		}
		criteria.setFirstResult(firstResult).setMaxResults(maxResults).addOrder(asc ? Order.asc(orderBy) : Order.desc(orderBy));
		return find(criteria);
	}

	public long getListCount(Long userId, boolean isAdmin, List<CommunityMemberStatus> statusList, Boolean creator, String query, CommunityAccessType accessType, String communityType, Long activityScopeId, CommunityEntity parent, boolean checkParent, Boolean deleted) {
		Criteria criteria = getListCriteria(userId, isAdmin, statusList, creator, query, accessType, activityScopeId, parent, checkParent, deleted);
		if (communityType != null) {
			if (!communityType.equals("")) {
				FieldEntity field = fieldDao.getByInternalName("COMMUNITY_TYPE");
				Criteria ownerCriteria = getCurrentSession().createCriteria(FieldValueEntity.class);
				ownerCriteria.setProjection(Property.forName("object.id"));
				ownerCriteria.add(Restrictions.eq("field", field));
				ownerCriteria.add(Restrictions.eq("stringValue", communityType));
				criteria.add(Restrictions.in("id", ownerCriteria.list()));
			}
		}
		criteria.setProjection(Projections.rowCount());
		return (long) criteria.uniqueResult();
	}

	public CommunityEntity getBySeoLink(String seoLink) {
		FieldEntity field = fieldDao.getByInternalName("COMMUNITY_SHORT_LINK_NAME");
		List<FieldValueEntity> fieldValues = fieldValueDao.getList(field, seoLink);
		List<Long> ids = new ArrayList<>();

		for (FieldValueEntity fieldValue : fieldValues) {
			ids.add(fieldValue.getObject().getId());
		}
		fieldValues.clear();

		CommunityEntity community = null;
		if (ids.size() > 0) {
			community = (CommunityEntity) getCriteria()
					.add(Restrictions.in("id", ids))
					.addOrder(Order.asc("deleted"))
					.addOrder(Order.desc("createdAt"))
					.setMaxResults(1).uniqueResult();
		}

		return community;
	}

	@SuppressWarnings("unchecked")
	public List<CommunityEntity> findByOkved(Collection<OkvedEntity> okveds) {
		final List<Long> okvedIds = new ArrayList<>(okveds.size());
		for (OkvedEntity o : okveds) {
			okvedIds.add(o.getId());
		}
		return createQuery("SELECT c FROM communities c INNER JOIN c.okveds o WHERE o.id IN (:okveds)").setParameterList("okveds", okvedIds).list();
	}

	public List<CommunityEntity> getParents(CommunityEntity community) {
		LinkedList<CommunityEntity> parents = new LinkedList<>();
		while (community.getParent() != null) {
			parents.addFirst(community.getParent());
			community = community.getParent();
		}
		return parents;
	}

	/*private static final String HIERARCHY_QUERY = ""
			+ "with recursive tree (id, parent_id, path, level) as ("
			+ "select t1.id, t1.parent_id, cast(t1.id as varchar (50)) as path, 1 from communities t1 where (t1.deleted = false) and (t1.parent_id = :parent_id)"
			+ " union "
			+ "select t2.id, t2.parent_id, cast(tree.path || '->' || t2.id as varchar(50)) ,level + 1 from communities t2 inner join tree on (tree.id = t2.parent_id) where (t2.deleted = false)"
			+ ") select * from tree order by path limit :limit offset :offset";

	public Map<CommunityEntity, Integer> getChildrenHierarchyList(CommunityEntity parent, int firstResult, int maxResults) {
		List<Object[]> list = createSQLQuery(HIERARCHY_QUERY).addScalar("id", LongType.INSTANCE).addScalar("level", IntegerType.INSTANCE).setLong("parent_id", parent.getId()).setInteger("limit", maxResults).setInteger("offset", firstResult).list();
		Map<CommunityEntity, Integer> map = new LinkedHashMap<>();
		List<Long> ids = new ArrayList<>();
		for (Object[] objects : list) {
			ids.add((Long) objects[0]);
		}
		List<CommunityEntity> communities = getByIds(ids);
		for (Object[] objects : list) {
			Long id = (Long) objects[0];
			Integer level = (Integer) objects[1];
			for (CommunityEntity community : communities) {
				if (community.getId().equals(id)) {
					map.put(community, level);
					break;
				}
			}
		}
		return map;
	}*/

	public int getTotalRootCount() {
		return count(Restrictions.isNull("parent"), Restrictions.eq("deleted", false));
	}

	/*public int getCountByAssociationForm(CommunityAssociationForm associationForm, CommunityEntity exclude) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("deleted", false));
		conjunction.add(Restrictions.eq("associationForm", associationForm));
		if (exclude != null) {
			conjunction.add(Restrictions.ne("id", exclude.getId()));
		}
		return count(conjunction);
	}*/

	public CommunityEntity getFirstByAssociationForm(RameraListEditorItem associationForm) {
		Criteria criteria = getCriteria();
		FieldEntity field = fieldDao.getByInternalName("COMMUNITY_ASSOCIATION_FORM");
		Criteria associationFormCriteria = getCurrentSession().createCriteria(FieldValueEntity.class);
		associationFormCriteria.setProjection(Property.forName("object.id"));
		associationFormCriteria.add(Restrictions.eq("field", field));
		associationFormCriteria.add(Restrictions.eq("stringValue", String.valueOf(associationForm.getId())));
		criteria.add(Restrictions.in("id", associationFormCriteria.list()));
		criteria.add(Restrictions.eq("deleted", false));

		return (CommunityEntity)criteria.uniqueResult();
	}

	private static final String QUERY_GET_BY_FIELD =
			"select distinct c.id from communities c " +
					"join community_members cm on c.id = cm.community_id and cm.sharer_id = :sharer_id " +
					"join field_values fv on fv.object_type = 'COMMUNITY' and c.id = fv.object_id " +
					"join fields f on f.internal_name = :internal_name and fv.field_id = f.id " +
					"where fv.string_value = :field_value";

	/**
	 * Получить список объединений по пользователю и полю
	 * @param userEntity
	 * @param fieldInternalName
	 * @param fieldValueString
	 * @return
	 */
	public List<CommunityEntity> getByField(UserEntity userEntity, String fieldInternalName, String fieldValueString) {
		SQLQuery query = createSQLQuery(QUERY_GET_BY_FIELD);
		query.setLong("sharer_id", userEntity.getId());
		query.setString("internal_name", fieldInternalName);
		query.setString("field_value", fieldValueString);
		query.addScalar("id", LongType.INSTANCE);
		return getByIds(query.list());
	}

	private static final String QUERY_GET_BY_MEMBER =
			"select distinct c.id as id from communities c " +
					"join community_members cm on c.id = cm.community_id and cm.sharer_id = :sharer_id";

	/**
	 * Получить список объединений по участнику
	 * @param userId
	 * @return
	 */
	public List<CommunityEntity> getByMember(Long userId) {
		SQLQuery query = createSQLQuery(QUERY_GET_BY_MEMBER);
        query.addScalar("id", LongType.INSTANCE);
		query.setLong("sharer_id", userId);
		return getByIds(query.list());
	}

	public List<CommunityEntity> getPossibleCommunitiesMembers(UserEntity director, CommunityEntity community, String query, int firstResult, int maxResults) {
		Criteria criteria = getCriteria();
		/*Criteria membersCriteria = getCurrentSession().createCriteria(OrganizationCommunityMemberEntity.class);
		membersCriteria.setProjection(Property.forName("organization.id"));
		membersCriteria.add(Restrictions.eq("community", community));*/

		criteria.createAlias("members", "membersAlias");
		//criteria.add(Restrictions.eq("membersAlias.creator", true));
		criteria.add(Restrictions.eq("membersAlias.user", director));
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.ilike("name", query, MatchMode.ANYWHERE));
		criteria.add(Restrictions.ne("id", community.getId()));

		//FieldEntity field = fieldDao.getByInternalName("COMMUNITY_TYPE");
		FieldEntity field = fieldDao.getByInternalName(FieldConstants.COMMUNITY_DIRECTOR_SHARER_ID);
		Criteria directorCommunitiesCriteria = getCurrentSession().createCriteria(FieldValueEntity.class);
		directorCommunitiesCriteria.setProjection(Property.forName("object.id"));
		directorCommunitiesCriteria.add(Restrictions.eq("field", field));
		directorCommunitiesCriteria.add(Restrictions.eq("stringValue", String.valueOf(director.getId())));
		//typeCriteria.add(Restrictions.eq("stringValue", communityType));

		List<CommunityEntity> result;

		List<Long> directorCommunities = directorCommunitiesCriteria.list();
		if (directorCommunities.size() > 0) {
			criteria.add(Restrictions.in("id", directorCommunities));
			criteria.setFirstResult(firstResult).setMaxResults(maxResults).addOrder(Order.asc("name"));
			result = find(criteria);
		} else {
			result = Collections.emptyList();
		}
		return result;
	}

	public long getPossibleCommunitiesMembersCount(Long userId, CommunityEntity community, String query) {
		Criteria criteria = getCriteria();
		/*Criteria membersCriteria = getCurrentSession().createCriteria(OrganizationCommunityMemberEntity.class);
		membersCriteria.setProjection(Property.forName("organization.id"));
		membersCriteria.add(Restrictions.eq("community", community));*/

		criteria.createAlias("members", "membersAlias");
		//criteria.add(Restrictions.eq("membersAlias.creator", true));
		criteria.add(Restrictions.eq("membersAlias.user.id", userId));
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.ilike("name", query, MatchMode.ANYWHERE));
		criteria.add(Restrictions.ne("id", community.getId()));

		//FieldEntity field = fieldDao.getByInternalName("COMMUNITY_TYPE");
		FieldEntity field = fieldDao.getByInternalName(FieldConstants.COMMUNITY_DIRECTOR_SHARER_ID);
		Criteria directorCommunitiesCriteria = getCurrentSession().createCriteria(FieldValueEntity.class);
		directorCommunitiesCriteria.setProjection(Property.forName("object.id"));
		directorCommunitiesCriteria.add(Restrictions.eq("field", field));
		directorCommunitiesCriteria.add(Restrictions.eq("stringValue", String.valueOf(userId)));

		long result = 0l;

		List<Long> directorCommunities = directorCommunitiesCriteria.list();
		if (directorCommunities.size() > 0) {
			criteria.add(Restrictions.in("id", directorCommunities));
			criteria.setProjection(Projections.rowCount());
			result = (long) criteria.uniqueResult();
		}

		return result;
	}

	private static final String VERIFIED_COMMUNITIES_BY_SHARER_COUNT_SQL = "SELECT COUNT(*)\n" +
			"FROM communities C\n" +
			"WHERE C.verifier_id = :sharer_id and C.deleted=false AND C.verified=true;";

	/**
	 *Возвращает количество сертифицированных sharer'ом объединений
	 * @param userId
	 * @return
	 */
	public Long getVerifiedCommunitiesCount(Long userId) {
		Query query = getCurrentSession().createSQLQuery(VERIFIED_COMMUNITIES_BY_SHARER_COUNT_SQL).setLong("sharer_id", userId);
		return ((BigInteger)query.uniqueResult()).longValue();
	}

	// Возвращает список сертифицированных шарером пользователей(только нужную инфу)
	public List<CommunityEntity> getVerifiedCommunities(Long userId) {
		return find(Restrictions.eq("deleted", false), Restrictions.eq("verifier.id", userId));
	}

	// Получить все объединения, которые не удалены
	public List<CommunityEntity> getNotDeletedCommunities() {
		return find(Restrictions.eq("deleted", false));
	}

	// Загрузить список объединений по полям
	private static final String GET_COMMUNITIES_BY_FIELDS_SQL =
			"select c.id from communities c " +
					"where c.deleted = false ";
	private static final String FIELD_EXISTS_SQL =
			" and exists (" +
					"	select 1 from field_values fv " +
					"	join fields f on f.internal_name = :internal_name and fv.field_id = f.id " +
					"	where fv.object_type = 'COMMUNITY' and c.id = fv.object_id and fv.string_value = :string_value " +
					")";

	/**
	 * Получить объединения по набору полей
	 * @param fieldValueParameters
	 * @return
	 */
	public List<CommunityEntity> getByFields(FieldValueParameterDto... fieldValueParameters) {
		StringBuilder sqlQuerySB = new StringBuilder(GET_COMMUNITIES_BY_FIELDS_SQL);
		SQLQuery query;
		if (fieldValueParameters != null) {
			int i = 0;
			for (FieldValueParameterDto fieldValueParameterDto : fieldValueParameters) {
				String fieldExistsSql = FIELD_EXISTS_SQL;
				fieldExistsSql = fieldExistsSql.replaceAll(":internal_name", ":internal_name_" + i);
				fieldExistsSql = fieldExistsSql.replaceAll(":string_value", ":string_value_" + i);
				sqlQuerySB.append(fieldExistsSql);
				i++;
			}
			query = createSQLQuery(sqlQuerySB.toString());
			i = 0;
			for (FieldValueParameterDto fieldValueParameterDto : fieldValueParameters) {
				query.setString("internal_name_" + i, fieldValueParameterDto.getInternalName());
				query.setString("string_value_" + i, fieldValueParameterDto.getStringValue());
				i++;
			}
		} else {
			query = createSQLQuery(sqlQuerySB.toString());
		}
		query.addScalar("id", LongType.INSTANCE);
		return getByIds(query.list());
	}

	public Long findCommunityId(String seoLink) {
		Long result = null;
		Long communityId = VarUtils.getLong(seoLink, -1l);
		SQLQuery query = createSQLQuery(
				"select c.id from communities c \n" +
				"where c.id = :community_id or exists(select fv.id \n" +
				"from field_values fv \n" +
				"join fields f on fv.field_id = f.id and  f.internal_name = :seo_link_name \n" +
				"where fv.object_type = :object_type and fv.object_id = c.id and fv.string_value = :seo_link) "
		);
		query.setString("object_type", Discriminators.COMMUNITY);
		query.setString("seo_link_name", FieldConstants.COMMUNITY_SHORT_LINK_NAME);
		query.setLong("community_id", communityId);
		query.setString("seo_link", seoLink);
		query.addScalar("id", LongType.INSTANCE);
		if (query.list() != null && !query.list().isEmpty()) {
			result = (Long)(query.list().get(0));
		}
		return result;
	}
}