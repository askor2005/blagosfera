package ru.askor.blagosfera.data.jpa.entities.cms;

import ru.askor.blagosfera.domain.section.HelpSectionDomain;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.utils.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "help_sections")
public class HelpSectionEntity extends LongIdentifiable {

	@Column(nullable = false, unique = true, length = 100)
	private String name;

	@Column(nullable = false)
	private boolean published;

	@JoinColumn(name = "parent_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private HelpSectionEntity parent;

	@JoinColumn(name = "page_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private PageEntity page;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("id")
	private List<HelpSectionEntity> children;

	public HelpSectionEntity() {
		super();
	}

	public HelpSectionEntity(String name, boolean published, HelpSectionEntity parent, PageEntity page) {
		super();
		this.name = name;
		this.published = published;
		this.parent = parent;
		this.page = page;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public HelpSectionEntity getParent() {
		return parent;
	}

	public void setParent(HelpSectionEntity parent) {
		this.parent = parent;
	}

	public PageEntity getPage() {
		return page;
	}

	public void setPage(PageEntity page) {
		this.page = page;
	}

	public List<HelpSectionEntity> getChildren() {
		return children;
	}

	public void setChildren(List<HelpSectionEntity> children) {
		this.children = children;
	}

	public String getTitle() {
		return StringUtils.hasLength(page.getTitle()) ? page.getTitle() : name;
	}

	public HelpSectionDomain toDomain() {
		HelpSectionDomain result = new HelpSectionDomain();
		result.setTitle(getTitle());
		result.setId(getId());
		result.setName(getName());
		result.setPublished(isPublished());
		HelpSectionEntity parent = getParent();
		PageEntity pageEntity = getPage();
		result.setParentId(parent != null ? parent.getId() : null);
		result.setPageId(pageEntity != null ? pageEntity.getId() : null);
		return result;
	}

	public static List<HelpSectionDomain> toDomainList(List<HelpSectionEntity> helpSections) {
		List<HelpSectionDomain> result = null;
		if (helpSections != null) {
			result = new ArrayList<>();
			for (HelpSectionEntity helpSection : helpSections) {
				result.add(helpSection.toDomain());
			}
		}
		return result;
	}
	
}
