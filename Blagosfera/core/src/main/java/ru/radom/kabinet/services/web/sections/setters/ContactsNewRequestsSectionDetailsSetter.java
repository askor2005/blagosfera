package ru.radom.kabinet.services.web.sections.setters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.radom.kabinet.dao.ContactDao;

@Component
public class ContactsNewRequestsSectionDetailsSetter extends AbstractSectionDetailsSetter {

	@Autowired
	private ContactDao contactDao;

	@Override
	public void set(SectionDomain section, Long userId) {
		long count = contactDao.getNewRequestsCount(userId);
		section.getDetails().put("visible", count > 0);
		section.getDetails().put("titleSuffix", "(" + count + ")");
	}

	@Override
	public String getSupportedSectionName() {
		return "contactsNewRequests";
	}

}
