package ru.radom.kabinet.dao.news;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.radom.kabinet.model.news.News;
import ru.askor.blagosfera.domain.RadomAccount;

import java.util.List;

@Repository("newsDao")
public class NewsDao extends Dao<News> {

	private static final Logger logger = LoggerFactory.getLogger(NewsDao.class);

	public News getById(Long id, Boolean deleted) {
		News news = super.getById(id);
		return news != null && !news.getDeleted() ? news : null;
	}

	public News getById(Long id) {
		return this.getById(id, false);
	}

	public List<News> getBySharer(UserEntity userEntity, News lastLoaded, int maxResults, Boolean deleted) {
		if (deleted == null) {
			return createSQLQuery("select * from news as n left join news_subscribes as ns on (n.scope_type = ns.scope_type) and (n.scope_id = ns.scope_id) where (ns.sharer_id = :sharer_id) and (n.id < :last_id) order by date desc limit :limit").addEntity("n", News.class).setLong("sharer_id", userEntity.getId()).setLong("last_id", lastLoaded != null && lastLoaded.getId() != null ? lastLoaded.getId() : Long.MAX_VALUE).setInteger("limit", maxResults).list();
		} else {
			return createSQLQuery("select * from news as n left join news_subscribes as ns on (n.scope_type = ns.scope_type) and (n.scope_id = ns.scope_id) where (ns.sharer_id = :sharer_id) and (n.id < :last_id) and (n.deleted = :deleted) order by date desc limit :limit").addEntity("n", News.class).setLong("sharer_id", userEntity.getId()).setLong("last_id", lastLoaded != null && lastLoaded.getId() != null ? lastLoaded.getId() : Long.MAX_VALUE).setInteger("limit", maxResults).setBoolean("deleted", deleted).list();
		}
	}

	public List<News> getBySharer(UserEntity userEntity, News lastLoaded, int maxResults) {
		return this.getBySharer(userEntity, lastLoaded, maxResults, false);
	}

	public List<News> getByAuthor(RadomAccount author, News lastLoaded, Integer perPage, Boolean deleted) {

		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("author", author));
		if (lastLoaded != null) {
			conjunction.add(Restrictions.lt("id", lastLoaded.getId()));
		}
		if (deleted != null) {
			conjunction.add(Restrictions.eq("deleted", deleted));
		}
		return find(Order.desc("id"), 0, perPage, conjunction);
	}

	public List<News> getByAuthor(RadomAccount author, News lastLoaded, Integer perPage) {
		return this.getByAuthor(author, lastLoaded, perPage, false);
	}

	public List<News> getByScope(LongIdentifiable scope, News lastLoaded, Integer perPage) {
		return getByScope(scope, lastLoaded, perPage, false, false);
	}

	public List<News> getByScope(LongIdentifiable scope, News lastLoaded, Integer perPage, boolean excludeModerated) {
		return this.getByScope(scope, lastLoaded, perPage, excludeModerated, false);
	}

	public List<News> getByScope(LongIdentifiable scope, News lastLoaded, Integer perPage, boolean excludeModerated, Boolean deleted) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("scope", scope));
		if (lastLoaded != null) {
			conjunction.add(Restrictions.lt("id", lastLoaded.getId()));
		}
		if (deleted != null) {
			conjunction.add(Restrictions.eq("deleted", deleted));
		}
		if (excludeModerated) {
			conjunction.add(Restrictions.eq("moderated", false));
		}
		return find(Order.desc("id"), 0, perPage, conjunction);
	}

	public News getByDiscussion(Discussion discussion) {
		return findFirst(Restrictions.eq("discussion", discussion));
	}
}
