package ru.askor.blagosfera.data.jpa.specifications.invite;

import org.springframework.data.jpa.domain.Specification;
import ru.radom.kabinet.model.invite.InvitationEntity;

import java.util.Date;

/**
 * Created by vtarasenko on 15.04.2016.
 */
public class InviteSpecifications {
    public static Specification<InvitationEntity> emailEqual(String email){
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"),email);
    }
    public static Specification<InvitationEntity> statusEqual(Integer status){
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"),status);
    }
    public static Specification<InvitationEntity> expireDateGreater(Date date){
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThan(root.<Date>get("expireDate"),date);
    }
}
