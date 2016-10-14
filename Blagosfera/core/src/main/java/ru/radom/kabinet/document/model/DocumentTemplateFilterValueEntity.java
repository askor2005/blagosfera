package ru.radom.kabinet.document.model;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.fields.FieldEntity;

import javax.persistence.*;

/**
 * Модель Значение фильтра шаблона документа - для документооборота
 */
@Entity
@Table(name = DocumentTemplateFilterValueEntity.TABLE_NAME)
public class DocumentTemplateFilterValueEntity extends LongIdentifiable {
	public static final String TABLE_NAME = "documents_templates_filters_values";

	public static class Columns {
		public static final String DOCUMENT_TEMPLATE_ID = "document_template_id";
		public static final String PARTICIPANT_ID = "participant_id";
		public static final String FIELD_ID = "field_id";
		public static final String VALUE = "value";
	}

	//идентификатор на шаблон
	@JoinColumn(name = Columns.DOCUMENT_TEMPLATE_ID)
	@ManyToOne(fetch = FetchType.LAZY)
	private DocumentTemplateEntity documentTemplate;

	//идентификатор на участника
	@JoinColumn(name = Columns.PARTICIPANT_ID)
	@ManyToOne(fetch = FetchType.LAZY)
	private DocumentClassDataSourceEntity participant;

	//идентификатор на поле фильтр
	@JoinColumn(name = Columns.FIELD_ID)
	@ManyToOne(fetch = FetchType.LAZY)
	private FieldEntity filterField;

	//значение фильтра
	@Column(name = Columns.VALUE)
	private String value;

	public DocumentTemplateEntity getDocumentTemplate() {
		return documentTemplate;
	}

	public void setDocumentTemplate(DocumentTemplateEntity documentTemplate) {
		this.documentTemplate = documentTemplate;
	}

	public DocumentClassDataSourceEntity getParticipant() {
		return participant;
	}

	public void setParticipant(DocumentClassDataSourceEntity participant) {
		this.participant = participant;
	}

	public FieldEntity getFilterField() {
		return filterField;
	}

	public void setFilterField(FieldEntity filterField) {
		this.filterField = filterField;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}