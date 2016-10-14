package ru.radom.kabinet.document.model;

import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.AssociationFormSearchType;
import ru.askor.blagosfera.domain.document.DocumentClassDataSource;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель источник данных класса документа - для документооборота
 */
@Entity
@Table(name = DocumentClassDataSourceEntity.TABLE_NAME)
public class DocumentClassDataSourceEntity extends LongIdentifiable {
	public static final String TABLE_NAME = "documents_types_participants";

	public static class Columns {
		public static final String DOCUMENT_TYPE_ID = "document_type_id";
		public static final String PARTICIPANT_TYPE = "participant_type";
		public static final String PARTICIPANT_NAME = "participant_name";
		public static final String ASSOCIATION_FORM_ID = "association_form_id";
		public static final String ASSOCIATION_FORM_SEARCH_TYPE = "association_form_search_type";
	}

	//идентификатор на тип шаблона
	@JoinColumn(name = Columns.DOCUMENT_TYPE_ID)
	@ManyToOne(fetch = FetchType.LAZY)
	private DocumentClassEntity documentType;

	/**
	 * тип участника
	 * @see ParticipantsTypes
	 */
	@Column(name = Columns.PARTICIPANT_TYPE)
	private String participantType;

	//наименование участника
	@Column(name = Columns.PARTICIPANT_NAME)
	private String participantName;

	//фильтры (наборы полей из системы)
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "documents_types_participants_filters", joinColumns = {@JoinColumn(name = "document_type_participant_id", nullable = false, updatable = false)}, inverseJoinColumns = {@JoinColumn(name = "field_id", nullable = false, updatable = false)})
	private List<FieldEntity> fieldsFilters;

	/**
	 * Форма объединения
	 * Ссылка на объект итем компонента универсальных списков
	 */
	@JoinColumn(name = Columns.ASSOCIATION_FORM_ID)
	@ManyToOne(fetch = FetchType.LAZY)
	private RameraListEditorItem associationForm;

	/**
	 * Способ применения фильтра по форме объединения
	 */
	@Column(name = Columns.ASSOCIATION_FORM_SEARCH_TYPE, nullable = true)
	private AssociationFormSearchType associationFormSearchType;

	public DocumentClassEntity getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentClassEntity documentType) {
		this.documentType = documentType;
	}

	public String getParticipantType() {
		return participantType;
	}

	public void setParticipantType(String participantType) {
		this.participantType = participantType;
	}

	public String getParticipantName() {
		return participantName;
	}

	public void setParticipantName(String participantName) {
		this.participantName = participantName;
	}

	public List<FieldEntity> getFieldsFilters() {
		return fieldsFilters;
	}

	public void setFieldsFilters(List<FieldEntity> fieldsFilters) {
		this.fieldsFilters = fieldsFilters;
	}

	public RameraListEditorItem getAssociationForm() {
		return associationForm;
	}

	public void setAssociationForm(RameraListEditorItem associationForm) {
		this.associationForm = associationForm;
	}

	public AssociationFormSearchType getAssociationFormSearchType() {
		return associationFormSearchType;
	}

	public void setAssociationFormSearchType(AssociationFormSearchType associationFormSearchType) {
		this.associationFormSearchType = associationFormSearchType;
	}

	public DocumentClassDataSource toDomain() {
		DocumentClassDataSource result = new DocumentClassDataSource();
		result.setId(getId());
		result.setName(getParticipantName());
		if (getParticipantType() != null) {
			result.setType(ParticipantsTypes.valueOf(getParticipantType()));
		}
		result.setAssociationForm(RameraListEditorItem.toDomainSafe(getAssociationForm()));
		result.setAssociationFormSearchType(getAssociationFormSearchType());
		return result;
	}

	public static DocumentClassDataSource toDomainSafe(DocumentClassDataSourceEntity entity) {
		DocumentClassDataSource result = null;
		if (entity != null) {
			result = entity.toDomain();
		}
		return result;
	}

	public static List<DocumentClassDataSource> toDomainList(List<DocumentClassDataSourceEntity> entities) {
		List<DocumentClassDataSource> result = null;
		if (entities != null) {
			result = new ArrayList<>();
			for (DocumentClassDataSourceEntity entity : entities) {
				result.add(toDomainSafe(entity));
			}
		}
		return result;
	}

}