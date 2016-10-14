package ru.radom.kabinet.dao.web;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.applications.Application;
import ru.askor.blagosfera.data.jpa.entities.cms.PageEntity;
import ru.radom.kabinet.model.web.Section;
import ru.radom.kabinet.utils.StringUtils;

import java.util.*;

@Repository("sectionDao")
public class SectionDao extends Dao<Section> {

	/**
	 * Получить корневые разделы у которых есть возможность установить альтернативную ссылку контента
	 * @return
	 */
	public List<Section> getRootsWithEditableForwardUrl() {
		return find(
				Order.asc("position"),
				Restrictions.isNull("parent"),
				Restrictions.eq("canSetForwardUrl", true)
				);
	}

	public List<Section> getRoots() {
		return find(Order.asc("position"), Restrictions.isNull("parent")/*, Restrictions.ne("name", "admin"), Restrictions.eq("published", true)*/);
	}

	public List<Section> getHierarchy(Section section) {
		// TODO переделать с использованием рекурсивного запроса
		LinkedList<Section> hierarchy = new LinkedList<Section>();
		while (section != null) {
			hierarchy.addFirst(section);
			section = section.getParent();
		}
		return hierarchy;
	}

	public Section getByLink(String link) {
		// TODO добавить индекс
		return findFirst(Restrictions.eq("link", link));
	}

	public Section getByName(String name) {
		// TODO добавить индекс
		return findFirst(Restrictions.eq("name", name));
	}

	public Section getByPage(PageEntity page) {
		return findFirst(Restrictions.eq("page", page));
	}

	public List<Section> getByLinkPrefix(String prefix) {
		return find(Restrictions.like("link", prefix, MatchMode.START));
	}

	public Section getByApplication(Application application) {
		return findFirst(Restrictions.eq("application", application));
	}
	
	public Map<Application, Section> getApplicationsSectionsMap(List<Application> applications) {
		List<Section> sections = applications.isEmpty() ? Collections.EMPTY_LIST : find(Restrictions.in("application", applications));
		Map<Application, Section> map = new HashMap<Application, Section>();
		for (Application application : applications) {
			Section applicationSection = null;
			for (Section section : sections) {
				if (section.getApplication().equals(application)) {
					applicationSection = section;
				}
			}
			map.put(application, applicationSection);
		}
		return map;
	}

	public List<Section> getList(Section parent, String query) {
		Criteria criteria = getCriteria(Order.asc("position"), 0, Integer.MAX_VALUE);
		if (parent != null) {
			criteria.add(Restrictions.eq("parent", parent));
		} else {
			criteria.add(Restrictions.isNull("parent"));
		}
		if (StringUtils.hasLength(query)) {
			criteria.add(Restrictions.ilike("title", query, MatchMode.ANYWHERE));
		}
		return find(criteria);
	}

	public int determineFirstPosition(Long sectionId) {
		return (int) createSQLQuery("select coalesce((min(position) - 1), 0) as p from sections where parent_id = :parent_id").addScalar("p", IntegerType.INSTANCE).setLong("parent_id", sectionId).uniqueResult();
	}

	public int determineLastPosition(Long sectionId) {
		return (int) createSQLQuery("select coalesce((max(position) + 1), 0) as p from sections where parent_id = :parent_id").addScalar("p", IntegerType.INSTANCE).setLong("parent_id", sectionId).uniqueResult();
	}
	
}
