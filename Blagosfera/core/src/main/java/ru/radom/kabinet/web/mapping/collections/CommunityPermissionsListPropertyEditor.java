package ru.radom.kabinet.web.mapping.collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.collections.CommunityPermissionsList;
import ru.radom.kabinet.dao.communities.CommunityPermissionDao;

@Component("communityPermissionsListEditor")
public class CommunityPermissionsListPropertyEditor extends CustomCollectionEditor {

	@Autowired
	private CommunityPermissionDao communityPermissionDao;

	public CommunityPermissionsListPropertyEditor() {
		super(CommunityPermissionsList.class);
	}

	@Override
	protected Object convertElement(Object element) {
		return element != null ? communityPermissionDao.getById(Long.valueOf((String) element)) : null;
	}

}
