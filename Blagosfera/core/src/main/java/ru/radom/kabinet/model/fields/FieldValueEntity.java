package ru.radom.kabinet.model.fields;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.*;
import org.hibernate.envers.Audited;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "field_values")
public class FieldValueEntity extends LongIdentifiable implements Serializable {

	@Column(name = "string_value", length = 10000000)
	@Audited
	private String stringValue;

	@Column(nullable = false)
	@Audited
	private boolean hidden;

	@JoinColumn(name = "field_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Fetch(FetchMode.JOIN)
	private FieldEntity field;

	@Any(metaColumn = @Column(name = "object_type", length = 50), fetch = FetchType.EAGER)
	@AnyMetaDef(idType = "long", metaType = "string", metaValues = {
			@MetaValue(targetEntity = UserEntity.class, value = Discriminators.SHARER),
			@MetaValue(targetEntity = CommunityEntity.class, value = Discriminators.COMMUNITY)})
	@JoinColumn(name = "object_id")
	private LongIdentifiable object;

	@Column(name = "object_id", nullable = false, insertable = false, updatable = false)
	private Long objectId;

	@Column(name = "object_type", nullable = false, insertable = false, updatable = false)
	private String objectType;

	// Ссылка на прикрепленный файл к полю
	@Column(name = "file_url", length = 10000)
	private String fileUrl;

	// Список файлов поля
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fieldValue")
	@OrderBy("id")
	private List<FieldFileEntity> fieldFiles;

	public String getStringValue() {
		return stringValue;
	}


	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public FieldEntity getField() {
		return field;
	}

	public void setField(FieldEntity field) {
		this.field = field;
	}

	public LongIdentifiable getObject() {
		return object;
	}

	public void setObject(LongIdentifiable object) {
		this.object = object;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public List<FieldFileEntity> getFieldFiles() {
		return fieldFiles;
	}

	public void setFieldFiles(List<FieldFileEntity> fieldFiles) {
		this.fieldFiles = fieldFiles;
	}
}