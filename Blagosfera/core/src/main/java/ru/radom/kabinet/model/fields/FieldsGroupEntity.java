package ru.radom.kabinet.model.fields;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.askor.blagosfera.domain.field.FieldsGroup;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "fields_groups")
public class FieldsGroupEntity extends LongIdentifiable {

	@Column(name = "internal_name", nullable = false, unique = true, length = 100)
	private String internalName;

	@Column(length = 100, nullable = false)
	private String name;

	@Column(nullable = false)
	private int position;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "fieldsGroup")
	@Fetch(value = FetchMode.SELECT)
	@OrderBy("position")
	private List<FieldEntity> fields = new ArrayList<>();

	@Column(name = "object_type", length = 50)
	private String objectType;

	/**
	 * Таблица с формами объединений в которых данные группы полей будут доступны
	 */
	@ManyToMany(fetch = FetchType.LAZY, cascade = {})
	@JoinTable(name = "fields_groups_association_forms", uniqueConstraints =
	@UniqueConstraint(name = "UK_fields_group_id_association_form_id",
			columnNames = {"fields_group_id", "association_form_id"}),
			joinColumns = {
					@JoinColumn(name = "fields_group_id", nullable = false, updatable = false)},
			inverseJoinColumns = {
					@JoinColumn(name = "association_form_id", nullable = false, updatable = false)})
	private Set<RameraListEditorItem> associationForms = new HashSet<>();

    public FieldsGroupEntity() {
    }

    public FieldsGroup toDomain(boolean withAssociationForms, boolean withFields) {
        FieldsGroup fieldsGroup = new FieldsGroup();
        fieldsGroup.setId(getId());
        fieldsGroup.setInternalName(getInternalName());
        fieldsGroup.setName(getName());
        fieldsGroup.setPosition(getPosition());

        if (withAssociationForms) {
            for (RameraListEditorItem item : getAssociationForms()) {
                fieldsGroup.getAssociationForms().add(item.toDomain());
            }
        }

        if (withFields) {
            for (FieldEntity field : getFields()) {
                fieldsGroup.getFields().add(field.toDomain());
            }
        }

        return fieldsGroup;
    }

	public static List<FieldsGroup> toDomainList(List<FieldsGroupEntity> fieldsGroupEntities, boolean withAssociationForms, boolean withFields) {
		List<FieldsGroup> result = null;
		if (fieldsGroupEntities != null) {
			result = new ArrayList<>();
			for(FieldsGroupEntity fieldsGroupEntity : fieldsGroupEntities) {
				result.add(fieldsGroupEntity.toDomain(withAssociationForms, withFields));
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

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	public List<FieldEntity> getFields() {
		return fields;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public Set<RameraListEditorItem> getAssociationForms() {
		return associationForms;
	}
}