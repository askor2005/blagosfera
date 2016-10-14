package ru.radom.kabinet.model.communities;

import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "community_activity_scopes")
//TODO Удалить
@Deprecated
public class CommunityActivityScope extends LongIdentifiable {

	@Column
	private String name;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "communities_activity_scopes", joinColumns = { @JoinColumn(name = "activity_scope_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "community_id", nullable = false, updatable = false) })
	private List<CommunityEntity> communities;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
