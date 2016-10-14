package ru.askor.blagosfera.data.jpa.specifications.transactions;

import org.springframework.data.jpa.domain.Specification;
import ru.askor.blagosfera.data.jpa.entities.account.AccountEntity;
import ru.askor.blagosfera.data.jpa.entities.account.TransactionDetailEntity;
import ru.askor.blagosfera.data.jpa.entities.account.TransactionEntity;
import ru.askor.blagosfera.domain.account.TransactionDetailType;
import ru.askor.blagosfera.domain.account.TransactionState;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.communities.CommunityPermissionEntity;
import ru.radom.kabinet.model.communities.CommunityPostEntity;
import ru.radom.kabinet.web.admin.dto.TransactionPlainModel;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.Date;
import java.util.List;

/**
 * Created by vtarasenko on 25.04.2016.
 */
public class TransactionSpecifications {

    public static Specification<TransactionEntity> submitDateFrom(Date minDate) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("submitDate"), minDate);
    }

    public static Specification<TransactionEntity> submitDateTo(Date maxDate) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.<Date>get("submitDate"), maxDate);
    }

    public static Specification<TransactionEntity> stateIs(TransactionState state) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("state"), state);
    }

    public static Specification<TransactionEntity> typeIs(TransactionPlainModel.Type type, List<Long> accountIds, Long accountTypeId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (accountIds.isEmpty()) { //если список пустой, иначе вылетит ResultSetException
                accountIds.add(-1L);
            }

            Subquery<TransactionDetailEntity> detailsSubquery = criteriaQuery.subquery(TransactionDetailEntity.class);
            Root<TransactionDetailEntity> detailsSubqueryRoot = detailsSubquery.from(TransactionDetailEntity.class);

            Predicate first = detailsSubqueryRoot.get("account").get("id").in(accountIds);

            Predicate second = type != null ?
                    criteriaBuilder.equal(detailsSubqueryRoot.get("type"), type == TransactionPlainModel.Type.CREDIT ?
                            TransactionDetailType.CREDIT :
                            TransactionDetailType.DEBIT) :
                    criteriaBuilder.conjunction();

            Predicate third = criteriaBuilder.equal(root.get("id"), detailsSubqueryRoot.get("transaction").get("id"));

            Predicate forth = accountTypeId != null ?
                    criteriaBuilder.equal(detailsSubqueryRoot.join("account").get("type").get("id"), accountTypeId) :
                    criteriaBuilder.conjunction();

            detailsSubquery.select(detailsSubqueryRoot).where(criteriaBuilder.and(first, second, third, forth));

            return criteriaBuilder.exists(detailsSubquery);
        };
    }
}
