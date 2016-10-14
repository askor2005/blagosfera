package ru.radom.kabinet.model.fields;

import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;

@Entity
@Table(name = "field_possible_values")
public class FieldPossibleValueEntity extends LongIdentifiable {

	@JoinColumn(name = "field_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private FieldEntity field;

	@Column(name = "string_value", length = 1000)
	private String stringValue;
	
	@Column(nullable = false)
	private int position;

	public FieldEntity getField() {
		return field;
	}

	public void setField(FieldEntity field) {
		this.field = field;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}