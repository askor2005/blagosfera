package ru.askor.blagosfera.data.jpa.specifications.community;

import org.springframework.data.jpa.domain.Specification;
import ru.radom.kabinet.model.communities.CommunityDocumentRequestEntity;

import javax.persistence.criteria.Expression;

/**
 *
 * Created by vgusev on 22.07.2016.
 */
public class CommunityDocumentRequestSpecifications {

    public static Specification<CommunityDocumentRequestEntity> userId(Long userId) {
        return (root, query, builder) -> {
            Expression<Long> userIdExpr = root.get("user").get("id");
            return builder.equal(userIdExpr, userId);
        };
    }

    public static Specification<CommunityDocumentRequestEntity> findById(Long id) {
        return (root, query, builder) -> {
            Expression<Long> idExpr = root.get("id");
            return builder.equal(idExpr, id);
        };
    }
}
