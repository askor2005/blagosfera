package ru.radom.kabinet.web.mapping.collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.collections.CommunityPostsList;
import ru.radom.kabinet.dao.communities.CommunityPostDao;

@Component("communityPostsListEditor")
public class CommunityPostsListPropertyEditor extends CustomCollectionEditor {

	@Autowired
	private CommunityPostDao communityPostDao;

	public CommunityPostsListPropertyEditor() {
		super(CommunityPostsList.class);
	}

	@Override
	protected Object convertElement(Object element) {
		return element != null ? communityPostDao.getById(Long.valueOf((String) element)) : null;
	}

}
