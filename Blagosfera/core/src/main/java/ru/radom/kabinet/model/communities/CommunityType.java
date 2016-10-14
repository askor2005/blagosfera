package ru.radom.kabinet.model.communities;

import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "community_types")
// TODO Удалить
@Deprecated
public class CommunityType extends LongIdentifiable {
	@Column
	private int position;

	@Column(length = 100)
	private String name;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}