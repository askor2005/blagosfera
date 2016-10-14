package ru.radom.kabinet.web.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.OkvedDao;

import java.util.List;

@Component("okvedListPropertyEditor")
public class OkvedListPropertyEditor extends CustomCollectionEditor {

	@Autowired
	private OkvedDao okvedDao;
	
	public OkvedListPropertyEditor() {
		super(List.class);
	}
	
	@Override
	protected Object convertElement(Object element) {
		if(element != null) {
			Long id = Long.valueOf((String)element);
			return okvedDao.getById(id);
		}
		return null;
	}

}
