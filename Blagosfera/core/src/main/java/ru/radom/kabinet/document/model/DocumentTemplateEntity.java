package ru.radom.kabinet.document.model;

import ru.askor.blagosfera.domain.document.DocumentTemplate;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель шаблон документа - для документооборота
 */
@Entity(name = "flowOfDocumentTemplate")
@Table(name = DocumentTemplateEntity.TABLE_NAME)
public class DocumentTemplateEntity extends LongIdentifiable {
	public static final String TABLE_NAME = "documents_templates";

	public static class Columns {
		public static final String CREATOR_ID = "creator_id";
		public static final String DOCUMENT_TYPE_ID = "document_type_id";
		public static final String NAME = "name";
		public static final String DOCUMENT_NAME = "document_name";
		public static final String DOCUMENT_SHORT_NAME = "document_short_name";
		public static final String CONTENT = "content";
		public static final String CODE = "code";
		public static final String HELP_LINK = "help_link";
		public static final String POSITION = "position";
	}

	//пользователь создавший шаблон документа
	@JoinColumn(name = Columns.CREATOR_ID)
	@ManyToOne(fetch = FetchType.LAZY)
	private UserEntity creator;

	//идентификатор на тип шаблона
	@JoinColumn(name = Columns.DOCUMENT_TYPE_ID)
	@ManyToOne(fetch = FetchType.LAZY)
	private DocumentClassEntity documentType;

	//наименование шаблона
	@Column(name = Columns.NAME)
	private String name;

	@Column(name = Columns.DOCUMENT_SHORT_NAME, length = 10000)
	private String documentShortName;

	// Полное название итогового документа
	@Column(name = Columns.DOCUMENT_NAME, length = 10000)
	private String documentName;

	//содержимое шаблона
	@Column(name = Columns.CONTENT, length = 10000000)
	private String content;

	// Код шаблона. Нужен для получения шаблона в коде.
	@Column(name = Columns.CODE, length = 100, unique = true, nullable = true)
	private String code;

	//фильтры шаблона
	@OneToMany(mappedBy = "documentTemplate", fetch = FetchType.LAZY)
	private List<DocumentTemplateFilterValueEntity> filters;

	// Подписанты шаблона документа
	@OneToMany(mappedBy = "documentTemplate", fetch = FetchType.LAZY)
	private List<DocumentTemplateParticipantEntity> templateParticipants;

	// Ссылка на страницу описания шаблона
	@Column(name = Columns.HELP_LINK, length = 2000)
	private String helpLink;

	// Сортировка
	@Column(name = Columns.POSITION, columnDefinition = "int default 0")
	private Integer position;

	@Column(name = "pdf_export_arguments")
	private String pdfExportArguments;

	public UserEntity getCreator() {
		return creator;
	}

	public void setCreator(UserEntity creator) {
		this.creator = creator;
	}

	public DocumentClassEntity getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentClassEntity documentType) {
		this.documentType = documentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDocumentShortName() {
		return documentShortName;
	}

	public void setDocumentShortName(String documentShortName) {
		this.documentShortName = documentShortName;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<DocumentTemplateFilterValueEntity> getFilters() {
		return filters;
	}

	public void setFilters(List<DocumentTemplateFilterValueEntity> filters) {
		this.filters = filters;
	}

	public List<DocumentTemplateParticipantEntity> getTemplateParticipants() {
		return templateParticipants;
	}

	public void setTemplateParticipants(List<DocumentTemplateParticipantEntity> templateParticipants) {
		this.templateParticipants = templateParticipants;
	}

	public String getHelpLink() {
		return helpLink;
	}

	public void setHelpLink(String helpLink) {
		this.helpLink = helpLink;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getPdfExportArguments() {
		return pdfExportArguments;
	}

	public void setPdfExportArguments(String pdfExportArguments) {
		this.pdfExportArguments = pdfExportArguments;
	}

	public DocumentTemplate toDomain(boolean withParticipants) {
		DocumentTemplate result = new DocumentTemplate();
		result.setId(getId());
		result.setCreator(getCreator() == null ? null : getCreator().toDomain());
		result.setDocumentClass(DocumentClassEntity.toDomainSafe(getDocumentType()));
		result.setName(getName());
		result.setDocumentShortName(getDocumentShortName());
		result.setDocumentName(getDocumentName());
		result.setContent(getContent());
		result.setCode(getCode());
		//result.setfilters
		if (withParticipants && getTemplateParticipants() != null) {
			result.setDocumentTemplateParticipants(DocumentTemplateParticipantEntity.toDomainList(getTemplateParticipants()));
		}
		result.setDocumentClass(DocumentClassEntity.toDomainSafe(getDocumentType()));
		result.setHelpLink(getHelpLink());
		result.setPosition(getPosition());
		result.setPdfExportArguments(getPdfExportArguments());

		return result;
	}

	public static DocumentTemplate toDomainSafe(DocumentTemplateEntity entity, boolean withParticipants) {
		DocumentTemplate result = null;
		if (entity != null) {
			result = entity.toDomain(withParticipants);
		}
		return result;
	}

	public static List<DocumentTemplate> toDomainList(List<DocumentTemplateEntity> entities, boolean withParticipants) {
		List<DocumentTemplate> result = null;
		if (entities != null) {
			result = new ArrayList<>();
			for (DocumentTemplateEntity entity : entities) {
				result.add(toDomainSafe(entity, withParticipants));
			}
		}
		return result;
	}
}