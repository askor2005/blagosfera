package ru.radom.kabinet.model.fields;

import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;

@Entity
@Table(name = MetaField.TABLE_NAME)
public class MetaField extends LongIdentifiable {
	public static final String TABLE_NAME = "meta_fields";

	public static class Columns {
		public static final String INTERNAL_NAME = "internal_name";
		public static final String PARTICIPANT_TYPE = "participant_type";
		public static final String FIELDS_GROUP = "fields_group_id";
		public static final String POSITION = "position";
		public static final String NAME = "name";
		public static final String DESCRIPTION = "description";
		public static final String USE_CASE = "use_case";
		public static final String TEXT_VALUE = "text_value";
	}

	/**
	 * внутреннее имя для использования в коде приложения
	 */
	@Column(name = Columns.INTERNAL_NAME, nullable = false, unique = true, length = 100)
	private String internalName;

	/**
	 * тип участника
	 * @see ParticipantsTypes
	 */
	@Column(name = Columns.PARTICIPANT_TYPE)
	private String participantType;

	/**
	 * группа полей к которой относится данное мета поле
	 */
	@JoinColumn(name = Columns.FIELDS_GROUP)
	@ManyToOne(fetch = FetchType.LAZY)
	private FieldsGroupEntity fieldsGroup;

	/**
	 * индекс сортировки
	 */
	@Column(name = Columns.POSITION)
	private int position;

	/**
	 * название мета поля
	 */
	@Column(name = Columns.NAME)
	private String name;

	/**
	 * описание мета поля
	 */
	@Column(name = Columns.DESCRIPTION)
	private String description;

	/**
	 * значение поля в виде текста
	 */
	@Column(name = Columns.TEXT_VALUE)
	private String textValue;

	/**
	 * возможность примененения к этому полю падежей
	 */
	@Column(name = Columns.USE_CASE)
	private Boolean useCase;

	public String getInternalName() {
		return internalName;
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	public String getParticipantType() {
		return participantType;
	}

	public void setParticipantType(String participantType) {
		this.participantType = participantType;
	}

	public FieldsGroupEntity getFieldsGroup() {
		return fieldsGroup;
	}

	public void setFieldsGroup(FieldsGroupEntity fieldsGroup) {
		this.fieldsGroup = fieldsGroup;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

	public Boolean getUseCase() {
		return useCase;
	}

	public void setUseCase(Boolean useCase) {
		this.useCase = useCase;
	}
}