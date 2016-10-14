package ru.askor.blagosfera.data.jpa.specifications.registrator;

import org.springframework.data.jpa.domain.Specification;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.criteria.JoinType;

/**
 * спецификации для пользователя поиска регистратора
 */
public class RegistratorSpecifications {
    public static Specification<UserEntity> idIn(Long[] userIds) {
        if (userIds.length == 0) {//если список пустой, иначе вылетит ResultSetException
            userIds = new Long[1];
            userIds[0] = -1l;
        }

        Long[] userIdsFinal = userIds;

        return (root, criteriaQuery, criteriaBuilder) -> root.get("id").in(userIdsFinal);
    }

    public static Specification<UserEntity> hasCommunityPostMnemoStartsWithLike(String mnemo) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.join("members", JoinType.LEFT).join("posts",JoinType.LEFT).get("mnemo"), mnemo + "%");
    }

    public static Specification<UserEntity> hasCommunityPostMnemoEqual(String mnemo) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.join("members",JoinType.LEFT).join("posts",JoinType.LEFT).get("mnemo"), mnemo);
    }

    public static Specification<UserEntity> hasNotCommunityPost(String mnemo) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.notEqual(root.join("members",JoinType.LEFT).join("posts",JoinType.LEFT).get("mnemo"), mnemo);
    }

    public static Specification<UserEntity> userIdNotEquals(Long userId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("id"), userId);
    }

    public static Specification<UserEntity> searchStringLike(String searchString) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get("searchString")), "%" + searchString.toUpperCase() + "%");
    }

    public static Specification<UserEntity> allRegistrators() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    public static Specification<UserEntity> disjunction() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.disjunction();
    }
}