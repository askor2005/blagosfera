package ru.radom.kabinet.hibernate;

import org.springframework.beans.factory.annotation.Autowired;
import ru.radom.kabinet.security.context.RequestContext;

public class RevisionListener implements org.hibernate.envers.RevisionListener {

	@Autowired
	private RequestContext radomRequestContext;

	@Override
	public void newRevision(Object revisionEntity) {
		// TODO-gusev Сохранение сущности может быть вне request треда что приводит к исключению получения request scope бина
		// TODO-gusev Надо переделать
		/*AutowireHelper.autowire(this, this.radomRequestContext);
		RevisionEntity entity = (RevisionEntity) revisionEntity;
		if (entity != null && radomRequestContext != null && radomRequestContext.getCurrentSharer() != null) {
			entity.setUser(radomRequestContext.getCurrentSharer());
		}*/
	}

}
