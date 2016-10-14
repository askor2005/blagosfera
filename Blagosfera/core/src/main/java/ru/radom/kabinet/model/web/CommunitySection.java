package ru.radom.kabinet.model.web;

import ru.askor.blagosfera.domain.community.CommunitySectionDomain;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "community_sections")
public class CommunitySection extends LongIdentifiable {

	/**
	 * для внутреннего использования, пишем латиницей
	 */
	@Column(length = 100, nullable = true, unique = true)
	private String name;

	/**
	 * для отображения пользователям, пишем по-русски
	 */
	@Column(length = 100, nullable = false)
	private String title;

	@Column(length = 1000, nullable = true, unique = true)
	private String link;

	@Column(length = 100, nullable = true)
	private String permission;

	@Column(nullable = false)
	private int position;

	@JoinColumn(name = "parent_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private CommunitySection parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("position")
	private List<CommunitySection> children;

	/**
	 * Признак того, что гости сообщества могут просматривать раздел. По умолчанию не могут.
	 */
	@Column(name = "is_guest_access", columnDefinition = "boolean default false", nullable = false)
	private Boolean guestAccess = false;

	@Transient
	private boolean visible;

	public CommunitySection() {
		super();
	}

	public CommunitySection(String name, String title, String link, String permission, int position, CommunitySection parent, List<CommunitySection> children, boolean visible) {
		super();
		this.name = name;
		this.title = title;
		this.link = link;
		this.permission = permission;
		this.position = position;
		this.parent = parent;
		this.children = children;
		this.visible = visible;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public CommunitySection getParent() {
		return parent;
	}

	public void setParent(CommunitySection parent) {
		this.parent = parent;
	}

	public List<CommunitySection> getChildren() {
		return children;
	}

	public void setChildren(List<CommunitySection> children) {
		this.children = children;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Boolean getGuestAccess() {
		return guestAccess;
	}

	public void setGuestAccess(Boolean guestAccess) {
		this.guestAccess = guestAccess;
	}

	public CommunitySectionDomain toDomain(boolean isWithChild) {
		CommunitySectionDomain result = new CommunitySectionDomain();
		result.setId(getId());
		result.setName(getName());
		result.setTitle(getTitle());
		result.setLink(getLink());
		result.setPermission(getPermission());
		result.setPosition(getPosition());
		result.setGuestAccess(getGuestAccess());

		if (isWithChild && getChildren() != null) {
			List<CommunitySectionDomain> childrenResult = new ArrayList<>();
			List<CommunitySection> children = getChildren();
			if (children != null) {
				childrenResult.addAll(children.stream().map(child -> child.toDomain(false)).collect(Collectors.toList()));
			}
			result.setChildren(childrenResult);
		}
		if (getParent() != null) {
			result.setParent(getParent().toDomain(false));
		}
		return result;
	}

	public static List<CommunitySectionDomain> toDomainList(List<CommunitySection> communitySections, boolean isWithChild) {
		List<CommunitySectionDomain> result = new ArrayList<>();
		if (communitySections != null) {
			result.addAll(communitySections.stream().map(communitySection -> communitySection.toDomain(isWithChild)).collect(Collectors.toList()));
		}
		return result;
	}
}
