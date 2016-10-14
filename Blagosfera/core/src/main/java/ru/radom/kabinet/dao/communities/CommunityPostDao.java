package ru.radom.kabinet.dao.communities;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.domain.community.CommunityPost;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.dto.community.CommunityUserPost;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityPostEntity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository("communityPostDao")
public class CommunityPostDao extends Dao<CommunityPostEntity> {

	private static final String QUERY_CHECK_SHARER_IN_POST =
			"select count(*) cnt from community_posts cp " +
			"	join community_members_posts cmp on cp.id = cmp.post_id " +
			"	join community_members cm on cmp.member_id = cm.id " +
			"	where cp.community_id = :community_id and cm.sharer_id = :sharer_id and cp.name = :post_name";

	/**
	 * Проверить участника на посту в объединении
	 * @param community
	 * @param postName
	 * @param userEntity
	 * @return
	 */
	public boolean checkMemberOnPost(CommunityEntity community, String postName, UserEntity userEntity) {
		Query query = createSQLQuery(QUERY_CHECK_SHARER_IN_POST);
		query.setLong("community_id", community.getId());
		query.setLong("sharer_id", userEntity.getId());
		query.setString("post_name", postName);
		int count = ((BigInteger)query.uniqueResult()).intValue();
		return count > 0;
	}

	public boolean checkName(CommunityEntity community, String name) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("community", community));
		conjunction.add(Restrictions.eq("name", name));
		return count(conjunction) == 0;
	}

	public boolean checkName(CommunityPost post) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("community.id", post.getCommunity().getId()));
		conjunction.add(Restrictions.eq("name", post.getName()));
		if (post.getId() != null) {
			conjunction.add(Restrictions.ne("id", post.getId()));
		}
		return count(conjunction) == 0;
	}

	public List<CommunityPostEntity> getByCommunityId(Long communityId) {
		return find(Order.asc("position"), Restrictions.eq("community.id", communityId));
	}

	public List<CommunityPostEntity> getByCommunity(CommunityEntity community) {
		return find(Order.asc("position"), Restrictions.eq("community", community));
	}

	public CommunityPostEntity getCeo(CommunityEntity community) {
		return findFirst(Restrictions.eq("community", community), Restrictions.eq("ceo", true));
	}

	private static final String GET_COMMUNITY_POSTS_QUERY =
			"select cp.id, sh.id as sharerId, comm.id as communityId, comm.name as communityName, cp.name as postName from community_members_posts as mp " +
			"	join community_members as cm on mp.member_id = cm.id " +
			"	join community_posts as cp on mp.post_id = cp.id " +
			"	join communities as comm on cm.community_id = comm.id " +
			"	join sharers as sh on cm.sharer_id = sh.id " +
			"		where cm.community_id = :community_id or cm.community_id in " +
			"			(select id from communities as child where child.parent_id = :community_id) " +
			"	order by cm.sharer_id asc";

	/**
	 * Получить список CommunityUserPost объектов по параметрам
	 * @param communityId
	 * @param limit
	 * @param offset
	 * @return
	 */
	public List<CommunityUserPost> getCommunityPosts(Long communityId, int limit, int offset) {
		SQLQuery query = getCurrentSession().createSQLQuery(GET_COMMUNITY_POSTS_QUERY);
		query.setLong("community_id", communityId);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		List<Object> mapList = query.list();
		List<CommunityUserPost> result = new ArrayList<>();
		for (Object item : mapList) {
			Map<String, Object> mapItem = (Map<String, Object>)item;
			CommunityUserPost communityPostDto = new CommunityUserPost();
			communityPostDto.setId(((BigInteger) mapItem.get("id")).longValue());
			communityPostDto.setCommunityId(((BigInteger)mapItem.get("communityid")).longValue());
			communityPostDto.setCommunityName((String)mapItem.get("communityname"));
			communityPostDto.setPostName((String) mapItem.get("postname"));
			communityPostDto.setUserId(((BigInteger) mapItem.get("sharerid")).longValue());
			result.add(communityPostDto);
		}
		return result;
	}

	/**
	 * Получить количество должностей в объединениях.
	 * @param communityId
	 * @return
	 */
	public int getCommunityPostsCount(Long communityId) {
		SQLQuery query = getCurrentSession().createSQLQuery(GET_COMMUNITY_POSTS_QUERY);
		query.setLong("community_id", communityId);
		return query.list().size();
	}

	/**
	 * Получить должность в объединении по названию
     */
	public CommunityPostEntity getByName(CommunityEntity community, String name) {
		return (CommunityPostEntity) getCriteria()
				.add(Restrictions.eq("community", community))
				.add(Restrictions.ilike("name", name.replaceAll("\\s+", "")))
				.uniqueResult();
	}

}
