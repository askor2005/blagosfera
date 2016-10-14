package ru.askor.blagosfera.data.jpa.specifications.contacts;

import org.springframework.data.jpa.domain.Specification;
import ru.radom.kabinet.model.ContactEntity;
import ru.radom.kabinet.model.ContactStatus;
import ru.radom.kabinet.model.ContactsGroupEntity;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;

/**
 * Created by vtarasenko on 08.04.2016.
 */
public class ContactSpecifications {

    private ContactSpecifications() {

    }

    public static Specification<ContactEntity> allContacts() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    public static Specification<ContactEntity> otherSearchStringLike(String search) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Expression<String> otherSearchStringExpression = root.get("other").get("searchString");
            return criteriaBuilder.like(criteriaBuilder.upper(otherSearchStringExpression), "%" + search.toUpperCase() + "%");
        };
    }

    public static Specification<ContactEntity> userEquals(UserEntity user) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<ContactEntity> otherEquals(UserEntity other) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("other"), other);
    }

    public static Specification<ContactEntity> contactsGroupEquals(ContactsGroupEntity contactsGroup) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (contactsGroup != null) {
                return criteriaBuilder.equal(root.join("contactsGroups", JoinType.INNER).get("id"),contactsGroup.getId());
            }
            else {
                return criteriaBuilder.isNull(root.join("contactsGroups", JoinType.LEFT).get("id"));
            }
        };
    }

    public static Specification<ContactEntity> sharerContactStatusEquals(ContactStatus sharerStatus) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("sharerStatus"), sharerStatus);
    }

    public static Specification<ContactEntity> otherStatusEquals(ContactStatus otherStatus) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("otherStatus"), otherStatus);
    }

    public static Specification<ContactEntity> otherDeleted(boolean deleted) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("other").get("deleted"), deleted);
    }
}
