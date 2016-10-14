package ru.radom.kabinet.services.web.sections;

import ru.askor.blagosfera.domain.section.SectionDomain;

public interface SectionsDetailService {

	void setDetails(SectionDomain section, Long userId);
	
}
