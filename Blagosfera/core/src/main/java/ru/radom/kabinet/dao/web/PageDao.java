package ru.radom.kabinet.dao.web;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.askor.blagosfera.data.jpa.entities.cms.PageEntity;
import java.util.List;

@Repository("pageDao")
public class PageDao extends Dao<PageEntity> {

    /**
     * Найти страницы по названию
     * @param titleQuery
     * @return
     */
    public List<PageEntity> findPages(String titleQuery) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.ilike("title", titleQuery, MatchMode.ANYWHERE));
        return find(criteria);
    }

}
