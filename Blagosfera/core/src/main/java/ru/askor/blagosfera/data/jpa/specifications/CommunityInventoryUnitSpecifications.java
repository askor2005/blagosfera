package ru.askor.blagosfera.data.jpa.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.inventory.CommunityInventoryUnitEntity;
import ru.radom.kabinet.model.communities.inventory.CommunityInventoryUnitTypeEntity;

import javax.persistence.criteria.Expression;

public class CommunityInventoryUnitSpecifications {

    private CommunityInventoryUnitSpecifications() {
    }

    public static Specification<CommunityInventoryUnitEntity> ownedBy(final CommunityEntity community) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("community").get("id"), community.getId());
    }

    public static Specification<CommunityInventoryUnitEntity> leasedTo(final CommunityEntity community) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("leasedTo").get("id"), community.getId());
    }

    public static Specification<CommunityInventoryUnitEntity> numberLike(final String number) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (number == null) return criteriaBuilder.conjunction();

            Expression<String> numberExpr = root.get("number");
            return criteriaBuilder.like(criteriaBuilder.lower(numberExpr), "%" + number.toLowerCase() + "%");
        };
    }

    public static Specification<CommunityInventoryUnitEntity> typeIs(final CommunityInventoryUnitTypeEntity type) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (type == null) return criteriaBuilder.conjunction();

            return criteriaBuilder.equal(root.get("type"), type);
        };
    }
}
