package ru.radom.kabinet.model.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.BooleanUtils;
import ru.askor.blagosfera.data.jpa.entities.cms.PageEntity;
import ru.askor.blagosfera.domain.section.SectionAccessType;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.askor.blagosfera.domain.section.SectionType;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.applications.Application;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "sections")
public class Section extends LongIdentifiable {

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

	@Column(length = 100, nullable = true, unique = false)
	private String icon;

	@Column(length = 1000, nullable = true, unique = false)
	private String hint;

	@Column(nullable = false)
	private int position;

	@JsonIgnore
	@JoinColumn(name = "parent_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private Section parent;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("position")
	private List<Section> children;

	@Column(nullable = true)
	private Boolean published;

	@JoinColumn(name = "page_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private PageEntity page;

	@JsonIgnore
	@Column(name = "help_link", length = 100, nullable = true, unique = false)
	private String helpLink;

	@JsonIgnore
	@JoinColumn(name = "application_id", nullable = true, unique = true)
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	private Application application;

	@Column(name = "image_url", length = 1000, nullable = true, unique = false)
	private String imageUrl;

	@JsonIgnore
	@Column(nullable = false)
	private SectionType type;

	@JsonIgnore
	@Column(name = "access_type")
	private SectionAccessType accessType;

	// Флаг - есть возможность устанавливать альтернативную ссылку контента
	@Column(name = "can_set_forward_url", nullable = true)
	private Boolean canSetForwardUrl;

	// Альтернативная ссылка контента
	@Column(name = "forward_url", length = 1000)
	private String forwardUrl;

	@Column(nullable = false,name = "open_in_new_link")
	private boolean openInNewLink;
	//минимальный уровень регистратора, которому показывается данная секция
	@Column(nullable = true,name = "min_registrator_level_to_show")
	private Integer minRegistratorLevelToShow;
	//показывать данную секцию только админам
	@Column(nullable = false,name = "show_to_admin_users_only")
	private boolean showToAdminUsersOnly;
	//показывать данную секцию только админам
	@Column(nullable = false,name = "show_to_verified_users_only")
	private boolean showToVerifiedUsersOnly;
	@Column(nullable = false,name = "show_to_authorized_users_only")
	private boolean showToAuthorizedUsersOnly;
	@Column(nullable = false,name = "disabled")
	private boolean disabled;

	public Integer getMinRegistratorLevelToShow() {
		return minRegistratorLevelToShow;
	}

	public void setMinRegistratorLevelToShow(Integer minRegistratorLevelToShow) {
		this.minRegistratorLevelToShow = minRegistratorLevelToShow;
	}

	public boolean isShowToAuthorizedUsersOnly() {
		return showToAuthorizedUsersOnly;
	}

	public void setShowToAuthorizedUsersOnly(boolean showToAuthorizedUsersOnly) {
		this.showToAuthorizedUsersOnly = showToAuthorizedUsersOnly;
	}

	public boolean isShowToAdminUsersOnly() {
		return showToAdminUsersOnly;
	}

	public void setShowToAdminUsersOnly(boolean showToAdminUsersOnly) {
		this.showToAdminUsersOnly = showToAdminUsersOnly;
	}

	public boolean isShowToVerifiedUsersOnly() {
		return showToVerifiedUsersOnly;
	}

	public void setShowToVerifiedUsersOnly(boolean showToVerifiedUsersOnly) {
		this.showToVerifiedUsersOnly = showToVerifiedUsersOnly;
	}


	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isOpenInNewLink() {
		return openInNewLink;
	}

	public void setOpenInNewLink(boolean openInNewLink) {
		this.openInNewLink = openInNewLink;
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

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Section getParent() {
		return parent;
	}

	public void setParent(Section parent) {
		this.parent = parent;
	}

	public List<Section> getChildren() {
		return children;
	}

	public void setChildren(List<Section> children) {
		this.children = children;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public SectionType getType() {
		return type;
	}

	public void setType(SectionType type) {
		this.type = type;
	}

	public PageEntity getPage() {
		return page;
	}

	public void setPage(PageEntity page) {
		this.page = page;
	}

	public String getHelpLink() {
		return helpLink;
	}

	public void setHelpLink(String helpLink) {
		this.helpLink = helpLink;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public SectionAccessType getAccessType() {
		return accessType;
	}

	public void setAccessType(SectionAccessType accessType) {
		this.accessType = accessType;
	}

	public Boolean getCanSetForwardUrl() {
		return canSetForwardUrl;
	}

	public void setCanSetForwardUrl(Boolean canSetForwardUrl) {
		this.canSetForwardUrl = canSetForwardUrl;
	}

	public String getForwardUrl() {
		return forwardUrl;
	}

	public void setForwardUrl(String forwardUrl) {
		this.forwardUrl = forwardUrl;
	}

	public SectionDomain toDomain() {
		SectionDomain result = new SectionDomain();
		result.setOpenInNewLink(isOpenInNewLink());
		result.setId(getId());
		result.setName(getName());
		result.setHint(getHint());
		result.setIcon(getIcon());
		result.setHelpLink(getHelpLink());
		result.setImageUrl(getImageUrl());
		result.setLink(getLink());
		result.setTitle(getTitle());
		result.setPosition(getPosition());
		result.setCanSetForwardUrl(BooleanUtils.toBooleanDefaultIfNull(getCanSetForwardUrl(), false));
		result.setForwardUrl(getForwardUrl());
		result.setPublished(BooleanUtils.toBooleanDefaultIfNull(getPublished(), false));
		result.setVisible(true); // TODO
		result.setParentId(getParent() != null ? getParent().getId() : null);
		result.setChildren(getChildren() != null ? toDomainList(getChildren()) : null);
		result.setPageId(getPage() != null ? getPage().getId() : null);
		result.setType(getType());
		result.setAccessType(getAccessType());
		result.setEditable(SectionType.EDITABLE.equals(getType()));
		result.setMinRegistratorLevelToShow(getMinRegistratorLevelToShow());
		result.setShowToAdminUsersOnly(isShowToAdminUsersOnly());
		result.setShowToVerfiedUsersOnly(isShowToVerifiedUsersOnly());
		result.setShowToAuthorizedUsersOnly(isShowToAuthorizedUsersOnly());
		result.setDisabled(isDisabled());
		return result;
	}
	public SectionDomain toDomainNoChildren() {
		SectionDomain result = new SectionDomain();
		result.setOpenInNewLink(isOpenInNewLink());
		result.setId(getId());
		result.setName(getName());
		result.setHint(getHint());
		result.setIcon(getIcon());
		result.setHelpLink(getHelpLink());
		result.setImageUrl(getImageUrl());
		result.setLink(getLink());
		result.setTitle(getTitle());
		result.setPosition(getPosition());
		result.setCanSetForwardUrl(BooleanUtils.toBooleanDefaultIfNull(getCanSetForwardUrl(), false));
		result.setForwardUrl(getForwardUrl());
		result.setPublished(BooleanUtils.toBooleanDefaultIfNull(getPublished(), false));
		result.setVisible(true); // TODO
		result.setParentId(getParent() != null ? getParent().getId() : null);
		result.setPageId(getPage() != null ? getPage().getId() : null);
		result.setType(getType());
		result.setAccessType(getAccessType());
		result.setEditable(SectionType.EDITABLE.equals(getType()));
		result.setMinRegistratorLevelToShow(getMinRegistratorLevelToShow());
		result.setShowToAdminUsersOnly(isShowToAdminUsersOnly());
		result.setShowToVerfiedUsersOnly(isShowToVerifiedUsersOnly());
		result.setDisabled(isDisabled());
		result.setShowToAuthorizedUsersOnly(isShowToAuthorizedUsersOnly());
		return result;
	}

	public static SectionDomain toDomainSafe(Section section) {
		SectionDomain result = null;
		if (section != null) {
			result = section.toDomain();
		}
		return result;
	}
	public static SectionDomain toDomainSafeNoChildren(Section section) {
		SectionDomain result = null;
		if (section != null) {
			result = section.toDomainNoChildren();
		}
		return result;
	}

	public static List<SectionDomain> toDomainList(List<Section> sections) {
		List<SectionDomain> result = null;
		if (sections != null) {
			result = new ArrayList<>();
			for (Section section : sections) {
				result.add(toDomainSafe(section));
			}
		}
		return result;
	}
	public static List<SectionDomain> toDomainListNoChildren(List<Section> sections) {
		List<SectionDomain> result = null;
		if (sections != null) {
			result = new ArrayList<>();
			for (Section section : sections) {
				result.add(toDomainSafeNoChildren(section));
			}
		}
		return result;
	}
}
