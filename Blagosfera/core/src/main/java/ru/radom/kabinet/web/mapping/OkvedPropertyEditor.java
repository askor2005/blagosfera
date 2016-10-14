package ru.radom.kabinet.web.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.OkvedDao;
import ru.radom.kabinet.model.OkvedEntity;

import java.beans.PropertyEditorSupport;

@Component("okvedPropertyEditor")
public class OkvedPropertyEditor extends PropertyEditorSupport {
	
	@Autowired
	private OkvedDao okvedDao;
	
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		OkvedEntity okved = okvedDao.getById(Long.parseLong(text));
		setValue(okved);
	}
}
