package ru.radom.kabinet.model.news;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;
import ru.askor.blagosfera.domain.news.NewsItem;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.common.TagEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.discussion.Discussable;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.radom.kabinet.model.notifications.SystemAccountEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.model.rating.Ratable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "news")
public class News extends LongIdentifiable implements Discussable, Ratable {

	@Column(length = 1000, nullable = true)
	private String title;

	@Column(length = 10000000, nullable = false)
	private String text;

	@OneToMany(mappedBy = "news", cascade = CascadeType.ALL ,fetch = FetchType.LAZY, orphanRemoval = true)
	private List<NewsAttachment> attachments = new ArrayList<>();

	@JoinColumn(name = "category_id", nullable = false)
	@ManyToOne
	RameraListEditorItem category;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "news_tags",
			joinColumns = { @JoinColumn(name = "news_id", nullable = false, updatable = false) },
			inverseJoinColumns = {@JoinColumn(name = "tag_id", nullable = false, updatable = false)})
	List<TagEntity> tags;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@Any(metaColumn = @Column(name = "author_type", length = 50), fetch = FetchType.LAZY)
	@AnyMetaDef(idType = "long", metaType = "string", metaValues = { @MetaValue(targetEntity = SystemAccountEntity.class, value = Discriminators.SYSTEM_ACCOUNT), @MetaValue(targetEntity = UserEntity.class, value = Discriminators.SHARER) })
	@JoinColumn(name = "author_id")
	private RadomAccount author;

	@Column(name = "author_id", insertable = false, updatable = false)
	private Long authorId;

	@Any(metaColumn = @Column(name = "scope_type", length = 50), fetch = FetchType.LAZY)
	@AnyMetaDef(idType = "long", metaType = "string", metaValues = { @MetaValue(targetEntity = UserEntity.class, value = Discriminators.SHARER), @MetaValue(targetEntity = CommunityEntity.class, value = Discriminators.COMMUNITY) })
	@JoinColumn(name = "scope_id")
	private RadomAccount scope;

	@Column(name = "scope_id", insertable = false, updatable = false)
	private Long scopeId;

	@Column(name = "scope_type", insertable = false, updatable = false)
	private String scopeType;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "discussion_id", nullable = true)
	private Discussion discussion;

	@Column(nullable = true)
	private boolean moderated;

	@Column(name = "edit_date", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date editDate;

	@Column(name = "edit_count", nullable = true)
	private int editCount;

	@Any(metaColumn = @Column(name = "editor_type", length = 50), fetch = FetchType.LAZY)
	@AnyMetaDef(idType = "long", metaType = "string", metaValues = { @MetaValue(targetEntity = SystemAccountEntity.class, value = Discriminators.SYSTEM_ACCOUNT), @MetaValue(targetEntity = UserEntity.class, value = Discriminators.SHARER) })
	@JoinColumn(name = "editor_id")
	private RadomAccount editor;

	@Column(nullable = false)
	private boolean deleted;


	/*
     * --------->CONSTRUCTORS REGION<-------------
     */
	public News() { }

	public News(String title, String text, Date date, RadomAccount author, RadomAccount scope) {
		super();
		this.title = title;
		this.text = text;
		this.date = date;
		this.author = author;
		this.scope = scope;
		this.deleted = false;
	}

	public News(String title, String text, RadomAccount author, RadomAccount scope) {
		this(title, text, new Date(), author, scope);
	}
    /*
     * --------->END CONSTRUCTORS REGION<-------------
     */


	/*
     * --------->GETTERS AND SETTERS REGION<-------------
     */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<NewsAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<NewsAttachment> attachments) {
		this.attachments = attachments;
	}

	public RameraListEditorItem getCategory() {
		return category;
	}

	public void setCategory(RameraListEditorItem category) {
		this.category = category;
	}

	public List<TagEntity> getTags() {
		return tags;
	}

	public void setTags(List<TagEntity> tags) {
		this.tags = tags;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public RadomAccount getAuthor() {
		return author;
	}

	public void setAuthor(RadomAccount author) {
		this.author = author;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public RadomAccount getScope() {
		return scope;
	}

	public void setScope(RadomAccount scope) {
		this.scope = scope;
	}

	public Long getScopeId() {
		return scopeId;
	}

	public void setScopeId(Long scopeId) {
		this.scopeId = scopeId;
	}

	public String getScopeType() {
		return scopeType;
	}

	public void setScopeType(String scopeType) {
		this.scopeType = scopeType;
	}

	public boolean isModerated() {
		return moderated;
	}

	public void setModerated(boolean moderated) {
		this.moderated = moderated;
	}

	public String getLink() {
		return "/news/" + getId();
	}

	@Override
	public Discussion getDiscussion() {
		return discussion;
	}

	public void setDiscussion(Discussion discussion) {
		this.discussion = discussion;
	}

	public Date getEditDate() {
		return editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public int getEditCount() {
		return editCount;
	}

	public void setEditCount(int editCount) {
		this.editCount = editCount;
	}

	public RadomAccount getEditor() {
		return editor;
	}

	public void setEditor(RadomAccount editor) {
		this.editor = editor;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
    /*
     * --------->END GETTERS AND SETTERS REGION<-------------
     */


	public NewsItem toDomain() {
		NewsItem result = new NewsItem();

		result.setId(getId());
		result.setTitle(title);
		result.setText(text);
		result.setLink(getLink());
		result.setEditCount(editCount);
		result.setDate(date);

		result.setAuthor(author);
		result.setScope(scope);
		result.setCategory(category.toDomain());

		for (NewsAttachment attachment : attachments) {
			result.getAttachments().add(attachment.toDomain());
		}


		result.setTags(tags.stream()
				.map(TagEntity::toDomain)
				.collect(Collectors.toList()));

		return result;
	}

}
