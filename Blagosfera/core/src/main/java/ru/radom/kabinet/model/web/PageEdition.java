package ru.radom.kabinet.model.web;
import ru.askor.blagosfera.data.jpa.entities.cms.PageEntity;
import ru.askor.blagosfera.domain.cms.PageEditionDomain;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "page_editions")
public class PageEdition extends LongIdentifiable {

	@JoinColumn(name = "page_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private PageEntity page;

	@JoinColumn(name = "editor_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private UserEntity editor;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	public PageEdition() {
		
	}
	
	public PageEdition(PageEntity page, UserEntity editor, Date date) {
		super();
		this.page = page;
		this.editor = editor;
		this.date = date;
	}

	public PageEntity getPage() {
		return page;
	}

	public void setPage(PageEntity page) {
		this.page = page;
	}

	public UserEntity getEditor() {
		return editor;
	}

	public void setEditor(UserEntity editor) {
		this.editor = editor;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	public PageEditionDomain toDomain() {
		return new PageEditionDomain(getId(),getPage().getId(),getEditor().getId(),getDate());
	}

}
