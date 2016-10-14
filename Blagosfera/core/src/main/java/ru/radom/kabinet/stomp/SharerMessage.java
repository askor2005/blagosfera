package ru.radom.kabinet.stomp;

import ru.askor.blagosfera.domain.user.User;

public class SharerMessage {
	
	private Long id;
	private String shortName;
	private String avatar;
	private String link;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public SharerMessage(User user) {
		this.id = user.getId();
		this.shortName = user.getShortName();
		this.avatar = user.getAvatar();
		this.link = user.getLink();
	}

}