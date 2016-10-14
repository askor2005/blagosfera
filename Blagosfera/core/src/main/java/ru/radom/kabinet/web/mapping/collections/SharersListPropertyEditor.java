package ru.radom.kabinet.web.mapping.collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.collections.SharersList;
import ru.radom.kabinet.dao.SharerDao;

@Component("sharersListEditor")
public class SharersListPropertyEditor extends CustomCollectionEditor {

	@Autowired
	private SharerDao sharerDao;
	
	public SharersListPropertyEditor() {
		super(SharersList.class);
	}
	
	@Override
	protected Object convertElement(Object element) {
		return element != null ? sharerDao.getById(Long.valueOf((String)element)) : null;
	}

}
