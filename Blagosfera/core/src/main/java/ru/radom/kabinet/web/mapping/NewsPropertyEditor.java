package ru.radom.kabinet.web.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.news.NewsDao;

import java.beans.PropertyEditorSupport;

@Component("newsPropertyEditor")
public class NewsPropertyEditor extends PropertyEditorSupport {

	@Autowired
	private NewsDao newsDao;

	@Override
	public void setAsText(String text) {
		long id = Long.parseLong(text);
		setValue(id > 0 ? newsDao.getById(id) : null);
	}
}
