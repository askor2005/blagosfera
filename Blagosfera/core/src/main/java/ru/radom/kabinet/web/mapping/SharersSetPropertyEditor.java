package ru.radom.kabinet.web.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.SharerDao;

import java.util.Set;

@Component("sharersSetEditor")
public class SharersSetPropertyEditor extends CustomCollectionEditor {

	@Autowired
	private SharerDao sharerDao;
	
	public SharersSetPropertyEditor() {
		super(Set.class);
	}
	
	@Override
	protected Object convertElement(Object element) {
		if(element != null) {
			Long id = Long.valueOf((String)element);
			return sharerDao.getById(id);
		}
		return null;
	}

}
