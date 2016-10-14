package ru.radom.kabinet.model.communities;

import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.community.log.CommunityLogEvent;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "community_log_events")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "community_event")
public class CommunityLogEventEntity extends LongIdentifiable {

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@JoinColumn(name = "sharer_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private UserEntity userEntity;

	@JoinColumn(name = "community_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private CommunityEntity community;

	@Column(name = "type", nullable = false)
	private CommunityEventType type;

	public CommunityLogEventEntity() {
		
	}
	
	public CommunityLogEventEntity(Date date, UserEntity userEntity, CommunityEntity community, CommunityEventType type) {
		super();
		this.date = date;
		this.userEntity = userEntity;
		this.community = community;
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public UserEntity getUserEntity() {
		return userEntity;
	}

	public void setUserEntity(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	public CommunityEntity getCommunity() {
		return community;
	}

	public void setCommunity(CommunityEntity community) {
		this.community = community;
	}

	public CommunityEventType getType() {
		return type;
	}

	public void setType(CommunityEventType type) {
		this.type = type;
	}

	public CommunityLogEvent toDomain() {
		CommunityLogEvent result = new CommunityLogEvent();
		result.setId(getId());
		if (getCommunity() != null) {
			result.setCommunity(getCommunity().toDomain());
		}
		result.setDate(getDate());
		result.setType(getType());
		if (getUserEntity() != null) {
			result.setUser(getUserEntity().toDomain());
		}
		return result;
	}

	public static CommunityLogEvent toDomainSafe(CommunityLogEventEntity entity) {
		CommunityLogEvent result = null;
		if (entity != null) {
			result = entity.toDomain();
		}
		return result;
	}

	public static List<CommunityLogEvent> toDomainList(List<CommunityLogEventEntity> entities) {
		List<CommunityLogEvent> result = null;
		if (entities != null) {
			result = new ArrayList<>();
			for (CommunityLogEventEntity entity : entities) {
				result.add(toDomainSafe(entity));
			}
		}
		return result;
	}
}
