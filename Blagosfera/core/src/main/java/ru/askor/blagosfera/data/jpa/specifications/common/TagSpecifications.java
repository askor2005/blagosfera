package ru.askor.blagosfera.data.jpa.specifications.common;

import org.springframework.data.jpa.domain.Specification;
import ru.radom.kabinet.model.common.TagEntity;

import javax.persistence.criteria.Expression;

/**
 * Класс для создания JPA спецификаций, относящихся к тегам
 */
public class TagSpecifications {


    public static Specification<TagEntity> startsWithInLowerCase(String term) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (term == null) {
                return criteriaBuilder.conjunction();
            }

            Expression<String> lowerTextExpression = criteriaBuilder.lower(root.get("text"));
            return criteriaBuilder.like(lowerTextExpression, term.toLowerCase() + "%");

        };
    }


    public static Specification<TagEntity> usagesCountGtOrEqThan(Long usageCount) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (usageCount == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(root.get("usageCount"), usageCount);
        };
    }
}
