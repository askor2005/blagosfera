package ru.askor.blagosfera.data.jpa.specifications.community;

import org.springframework.data.jpa.domain.Specification;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.communities.CommunityPermissionEntity;
import ru.radom.kabinet.model.communities.CommunityPostEntity;

import javax.persistence.criteria.*;

/**
 * Created by Maxim Nikitin on 01.03.2016.
 */
public class CommunityMemberSpecifications {

    private CommunityMemberSpecifications() {}

    /**
     * проверяет принадлежит ли мембер юзеру
     * @param userId
     * @return
     */
    public static Specification<CommunityMemberEntity> userId(Long userId) {
        return (root, query, builder) -> {
            Expression<Long> userIdExpr = root.get("user").get("id");
            return builder.equal(userIdExpr, userId);
        };
    }

    /**
     * проверяет id родительского объединения
     * @param parentId
     * @return
     */
    public static Specification<CommunityMemberEntity> parentCommunityId(Long parentId) {
        return (root, query, builder) -> {
            Expression<Long> parentIdExpr = root.get("community").get("parent").get("id");
            return builder.equal(parentIdExpr, parentId);
        };
    }

    /**
     * проверяет что объединение не является подгруппой
     * @return
     */
    public static Specification<CommunityMemberEntity> noParent() {
        return (root, query, builder) -> {
            Expression<CommunityEntity> parentExpr = root.get("community").get("parent");
            return builder.isNull(parentExpr);
        };
    }

    /**
     * проверяет существует ли заданная роль в постах мембера
     * @param permission
     * @return
     */
    public static Specification<CommunityMemberEntity> hasPermission(String permission) {
        return (root, query, builder) -> {
            // джойним посты к мемберу
            Join<CommunityMemberEntity, CommunityPostEntity> joinMemberPost = root.join("posts");

            // джойним роли к постам
            Join<CommunityPostEntity, CommunityPermissionEntity> joinPostPermission = joinMemberPost.join("permissions");

            // получить все посты из мембера
            Subquery<CommunityPostEntity> postsSubquery = query.subquery(CommunityPostEntity.class);
            Root<CommunityPostEntity> postsSubqueryRoot = postsSubquery.from(CommunityPostEntity.class);
            postsSubquery.select(postsSubqueryRoot);
            Predicate postInMember = postsSubqueryRoot.in(joinMemberPost);
            postsSubquery.where(postInMember);

            // найти заданную роль в постах
            Subquery<CommunityPermissionEntity> permissionsSubquery = postsSubquery.subquery(CommunityPermissionEntity.class);
            Root<CommunityPermissionEntity> permissionsSubqueryRoot = permissionsSubquery.from(CommunityPermissionEntity.class);
            permissionsSubquery.select(permissionsSubqueryRoot);
            Predicate permissionInPosts = permissionsSubqueryRoot.in(joinPostPermission);
            Predicate permissionNameIs = builder.equal(permissionsSubqueryRoot.get("name"), permission);
            /*permissionsSubquery.where(
                    builder.or(
                            builder.and(permissionInPosts, permissionNameIs),
                            builder.equal(root.get("creator"), true)
                    )
            );*/
            query.distinct(true);
            permissionsSubquery.where(builder.and(permissionInPosts, permissionNameIs));

            // существует ли в постах нужная роль
            return builder.or(builder.exists(permissionsSubquery), builder.equal(root.get("creator"), true));
            //return builder.exists(permissionsSubquery);
        };
    }
}
