package ru.radom.kabinet.model.discussion;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.MetaValue;
import ru.askor.blagosfera.domain.discussion.DiscussionDomain;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.radom.kabinet.model.rating.Ratable;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "discussions")
public class Discussion extends LongIdentifiable implements Ratable {

	@Column(name = "created_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	/**
	 * Заголовок обсуждения
	 */
	@Column(length = 255)
	private String title;

	/**
	 * "Начальный комментарий" обсуждения. Должен быть создан одновременно с
	 * обсуждением. Содержит полный текст описания вопроса выносимого на
	 * обсужднение Явлется вершиной дерева коментариев конкретного обсуждения.
	 */
	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(updatable = false, nullable = false)
	private CommentEntity root;

	/**
	 * Автор обсуждения
	 */
	@ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "sharer_id")
	private UserEntity author;

	/**
	 * Область видимости обсуждения
	 */
	@Any(metaColumn = @Column(name = "scope_type", length = 50), fetch = FetchType.LAZY)
	@AnyMetaDef(idType = "long", metaType = "string", metaValues = { @MetaValue(targetEntity = UserEntity.class, value = Discriminators.SHARER), @MetaValue(targetEntity = CommunityEntity.class, value = Discriminators.COMMUNITY) })
	@JoinColumn(name = "scope_id", nullable = true)
	private RadomAccount scope;

	private String recommendations;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "topic_id", foreignKey = @ForeignKey(name = "fk_discussion_topic"))
	private DiscussionTopic topic;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "time_limit")
	private Calendar timeLimit;

	@Column(name = "comments_limit", nullable = true)
	private long commentsLimit = 0L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "mandatory_period", nullable = true)
	private Calendar mandatoryPeriod;

	@Column(name = "publicly_commentable", nullable = true)
	private boolean publiclyCommentable = false;

	@Column(name = "publicly_visible", nullable = true)
	private boolean publiclyVisible = false;

	@Formula(value = "(select coalesce((count(c.id) - 1),0) from comments c where (c.parent_discussion_id = id))")
	private int commentsCount;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "discussions_to_sharers", 
		joinColumns = @JoinColumn(name = "discussion_id"),
		inverseJoinColumns = @JoinColumn(name = "sharer_id")
	)
	private Set<UserEntity> allowedUserEntities = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "discussions_to_communities", 
		joinColumns = @JoinColumn(name = "discussion_id"),
		inverseJoinColumns = @JoinColumn(name = "community_id")
	)
	private Set<CommunityEntity> allowedCommunities = new HashSet<>();

	public Discussion() {
		this.createdAt = new Date();
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public CommentEntity getRoot() {
		return root;
	}

	public void setRoot(CommentEntity root) {
		this.root = root;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public UserEntity getOwner() {
		return author;
	}

	public void setOwner(UserEntity author) {
		this.author = author;
	}

	public void setRecommendations(String recommendations) {
		this.recommendations = recommendations;
	}

	public String getRecommendations() {
		return recommendations;
	}

	public RadomAccount getScope() {
		return scope;
	}

	public void setScope(RadomAccount scope) {
		this.scope = scope;
	}

	public UserEntity getAuthor() {
		return author;
	}

	public void setAuthor(UserEntity author) {
		this.author = author;
	}

	public DiscussionTopic getTopic() {
		return topic;
	}

	public void setTopic(DiscussionTopic topic) {
		this.topic = topic;
	}

	public Calendar getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Calendar timeLimit) {
		this.timeLimit = timeLimit;
	}

	public long getCommentsLimit() {
		return commentsLimit;
	}

	public void setCommentsLimit(long commentsLimit) {
		this.commentsLimit = commentsLimit;
	}

	public Calendar getMandatoryPeriod() {
		return mandatoryPeriod;
	}

	public void setMandatoryPeriod(Calendar mandatoryPeriod) {
		this.mandatoryPeriod = mandatoryPeriod;
	}

	public boolean isPubliclyCommentable() {
		return publiclyCommentable;
	}

	public void setPubliclyCommentable(boolean publiclyCommentable) {
		this.publiclyCommentable = publiclyCommentable;
	}

	public boolean isPubliclyVisible() {
		return publiclyVisible;
	}

	public void setPubliclyVisible(boolean publiclyVisible) {
		this.publiclyVisible = publiclyVisible;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public Set<UserEntity> getAllowedUserEntities() {
		return allowedUserEntities;
	}

	public void setAllowedUserEntities(Set<UserEntity> allowedUserEntities) {
		this.allowedUserEntities = allowedUserEntities;
	}

	public void addAllowedSharer(UserEntity userEntity) {
		this.allowedUserEntities.add(userEntity);
	}
	
	public void removeAllowedSharer(UserEntity userEntity) {
		this.allowedUserEntities.remove(userEntity);
	}
	
	public void clearAllowedSharers() {
		this.allowedUserEntities.clear();
	}
	
	public Set<CommunityEntity> getAllowedCommunities() {
		return allowedCommunities;
	}

	public void setAllowedCommunities(Set<CommunityEntity> allowedCommunities) {
		this.allowedCommunities = allowedCommunities;
	}

	public void addAllowedCommunity(CommunityEntity community) {
		this.allowedCommunities.add(community);
	}
	
	public void removeAllowedCommunity(CommunityEntity community) {
		this.allowedCommunities.remove(community);
	}
	
	public void clearAllowedCommunities() {
		this.allowedCommunities.clear();
	}

	public DiscussionDomain toDomain() {
		DiscussionDomain result = new DiscussionDomain();

		result.setId(getId());
		result.setTitle(title);
		result.setCommentsCount(commentsCount);

		return result;
	}
	
}
