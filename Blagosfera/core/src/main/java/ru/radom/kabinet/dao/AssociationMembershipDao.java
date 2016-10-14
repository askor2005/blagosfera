package ru.radom.kabinet.dao;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.AssociationMembership;
import ru.radom.kabinet.model.UserEntity;

import java.util.List;

@Repository("associationMembershipDao")
public class AssociationMembershipDao extends Dao<AssociationMembership> {

	public List<AssociationMembership> getBySharer(UserEntity userEntity) {
		return find(Restrictions.eq("user", userEntity));
	}
	
}
