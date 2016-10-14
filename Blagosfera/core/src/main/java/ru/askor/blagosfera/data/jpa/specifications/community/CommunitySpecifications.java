package ru.askor.blagosfera.data.jpa.specifications.community;

import org.springframework.data.jpa.domain.Specification;
import ru.radom.kabinet.dao.communities.dto.FieldValueParameterDto;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommunitySpecifications {

    private CommunitySpecifications() {
    }

    public static Specification<CommunityEntity> createdBy(final UserEntity creator) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Expression<UserEntity> createdBy = root.get("creator");
            return criteriaBuilder.equal(createdBy, creator);
        };
    }

    public static Specification<CommunityEntity> notCreatedBy(final UserEntity creator) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Expression<UserEntity> createdBy = root.get("creator");
            return criteriaBuilder.not(criteriaBuilder.equal(createdBy, creator));
        };
    }

    public static Specification<CommunityEntity> isMember(final CommunityEntity subgroup) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Expression<Collection<CommunityEntity>> members = root.get("organizationCommunityMembers");
            return criteriaBuilder.isMember(subgroup, members);
        };
    }

    public static Specification<CommunityEntity> findByFieldValues(FieldValueParameterDto... fieldValueParameters) {
        return (root, cq, cb) -> {
            List<Predicate> mainQueryPredicates = new ArrayList<>();


            List<Subquery<FieldValueEntity>> subqueries = new ArrayList<>();
            if (fieldValueParameters != null) {
                for (FieldValueParameterDto fieldValueParameter : fieldValueParameters) {
                    Subquery<FieldValueEntity> fieldValueSubquery = cq.subquery(FieldValueEntity.class);
                    Root<FieldValueEntity> fieldValueRoot = fieldValueSubquery.from(FieldValueEntity.class);
                    fieldValueSubquery.select(fieldValueRoot);

                    Root<FieldEntity> fieldRoot = cq.from(FieldEntity.class);
                    Join<FieldValueEntity, FieldEntity> fieldValueFieldJoin = fieldValueRoot.join("field");

                    //fieldValueFieldJoin.


                    List<Predicate> subQueryPredicates = new ArrayList<>();
                    subQueryPredicates.add(cb.equal(fieldRoot.get("internalName"), fieldValueParameter.getInternalName()));
                    subQueryPredicates.add(cb.equal(fieldValueRoot.get("stringValue"), fieldValueParameter.getStringValue()));
                    subQueryPredicates.add(cb.equal(fieldValueRoot.get("objectType"), Discriminators.COMMUNITY));
                    subQueryPredicates.add(cb.equal(fieldValueRoot.get("objectId"), root.get("id")));

                    fieldValueSubquery.where(subQueryPredicates.toArray(new Predicate[]{}));
                    subqueries.add(fieldValueSubquery);
                }
            }

            for (Subquery<FieldValueEntity> subquery : subqueries) {
                mainQueryPredicates.add(cb.exists(subquery));
            }
            return cb.and(mainQueryPredicates.toArray(new Predicate[]{}));
        };
    }
}
