package ru.radom.kabinet.dao;

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
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.LongIdentifiable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class DaoManager implements InitializingBean, ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(DaoManager.class);

	private Map<Class<? extends LongIdentifiable>, Dao> MAP = new HashMap<Class<? extends LongIdentifiable>, Dao>();

	private ApplicationContext applicationContext;

	@Override
	public void afterPropertiesSet() throws Exception {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Repository.class));
		scanner.addIncludeFilter(new AssignableTypeFilter(Dao.class));
		for (BeanDefinition beanDefinition : scanner.findCandidateComponents("ru.radom.kabinet.dao")) {
			ScannedGenericBeanDefinition scannedGenericBeanDefinition = (ScannedGenericBeanDefinition) beanDefinition;
			String daoClassName = scannedGenericBeanDefinition.getBeanClassName();
			Class daoClass = Class.forName(daoClassName);
			Object daoBean = applicationContext.getBean(daoClass);
			try {
				Dao dao = (Dao) daoBean;
				Class<? extends LongIdentifiable> persistentClass = dao.getPersistentClass();
				MAP.put(persistentClass, dao);
				logger.info("DAO for " + persistentClass.getName() + " found");
			} catch(ClassCastException ex) {
				logger.debug("Skipping AbstractDao instance");
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public Dao<?> getDao(Class<? extends LongIdentifiable> clazz) {
		return MAP.get(clazz);
	}

	public Set<Class<? extends LongIdentifiable>> getPersistentClasses() {
		return Collections.unmodifiableSet(MAP.keySet());
	}

}
