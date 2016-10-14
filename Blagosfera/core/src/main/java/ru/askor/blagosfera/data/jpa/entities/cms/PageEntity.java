package ru.askor.blagosfera.data.jpa.entities.cms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import ru.askor.blagosfera.domain.cms.Page;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.web.PageEdition;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "pages")
public class PageEntity extends LongIdentifiable {

	// Время в милисекундах от времени последней блокировки страницы после которого страница будет возможна для редактирования другими пользователями
	public static final long MILLISECONDS_FOR_RELEASE_PAGE = 2 * 60 * 1000;

	@JsonIgnore
	@Column(length = 10000000)
	private String content;

	@Column(length = 1000)
	private String title;

	@JsonIgnore
	@Column(length = 1000)
	private String description;

	@JsonIgnore
	@Column(length = 1000)
	private String keywords;

	@JsonIgnore
	@Formula("COALESCE((select count(*) from page_editions as pe where pe.page_id = id), 0)")
	private int editionsCount;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "page",orphanRemoval = true)
	@OrderBy("date desc")
	private List<PageEdition> editions;

	/**
	 * Текущий пользователь редактирующий страницу
	 */
	@JsonIgnore
	@JoinColumn(name = "current_editor_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private UserEntity currentEditor;

	/**
	 * Последняя дата когда редактор находился на странице
	 */
	@JsonIgnore
	@Column(name = "current_editor_date")
	private Date currentEditorEditDate;

    @Column(name = "path")
    @Type(type="text")
    private String path;

    public PageEntity() {
    }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public int getEditionsCount() {
		return editionsCount;
	}

	public void setEditionsCount(int editionsCount) {
		this.editionsCount = editionsCount;
	}

	public List<PageEdition> getEditions() {
		return editions;
	}

	public void setEditions(List<PageEdition> editions) {
		this.editions = editions;
	}

	public UserEntity getCurrentEditor() {
		return currentEditor;
	}

	public void setCurrentEditor(UserEntity currentEditor) {
		this.currentEditor = currentEditor;
	}

	public Date getCurrentEditorEditDate() {
		return currentEditorEditDate;
	}

	public void setCurrentEditorEditDate(Date currentEditorEditDate) {
		this.currentEditorEditDate = currentEditorEditDate;
	}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Page toDomain() {
		UserEntity currrentEditor = getCurrentEditor();
		Page page = new Page(getId(),getContent(),title,description,keywords,editionsCount,currentEditorEditDate,currrentEditor != null ? currrentEditor.getId() : null);
		return page;
	}
}
