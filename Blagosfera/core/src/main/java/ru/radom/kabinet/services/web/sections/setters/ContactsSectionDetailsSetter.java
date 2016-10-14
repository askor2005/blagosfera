package ru.radom.kabinet.services.web.sections.setters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.radom.kabinet.dao.ContactDao;

@Component
public class ContactsSectionDetailsSetter extends AbstractSectionDetailsSetter {

	@Autowired
	private ContactDao contactDao;

	@Override
	public void set(SectionDomain section, Long userId) {
		long newRequestsCount = contactDao.getNewRequestsCount(userId);
		if (newRequestsCount > 0) {
			section.getDetails().put("additionalIcon", "fa fa-warning faa-flash animated");
		}
	}

	@Override
	public String getSupportedSectionName() {
		return "contacts";
	}

}
