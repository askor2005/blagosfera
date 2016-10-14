package ru.radom.kabinet.dao.communities;

import org.hibernate.Query;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.communities.CommunityPermissionEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("communityPermissionDao")
public class CommunityPermissionDao extends Dao<CommunityPermissionEntity> {

	public List<CommunityPermissionEntity> getByCommunityId(Long communityId) {
		List<Long> ids = communityId != null ? createSQLQuery(
				QUERY_PERMISSIONS_BY_COMMUNITY)
				.addScalar("id", LongType.INSTANCE)
				.setLong("community_id", communityId)
				.list() : Collections.emptyList();

		return getByIds(ids);
	}

	// Получить все роли по участнику объединения
	private static final String QUERY_PERMISSIONS_BY_MEMBER =
			"select cp.name from community_permissions cp " +
			"join community_posts_permissions cpp on cp.id = cpp.permission_id " +
			"join community_members_posts cmp on cpp.post_id = cmp.post_id " +
			"where cmp.member_id = :member_id";

	public Set<String> getPermissions(CommunityMemberEntity member) {
		return new HashSet<String>(member != null ? createSQLQuery(
				QUERY_PERMISSIONS_BY_MEMBER)
				.addScalar("name", StringType.INSTANCE).setLong("member_id", member.getId()).list() : Collections.emptyList());
	}

	// Получить все роли по объединению
	private static final String QUERY_PERMISSIONS_BY_COMMUNITY =
			"select cp.id as id, cp.name as name from community_permissions cp " +
			"	left join community_permission_association_forms cpaf on cp.id = cpaf.community_permission_id " +
			"	left join community_security_permissions_communities cspc on cp.id = cspc.community_permission_id " +
			"where " +
			"	(cp.security_role = false and (cpaf.association_form_id is null or cpaf.association_form_id = " +
			"		cast(coalesce((" +
			"				select fv.string_value from field_values fv " +
			"				join fields f on fv.field_id = f.id and f.internal_name = 'COMMUNITY_ASSOCIATION_FORM' and fv.string_value != '' and fv.object_id = :community_id), '-1') as int))) or " +
			"	(cp.security_role = true and cspc.community_id = :community_id)";

	public Set<String> getPermissions(CommunityEntity community) {
		return new HashSet<String>(community != null ? createSQLQuery(
				QUERY_PERMISSIONS_BY_COMMUNITY)
				.addScalar("name", StringType.INSTANCE)
				.setLong("community_id", community.getId())
				.list() : Collections.emptyList());
	}

	private static final String QUERY_CHECK_PERMISSION =
			"select distinct cp.id from community_permissions cp " +
			"	join community_posts_permissions cpp on cp.id = cpp.permission_id " +
			"	join community_members_posts cmp on cpp.post_id = cmp.post_id " +
			"	join community_members cm on cmp.member_id = cm.id " +
			"	where cm.community_id = :community_id and cm.sharer_id = :sharer_id and cp.name = :permission_name";


	/**
	 * Проверить, есть ли у пользователя права в объединении
	 * @param community
	 * @param userEntity
	 * @param permissionName
	 * @return
	 */
	public boolean checkPermissionBySharer(CommunityEntity community, UserEntity userEntity, String permissionName) {
		Query query = createSQLQuery(QUERY_CHECK_PERMISSION);
		query.setLong("community_id", community.getId());
		query.setLong("sharer_id", userEntity.getId());
		query.setString("permission_name", permissionName);
		return query.list().size() > 0;
	}
}
