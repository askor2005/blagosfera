package ru.askor.blagosfera.data.jpa.specifications.settings;

import org.springframework.data.jpa.domain.Specification;
import ru.askor.blagosfera.data.jpa.entities.settings.SystemSettingEntity;

import javax.persistence.criteria.Expression;

/**
 * Created by Maxim Nikitin on 10.03.2016.
 */
public class SystemSettingSpec {

    private SystemSettingSpec() {
    }

    public static Specification<SystemSettingEntity> keyLike(String value) {
        return like("key", value);
    }

    public static Specification<SystemSettingEntity> descriptionLike(String value) {
        return like("description", value);
    }

    private static Specification<SystemSettingEntity> like(String key, String value) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (value == null) return criteriaBuilder.conjunction();
            Expression<String> expr = root.get(key);
            return criteriaBuilder.like(criteriaBuilder.lower(expr), "%" + value.toLowerCase() + "%");
        };
    }
}
