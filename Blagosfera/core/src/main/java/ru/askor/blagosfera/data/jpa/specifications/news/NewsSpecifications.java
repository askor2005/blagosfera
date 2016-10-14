package ru.askor.blagosfera.data.jpa.specifications.news;

import org.springframework.data.jpa.domain.Specification;
import ru.radom.kabinet.model.common.TagEntity;
import ru.radom.kabinet.model.news.News;

import java.util.List;

/**
 * Класс для создания JPA спецификаций, относящихся к новостям
 */
public class NewsSpecifications {

    private NewsSpecifications() {}

    public static Specification<News> scopeIdIn(List<Long> ids) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (ids == null || ids.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.isTrue(root.get("scopeId").in(ids));
        };
    }

    public static Specification<News> idLessThan(Long id) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (id == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThan(root.get("id"), id);
        };
    }


    public static Specification<News> hasTag(TagEntity tagEntity) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (tagEntity == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.isMember(tagEntity, root.get("tags"));
        };
    }

}