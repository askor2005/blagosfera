package ru.radom.kabinet.web.mapping;

import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.LongIdentifiable;

import java.beans.PropertyEditorSupport;

public class LongIdentifiablePropertyEditor extends PropertyEditorSupport {

	private Dao<?> dao;
	
	public LongIdentifiablePropertyEditor(Dao<?> dao) {
		super();
		this.dao = dao;
	}

	@Override
	public void setAsText(String text) {
		try {
			Long id = Long.parseLong(text);
			LongIdentifiable longIdentifiable = dao.getById(id);
			setValue(longIdentifiable);
		} catch (Exception e) {
			setValue(null);
		}
		
	}
}
