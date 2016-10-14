package ru.radom.kabinet.model.fields;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.BooleanUtils;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "fields")
public class FieldEntity extends LongIdentifiable {

	@Column(name = "internal_name", nullable = false, unique = true, length = 100)
	private String internalName;

	@Column(length = 100, nullable = false)
	private String name;

	@Column(nullable = false)
	private int position;

	@Column(name = "is_unique")
	private boolean unique;

	@Column(name = "hidden_by_default", nullable = false)
	private boolean hiddenByDefault;

	@Column(nullable = false)
	private boolean hideable;

	@JsonIgnore
	@JoinColumn(nullable = false, name = "fields_group_id")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private FieldsGroupEntity fieldsGroup;

	@Column
	private FieldType type;

	@Column(length = 1000)
	private String comment;

	@Column(length = 1000)
	private String example;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "field")
	@OrderBy("position")
	private List<FieldPossibleValueEntity> possibleValues;

	@Column
	private boolean required;

	@Column
	private int points;

	// TODO Походу не нужное поле
	@Deprecated
	@Column(name = "verified_editable", nullable = false)
	private boolean verifiedEditable;

	//использовать падеж для поля
	@Column(name = "use_case")
	private Boolean useCase;

	// Поле с прикрепляемым файлом
	@Column(name = "attached_file")
	private Boolean attachedFile = false;

	// Минимальный размер поля
	@Column(name = "min_size")
	private Integer minSize;

	// Максимальный размер поля
	@Column(name = "max_size")
	private Integer maxSize;

	// Маска поля
	@Column(name = "mask", length = 100)
	private String mask;

	// Поле ввода под маску
	@Column(name = "placeholder", length = 100)
	private String placeholder;

    public FieldEntity() {
    }

    public Field toDomain() {
        Field field = new Field();
        field.setId(getId());
        field.setName(getName());
        field.setValue(null);
        field.setExample(getExample());
        field.setInternalName(getInternalName());
		field.setPosition(getPosition());
		field.setRequired(isRequired());
		field.setPoints(getPoints());
        field.setType(getType());
		field.setMask(getMask());
		field.setPlaceholder(getPlaceholder());
        field.setFieldsGroup(getFieldsGroup() == null ? null : getFieldsGroup().getInternalName());
		field.setAttachedFile(BooleanUtils.toBooleanDefaultIfNull(getAttachedFile(), false));
        return field;
    }

	public static List<Field> toDomainList(List<FieldEntity> fieldEntities) {
		List<Field> result = null;
		if (fieldEntities != null) {
			result = new ArrayList<>();
			for (FieldEntity fieldEntity : fieldEntities) {
				result.add(fieldEntity.toDomain());
			}
		}
		return result;
	}

	public String getInternalName() {
		return internalName;
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isHideable() {
		return hideable;
	}

	public void setHideable(boolean hideable) {
		this.hideable = hideable;
	}

	public FieldsGroupEntity getFieldsGroup() {
		return fieldsGroup;
	}

	public void setFieldsGroup(FieldsGroupEntity fieldsGroup) {
		this.fieldsGroup = fieldsGroup;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public List<FieldPossibleValueEntity> getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(List<FieldPossibleValueEntity> possibleValues) {
		this.possibleValues = possibleValues;
	}

	public boolean isHiddenByDefault() {
		return hiddenByDefault;
	}

	public void setHiddenByDefault(boolean hiddenByDefault) {
		this.hiddenByDefault = hiddenByDefault;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public boolean isVerifiedEditable() {
		return verifiedEditable;
	}

	public void setVerifiedEditable(boolean verifiedEditable) {
		this.verifiedEditable = verifiedEditable;
	}

	public Boolean isUseCase() {
		return useCase;
	}

	public void setUseCase(Boolean useCase) {
		this.useCase = useCase;
	}

	public Boolean getAttachedFile() {
		return attachedFile;
	}

	public void setAttachedFile(Boolean attachedFile) {
		this.attachedFile = attachedFile;
	}

	public Integer getMinSize() {
		return minSize;
	}

	public void setMinSize(Integer minSize) {
		this.minSize = minSize;
	}

	public Integer getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(Integer maxSize) {
		this.maxSize = maxSize;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	@Override
	public int hashCode() {
		return internalName.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if ((object instanceof FieldEntity) || (internalName == null)) {
			return internalName.equals(((FieldEntity) object).getInternalName());
		} else {
			return false;
		}
	}
}
