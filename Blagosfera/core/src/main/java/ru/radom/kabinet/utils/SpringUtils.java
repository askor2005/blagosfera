package ru.radom.kabinet.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

@Component
public class SpringUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		if (applicationContext == null) {
			throw new IllegalAccessError("Контекст приложения еще не инициализирован");
		}
		return applicationContext;
	}

	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}
	
	public static <T> T getBean(String name, Class<T> clazz) {
		return getApplicationContext().getBean(name, clazz);
	}

	public static <T> T getBean(String name, Object... params) {
		return (T)getApplicationContext().getBean(name, params);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringUtils.applicationContext = applicationContext;
	}

	public static void copyProperties(Object source, Object destination) {
		try {
			BeanUtils.copyProperties(source, destination);
		} catch (Exception e) {
			ExceptionUtils.check(true, e.getMessage());
		}
	}

}
