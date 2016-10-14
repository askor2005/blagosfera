package ru.radom.kabinet.model.notifications;

import ru.askor.blagosfera.domain.notification.NotificationLink;
import ru.askor.blagosfera.domain.notification.NotificationLinkType;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notification_links")
public class NotificationLinkEntity extends LongIdentifiable {

	@JoinColumn(name = "notification_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private NotificationEntity notification;

	@Column(length = 100, nullable = false)
	private String title;

	@Column(length = 100, nullable = true)
	private String url;

	@Column(nullable = false)
	private boolean ajax;
	
	@Column(name = "mark_as_read", nullable = false)
	private boolean makrAsRead;

	@Column(nullable = false)
	private NotificationLinkType type;

	@Column(nullable = false)
	private int position;

	public NotificationLinkEntity() {

	}

	public NotificationLinkEntity(NotificationEntity notification, String title, String url, boolean ajax, boolean makrAsRead, NotificationLinkType type, int position) {
		super();
		this.notification = notification;
		this.title = title;
		this.url = url;
		this.ajax = ajax;
		this.makrAsRead = makrAsRead;
		this.type = type;
		this.position = position;
	}

	public NotificationLinkEntity(String title, String url, boolean ajax, boolean makrAsRead, NotificationLinkType type, int position) {
		super();
		this.title = title;
		this.url = url;
		this.ajax = ajax;
		this.makrAsRead = makrAsRead;
		this.type = type;
		this.position = position;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isAjax() {
		return ajax;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public boolean isMakrAsRead() {
		return makrAsRead;
	}

	public void setMakrAsRead(boolean makrAsRead) {
		this.makrAsRead = makrAsRead;
	}

	public NotificationLinkType getType() {
		return type;
	}

	public void setType(NotificationLinkType type) {
		this.type = type;
	}

	public NotificationEntity getNotification() {
		return notification;
	}

	public void setNotification(NotificationEntity notification) {
		this.notification = notification;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	/*@Override
	public NotificationLinkEntity clone() {
		return new NotificationLinkEntity(getNotification(), getTitle(), getUrl(), isAjax(), isMakrAsRead(), getType(), getPosition());
	}*/

	public NotificationLink toDomain() {
		NotificationLink result = new NotificationLink();
		result.setId(getId());
		result.setPosition(getPosition());
		result.setAjax(isAjax());
		result.setMarkAsRead(isMakrAsRead());
		result.setTitle(getTitle());
		result.setType(getType());
		result.setUrl(getUrl());
		return result;
	}

	public static NotificationLink toDomainSafe(NotificationLinkEntity entity) {
		NotificationLink result = null;
		if (entity != null) {
			result = entity.toDomain();
		}
		return result;
	}

	public static List<NotificationLink> toDomainList(List<NotificationLinkEntity> entities) {
		List<NotificationLink> result = null;
		if (entities != null) {
			result = new ArrayList<>();
			for (NotificationLinkEntity entity : entities) {
				result.add(entity.toDomain());
			}
		}
		return result;
	}

}
