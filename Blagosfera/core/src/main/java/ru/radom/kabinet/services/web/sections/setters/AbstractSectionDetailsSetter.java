package ru.radom.kabinet.services.web.sections.setters;

import ru.askor.blagosfera.domain.section.SectionDomain;

public abstract class AbstractSectionDetailsSetter {

	public abstract void set(SectionDomain section, Long userId);
	public abstract String getSupportedSectionName();
	
}
