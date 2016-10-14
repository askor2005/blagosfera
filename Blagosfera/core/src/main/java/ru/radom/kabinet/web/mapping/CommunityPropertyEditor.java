package ru.radom.kabinet.web.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.communities.CommunityDao;

import java.beans.PropertyEditorSupport;

@Component("communityPropertyEditor")
public class CommunityPropertyEditor extends PropertyEditorSupport {

	@Autowired
	private CommunityDao communityDao;

	@Override
	public void setAsText(String text) {
		setValue(communityDao.getById(Long.parseLong(text)));
	}
}
