package ru.askor.blagosfera.data.jpa.specifications.news;

import org.springframework.data.jpa.domain.Specification;
import ru.radom.kabinet.model.common.TagEntity;
import ru.radom.kabinet.model.news.News;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Класс для создания JPA спецификаций, относящихся к фильтрации новостей
 */
public class NewsFilterSpecifications {

    private NewsFilterSpecifications() {
    }

    public static Specification<News> authorIdIs(Long id) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (id == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("authorId"), id);
        };
    }

    public static Specification<News> categoryIdIn(List<Long> ids) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (ids == null || ids.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.isTrue((root.get("category").get("id").in(ids)));
        };
    }


    public static Specification<News> dateBetween(Date dateFrom, Date dateTo) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (dateFrom == null && dateTo == null) {
                return criteriaBuilder.conjunction();
            } else if (dateFrom == null) {
                return criteriaBuilder.lessThan(root.get("date"), dateTo);
            } else if (dateTo == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), dateFrom);
            } else {
                return criteriaBuilder.between(root.get("date"), dateFrom, dateTo);
            }
        };
    }

    public static Specification<News> inScopeWithId(Long scopeId) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (scopeId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("scopeId"), scopeId);
        };
    }

    public static Specification<News> inScopeWithType(String scopeType) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (scopeType == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("scopeType"), scopeType);
        };
    }

    public static Specification<News> whereDeletedIs(Boolean deleted) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (deleted == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("deleted"), deleted);
        };
    }

    public static Specification<News> whereModeratedIs(Boolean moderated) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (moderated == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("moderated"), moderated);
        };
    }

    public static Specification<News> isDeleted(Boolean deleted) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (deleted == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("deleted"), deleted);

        };
    }


    public static Specification<News> hasAnyTag(List<TagEntity> tagEntities) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (tagEntities == null || tagEntities.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            for (TagEntity tagEntity : tagEntities) {
                Predicate predicate = criteriaBuilder.isMember(tagEntity, root.get("tags"));
                predicates.add(predicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}
