package ru.askor.blagosfera.data.jpa.specifications.cms;

import org.springframework.data.jpa.domain.Specification;
import ru.radom.kabinet.model.ContactEntity;
import ru.radom.kabinet.model.web.Section;

import javax.persistence.criteria.Expression;

/**
 * Created by vtarasenko on 25.06.2016.
 */
public class SectionSpecifications {
    public static Specification<Section> conjunction() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.conjunction();
        };
    }
    public static Specification<Section> parent(Section parent) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("parent"), parent);
        };
    }
    public static Specification<Section> isRoot() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.isNull(root.get("parent"));
        };
    }
    public static Specification<Section> showToAdminUsersOnly(boolean showToAdminUsersOnly) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("showToAdminUsersOnly"), showToAdminUsersOnly);
        };
    }
    public static Specification<Section> showToAuthorizedUsersOnly(boolean showToAuthorizedUsersOnly) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("showToAuthorizedUsersOnly"), showToAuthorizedUsersOnly);
        };
    }
    public static Specification<Section> showToVerifiedUsersOnly(boolean showToVerifiedUsersOnly) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("showToVerifiedUsersOnly"),showToVerifiedUsersOnly);
        };
    }
    public static Specification<Section> minRegistratiorLevelToShow(int level) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.greaterThanOrEqualTo(root.get("minRegistratorLevelToShow"), level);
        };
    }
    public static Specification<Section> minRegistratiorLevelToShowIsNull() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.isNull(root.get("minRegistratorLevelToShow"));
        };
    }
}
