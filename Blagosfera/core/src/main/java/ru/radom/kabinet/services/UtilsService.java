package ru.radom.kabinet.services;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import ru.radom.kabinet.expressions.Functions;
import ru.radom.kabinet.utils.DiscussionUtils;

@Service("utilsService")
public class UtilsService implements ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		Functions.setApplicationContext(applicationContext);
		DiscussionUtils.setApplicationContext(applicationContext);
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

}
