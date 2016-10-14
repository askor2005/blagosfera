package ru.radom.kabinet.dao.news;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.news.NewsSubscribe;
import ru.askor.blagosfera.domain.RadomAccount;

import java.util.List;

@Repository("newsSubscribeDao")
public class NewsSubscribeDao extends Dao<NewsSubscribe> {

	public NewsSubscribe get(LongIdentifiable scope, UserEntity user) {
		return findFirst(Restrictions.eq("scope", scope), Restrictions.eq("user", user));
	}

	public List<NewsSubscribe> getList(RadomAccount scope) {
		return find(Restrictions.eq("scope", scope));
	}

}
