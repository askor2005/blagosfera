package ru.radom.kabinet.model;

import ru.askor.blagosfera.domain.user.UserRole;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "roles")
public class Role extends LongIdentifiable {

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "sharers_roles", joinColumns = { @JoinColumn(name = "role_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "sharer_id", nullable = false, updatable = false) })
	private List<UserEntity> userEntities;

	@Column(name = "name", nullable = false, length = 20)
	private String name;

	public List<UserEntity> getUserEntities() {
		return userEntities;
	}

	public void setUserEntities(List<UserEntity> userEntities) {
		this.userEntities = userEntities;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserRole toDomain() {
		UserRole result = new UserRole();
		result.setId(getId());
		result.setName(getName());
		return result;
	}

	public static List<UserRole> toDomainList(List<Role> roles) {
		List<UserRole> result;
		if (roles != null && !roles.isEmpty()) {
			result = new ArrayList<>();
			for (Role role : roles) {
				result.add(role.toDomain());
			}
		} else {
			result = Collections.emptyList();
		}
		return result;
	}

}
