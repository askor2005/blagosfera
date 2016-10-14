package ru.radom.kabinet.model.communities;

import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.community.log.CommunityLogEvent;
import ru.askor.blagosfera.domain.community.log.CommunityMemberLogEvent;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorValue("community_member_event")
public class CommunityMemberLogEventEntity extends CommunityLogEventEntity {

	@JoinColumn(name = "member_sharer_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private UserEntity memberUserEntity;

	public CommunityMemberLogEventEntity() {

	}

	public CommunityMemberLogEventEntity(Date date, UserEntity userEntity, CommunityEntity community, UserEntity memberUserEntity, CommunityEventType type) {
		super(date, userEntity, community, type);
		this.memberUserEntity = memberUserEntity;
	}

	public UserEntity getMemberUserEntity() {
		return memberUserEntity;
	}

	public void setMemberUserEntity(UserEntity memberUserEntity) {
		this.memberUserEntity = memberUserEntity;
	}

	@Override
	public CommunityMemberLogEvent toDomain() {
		CommunityLogEvent baseLogEvent = super.toDomain();

		CommunityMemberLogEvent result = new CommunityMemberLogEvent();
		result.setId(baseLogEvent.getId());
		result.setCommunity(baseLogEvent.getCommunity());
		result.setDate(baseLogEvent.getDate());
		result.setType(baseLogEvent.getType());
		result.setUser(baseLogEvent.getUser());
		if (getUserEntity() != null) {
			result.setMemberUser(getUserEntity().toDomain());
		}
		return result;
	}

}
