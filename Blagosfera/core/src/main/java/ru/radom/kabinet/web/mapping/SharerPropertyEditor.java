package ru.radom.kabinet.web.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.UserEntity;

import java.beans.PropertyEditorSupport;

@Component("sharerPropertyEditor")
public class SharerPropertyEditor extends PropertyEditorSupport {

	@Autowired
	private SharerDao sharerDao;
	
	@Override
	public void setAsText(String text) {
		UserEntity userEntity = sharerDao.getById(Long.parseLong(text));
		setValue(userEntity);
	}
	
}
