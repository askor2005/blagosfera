package ru.radom.kabinet.hibernate;

import org.hibernate.envers.DefaultRevisionEntity;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "revinfo")
@org.hibernate.envers.RevisionEntity(RevisionListener.class)
public class RevisionEntity extends DefaultRevisionEntity {

	@JoinColumn(name = "sharer_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private UserEntity userEntity;

	public UserEntity getSharer() {
		return userEntity;
	}

	public void setSharer(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

}
