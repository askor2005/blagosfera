package ru.radom.kabinet.web.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.discussion.CommentDao;

import java.beans.PropertyEditorSupport;

@Component
public class CommentPropertyEditor extends PropertyEditorSupport {

	@Autowired
	private CommentDao commentDao;
	
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(commentDao.getById(Long.parseLong(text)));
	}
}
