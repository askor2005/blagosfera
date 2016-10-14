package ru.radom.kabinet.web.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.communities.CommunityDao;

import java.util.Set;

@Component("communitiesSetEditor")
public class CommunitiesSetPropertyEditor extends CustomCollectionEditor {

	@Autowired
	private CommunityDao communityDao;
	
	public CommunitiesSetPropertyEditor() {
		super(Set.class);
	}
	
	@Override
	protected Object convertElement(Object element) {
		if(element != null) {
			Long id = Long.valueOf((String)element);
			return communityDao.getById(id);
		}
		return null;
	}

}
