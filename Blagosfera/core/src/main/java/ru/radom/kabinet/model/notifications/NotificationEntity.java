package ru.radom.kabinet.model.notifications;

import org.hibernate.annotations.*;
import ru.askor.blagosfera.domain.notification.Notification;
import ru.askor.blagosfera.domain.notification.NotificationPriority;
import ru.askor.blagosfera.domain.notification.NotificationSender;
import ru.askor.blagosfera.domain.notification.NotificationType;
import ru.radom.kabinet.model.ContactEntity;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "notifications")
public class NotificationEntity extends LongIdentifiable implements Cloneable {

	@JoinColumn(name = "sharer_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private UserEntity user;

	@Any(metaColumn = @Column(name = "sender_type", length = 50), fetch = FetchType.LAZY)
	@AnyMetaDef(idType = "long", metaType = "string", metaValues = { @MetaValue(targetEntity = SystemAccountEntity.class, value = Discriminators.SYSTEM_ACCOUNT), @MetaValue(targetEntity = UserEntity.class, value = Discriminators.SHARER), @MetaValue(targetEntity = CommunityEntity.class, value = Discriminators.COMMUNITY) })
	@JoinColumn(name = "sender_id")
	private Object sender;

	@Column(nullable = false)
	private boolean read;

	@Column(nullable = false)
	private NotificationPriority priority;

	@Column(length = 1000, nullable = false)
	private String subject;

	@Column(name = "short_text")
    @Type(type="text")
	private String shortText;

	@Column(length = 2000)
	private String text;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "notification")
	@Fetch(FetchMode.SELECT)
	@OrderBy("position")
	private List<NotificationLinkEntity> links;

	@Column(nullable = true)
	private NotificationType type;

	@Any(metaColumn = @Column(name = "object_type", length = 50), fetch = FetchType.LAZY)
	@AnyMetaDef(idType = "long", metaType = "string", metaValues = { @MetaValue(targetEntity = ContactEntity.class, value = Discriminators.CONTACT), @MetaValue(targetEntity = CommunityMemberEntity.class, value = Discriminators.COMMUNITY_MEMBER) })
	@JoinColumn(name = "object_id")
	private Object object;

    public NotificationEntity() {
    }

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public Object getSender() {
		return sender;
	}

	public void setSender(Object sender) {
		this.sender = sender;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public NotificationPriority getPriority() {
		return priority;
	}

	public void setPriority(NotificationPriority priority) {
		this.priority = priority;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getShortText() {
		return shortText;
	}

	public void setShortText(String shortText) {
		this.shortText = shortText;
	}

	public List<NotificationLinkEntity> getLinks() {
		return links;
	}

	public void setLinks(List<NotificationLinkEntity> links) {
		this.links = links;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Notification toDomain() {
		Notification result = new Notification();
		result.setId(getId());
		result.setUser(getUser().toDomain());
		result.setSender(getNotificationSender(getSender()));
		result.setRead(isRead());
		result.setPriority(getPriority());
		result.setSubject(getSubject());
		result.setShortText(getShortText());
		result.setText(getText());
		result.setDate(getDate());
		result.getLinks().addAll(NotificationLinkEntity.toDomainList(getLinks()));
		return result;
	}

	public static Notification toDomainSafe(NotificationEntity entity) {
		Notification result = null;
		if (entity != null) {
			result = entity.toDomain();
		}
		return result;
	}

	public static List<Notification> toDomainList(List<NotificationEntity> entities) {
		List<Notification> result = null;
		if (entities != null) {
			result = new ArrayList<>();
			for (NotificationEntity entity : entities) {
				result.add(toDomainSafe(entity));
			}
		}
		return result;
	}

	private NotificationSender getNotificationSender(Object sender) {
		NotificationSender result = null;
		if (sender != null) {
			if (sender instanceof  UserEntity) {
				result = ((UserEntity) sender).toDomain();
			} else if (sender instanceof CommunityEntity) {
				result = ((CommunityEntity) sender).toDomain();
			} else if (sender instanceof SystemAccountEntity) {
				result = ((SystemAccountEntity) sender).toDomain();
			}
		}
		return result;
	}
}
