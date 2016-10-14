package ru.radom.kabinet.model.communities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.community.CommunityPost;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "community_members")
@Table(name = "community_members")
public class CommunityMemberEntity extends LongIdentifiable {

	@JoinColumn(name = "sharer_id", nullable = false)
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private UserEntity user;

	@JoinColumn(name = "inviter_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private UserEntity inviter;

	@JsonIgnore
	@JoinColumn(name = "community_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private CommunityEntity community;

	@Column(nullable = false)
	private CommunityMemberStatus status;

	@Column(name = "creator", nullable = false)
	private boolean creator;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "community_members_posts", joinColumns = { @JoinColumn(name = "member_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "post_id", nullable = false, updatable = false) })
	private List<CommunityPostEntity> posts;

	@Column(name = "request_date", nullable = true)
	private Date requestDate;

	public CommunityMemberEntity() {

	}

	public CommunityMemberEntity(UserEntity user, UserEntity inviter, CommunityEntity community, CommunityMemberStatus status) {
		this(user, inviter, community, status, false);
	}

	public CommunityMemberEntity(UserEntity user, UserEntity inviter, CommunityEntity community, CommunityMemberStatus status, boolean creator) {
		super();
		this.user = user;
		this.inviter = inviter;
		this.community = community;
		this.status = status;
		this.creator = creator;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public UserEntity getInviter() {
		return inviter;
	}

	public void setInviter(UserEntity inviter) {
		this.inviter = inviter;
	}

	public CommunityEntity getCommunity() {
		return community;
	}

	public void setCommunity(CommunityEntity community) {
		this.community = community;
	}

	public CommunityMemberStatus getStatus() {
		return status;
	}

	public void setStatus(CommunityMemberStatus status) {
		this.status = status;
	}

	public boolean isCreator() {
		return creator;
	}

	public void setCreator(boolean creator) {
		this.creator = creator;
	}

	public List<CommunityPostEntity> getPosts() {
		return posts;
	}

	public void setPosts(List<CommunityPostEntity> posts) {
		this.posts = posts;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public CommunityMember toDomain() {
		return toDomain(true, true, true, true);
	}

	public CommunityMember toDomain(boolean withInviter, boolean withUser, boolean withCommunity, boolean withPosts) {
		CommunityMember result = new CommunityMember();
		result.setId(getId());
		result.setUser(getUser().toDomain());
		result.setStatus(getStatus());
		result.setInviter(getInviter() != null ? getInviter().toDomain() : null);
		result.setCreator(isCreator());
		result.setRequestDate(getRequestDate());
		if (withPosts && getPosts() != null) {
			List<CommunityPostEntity> communityPosts = getPosts();
			List<CommunityPost> communityPostsList = new ArrayList<>();
			for (CommunityPostEntity post : communityPosts) {
				communityPostsList.add(post.toDomain(false, false, false, false));
			}
			result.setPosts(communityPostsList);
		}
		if (withInviter && getInviter() != null) {
			result.setInviter(getInviter().toDomain());
		}
		if (withUser && getUser() != null) {
			result.setUser(getUser().toDomain());
		}
		if (withCommunity && getCommunity() != null) {
			result.setCommunity(getCommunity().toDomain());
		}

		return result;
	}

	public static CommunityMember toDomainSafe(CommunityMemberEntity entity, boolean withInviter, boolean withUser, boolean withCommunity, boolean withPosts) {
		CommunityMember result = null;
		if (entity != null) {
			result = entity.toDomain(withInviter, withUser, withCommunity, withPosts);
		}
		return result;
	}

	public static List<CommunityMember> toDomainList(List<CommunityMemberEntity> members) {
		List<CommunityMember> result = null;
		if (members != null) {
			result = new ArrayList<>();
			for (CommunityMemberEntity member : members) {
				result.add(member.toDomain(true, true, true, true));
			}
		}
		return result;
	}


}
