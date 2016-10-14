package ru.radom.kabinet.dto;

import lombok.Getter;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.user.User;

import java.util.Map;

@Getter
public class CommunityDto {

	private Community community;
	private CommunityMember member;
	private User user;
	private int level;
	private Map<String, Object> parameters;
	private String eventType;

	public CommunityDto(Community community, CommunityMember member, User user, String eventType) {
		this(community, member, user, 0, eventType);
	}

	public CommunityDto(Community community, CommunityMember member, User user, int level, String eventType) {
		super();

		if (member == null && user == null) {
			throw new IllegalArgumentException("member and sharer cannot be both null");
		}

		if (member != null && user != null && !member.getUser().getId().equals(user.getId())) {
			throw new IllegalArgumentException("member is not corresponding to sharer");
		}

		this.community = community;
		this.member = member;
		this.user = user;
		this.level = level;
		this.eventType = eventType;
	}

	/*public CommunityDto(Community community, CommunityMember member, int level) {
		this(community, member, null, level);
	}

	public CommunityDto(Community community, CommunityMember member) {
		this(community, member, null, 0);
	}

	public CommunityDto(Community community, CommunityMember member, Map<String, Object> parameters) {
		this(community, member, null, 0);
		this.parameters = parameters;
	}
	
	public CommunityDto(Community community, User user, int level) {
		this(community, null, user, level);
	}

	public CommunityDto(Community community, User user) {
		this(community, null, user, 0);
	}*/
	

}
