package ru.radom.kabinet.model.fields;

public class FieldStates {

	private FieldEntity field;
	private boolean valueChangeAllowed;
	private boolean visible;

	public FieldStates(FieldEntity field, boolean valueChangeAllowed, boolean visible) {
		super();
		this.field = field;
		this.valueChangeAllowed = valueChangeAllowed;
		this.visible = visible;
	}

	public FieldEntity getField() {
		return field;
	}

	public void setField(FieldEntity field) {
		this.field = field;
	}

	public boolean isValueChangeAllowed() {
		return valueChangeAllowed;
	}

	public void setValueChangeAllowed(boolean valueChangeAllowed) {
		this.valueChangeAllowed = valueChangeAllowed;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
