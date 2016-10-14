package ru.radom.kabinet.web.mapping.collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.collections.RolesList;
import ru.radom.kabinet.dao.RoleDao;

@Component("rolesListEditor")
public class RolesListPropertyEditor extends CustomCollectionEditor {

	@Autowired
	private RoleDao roleDao;
	
	public RolesListPropertyEditor() {
		super(RolesList.class);
	}
	
	@Override
	protected Object convertElement(Object element) {
		return element != null ? roleDao.getById(Long.valueOf((String)element)) : null;
	}

}
