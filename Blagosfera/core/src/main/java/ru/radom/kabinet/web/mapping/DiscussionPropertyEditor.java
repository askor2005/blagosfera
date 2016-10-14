package ru.radom.kabinet.web.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.discussion.DiscussionDao;

import java.beans.PropertyEditorSupport;

@Component
public class DiscussionPropertyEditor extends PropertyEditorSupport {
	@Autowired
	private DiscussionDao discussionDao;
	
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(discussionDao.getById(Long.parseLong(text)));
	}
}
