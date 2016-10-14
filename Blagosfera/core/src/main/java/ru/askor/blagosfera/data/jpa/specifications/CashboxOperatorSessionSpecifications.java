package ru.askor.blagosfera.data.jpa.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.radom.kabinet.model.cashbox.CashboxOperatorSessionEntity;

import javax.persistence.criteria.Expression;
import java.util.Date;
import java.util.List;

public class CashboxOperatorSessionSpecifications {

    private CashboxOperatorSessionSpecifications() {
    }

    public static Specification<CashboxOperatorSessionEntity> workplaceIdIn(List<String> workplaceIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if ((workplaceIds == null) || workplaceIds.isEmpty()) return criteriaBuilder.disjunction();

            Expression<String> workplaceIdExpr = root.get("workplaceId");
            return criteriaBuilder.isTrue(workplaceIdExpr.in(workplaceIds));
        };
    }

    public static Specification<CashboxOperatorSessionEntity> operatorLike(String operator) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if ((operator == null) || operator.isEmpty()) return criteriaBuilder.conjunction();

            Expression<String> operatorExpr = root.get("operator").get("searchString");
            return criteriaBuilder.like(criteriaBuilder.upper(operatorExpr), "%" + operator.toUpperCase() + "%");
        };
    }

    public static Specification<CashboxOperatorSessionEntity> createdDateGreaterThan(Date date) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (date == null) return criteriaBuilder.conjunction();

            Expression<Date> createdDateExpr = root.get("createdDate");
            return criteriaBuilder.greaterThanOrEqualTo(createdDateExpr, date);
        };
    }

    public static Specification<CashboxOperatorSessionEntity> createdDateLessThan(Date date) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (date == null) return criteriaBuilder.conjunction();

            Expression<Date> createdDateExpr = root.get("createdDate");
            return criteriaBuilder.lessThanOrEqualTo(createdDateExpr, date);
        };
    }

    public static Specification<CashboxOperatorSessionEntity> activeState(String active) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (active == null) return criteriaBuilder.conjunction();

            Expression<Boolean> activeExpr = root.get("active");

            if (active.equals("active")) return criteriaBuilder.equal(activeExpr, true);
            if (active.equals("closed")) return criteriaBuilder.equal(activeExpr, false);

            return criteriaBuilder.conjunction();
        };
    }
}
