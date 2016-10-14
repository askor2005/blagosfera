package ru.radom.kabinet.model;

import javax.persistence.*;

@Entity
@Table(name = "association_memberships")
public class AssociationMembership extends LongIdentifiable {

	@JoinColumn(name = "association_id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Association association;

	@JoinColumn(name = "sharer_id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private UserEntity userEntity;

	public Association getAssociation() {
		return association;
	}

	public void setAssociation(Association association) {
		this.association = association;
	}

	public UserEntity getSharer() {
		return userEntity;
	}

	public void setSharer(UserEntity userEntity) {
		this.userEntity = userEntity;
	}
	
	
	
}
