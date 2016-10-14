package ru.radom.kabinet.model.settings;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "sharer_settings")
public class SharerSetting extends LongIdentifiable {

	@JoinColumn(name = "sharer_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private UserEntity user;

	@Column(nullable = false, length = 100)
	private String key;

	@Column(name = "val", nullable = false, length = 100000)
	private String value;

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
