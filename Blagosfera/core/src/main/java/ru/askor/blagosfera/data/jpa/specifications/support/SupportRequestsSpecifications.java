package ru.askor.blagosfera.data.jpa.specifications.support;

import org.springframework.data.jpa.domain.Specification;
import ru.askor.blagosfera.data.jpa.entities.support.SupportRequestEntity;
import ru.askor.blagosfera.domain.support.SupportRequest;
import ru.askor.blagosfera.domain.support.SupportRequestStatus;
import ru.radom.kabinet.model.ContactEntity;

/**
 * Created by vtarasenko on 19.05.2016.
 */
public class SupportRequestsSpecifications {
    public static Specification<SupportRequestEntity> status(SupportRequestStatus status) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"),status);
    }
    public static Specification<SupportRequestEntity> all() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.conjunction();
    }
}
