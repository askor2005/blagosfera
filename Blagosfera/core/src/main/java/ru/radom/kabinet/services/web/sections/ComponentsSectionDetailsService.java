/**
 * 
 */
package ru.radom.kabinet.services.web.sections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.radom.kabinet.services.web.sections.setters.AbstractSectionDetailsSetter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rkorablin
 *
 */
@Service
public class ComponentsSectionDetailsService implements SectionsDetailService, InitializingBean, ApplicationContextAware {

	
	private static final Logger logger = LoggerFactory.getLogger(ComponentsSectionDetailsService.class);
	
	private static final Map<String, AbstractSectionDetailsSetter> SETTERS = new HashMap<String, AbstractSectionDetailsSetter>();

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void setDetails(SectionDomain section, Long userId) {
		AbstractSectionDetailsSetter setter = SETTERS.get(section.getName());
		if (setter != null) {
			setter.set(section, userId);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
		scanner.addIncludeFilter(new AssignableTypeFilter(AbstractSectionDetailsSetter.class));
		for (BeanDefinition beanDefinition : scanner.findCandidateComponents("ru.radom.kabinet.services.web.sections.setters")) {
			ScannedGenericBeanDefinition scannedGenericBeanDefinition = (ScannedGenericBeanDefinition) beanDefinition;
			String setterClassName = scannedGenericBeanDefinition.getBeanClassName();
			Class setterClass = Class.forName(setterClassName);
			Object bean = applicationContext.getBean(setterClass);
			AbstractSectionDetailsSetter setter = (AbstractSectionDetailsSetter) bean;
			SETTERS.put(setter.getSupportedSectionName(), setter);
			logger.info(setterClassName + " found");
		}

	}

}
