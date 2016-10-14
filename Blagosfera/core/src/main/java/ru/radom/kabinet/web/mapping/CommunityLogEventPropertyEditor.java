/*
package ru.radom.kabinet.web.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.communities.CommunityLogEventDao;

import java.beans.PropertyEditorSupport;

// TODO Наверно нужно удалить
@Component("communityLogEventPropertyEditor")
public class CommunityLogEventPropertyEditor extends PropertyEditorSupport {

	@Autowired
	private CommunityLogEventDao communityLogEventDao;

	@Override
	public void setAsText(String text) {
		setValue(communityLogEventDao.getById(Long.parseLong(text)));
	}

}
*/
