package ru.radom.kabinet.dao.web;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.web.CommunitySection;

import java.util.LinkedList;
import java.util.List;

@Repository("communitySectionDao")
public class CommunitySectionDao extends Dao<CommunitySection> {

	public List<CommunitySection> getRoots() {
		return find(Order.asc("position"), Restrictions.isNull("parent"));
	}

	public CommunitySection getByLink(String link) {
		return findFirst(Restrictions.eq("link", link));
	}

	public List<CommunitySection> getHierarchy(CommunitySection section) {
		LinkedList<CommunitySection> hierarchy = new LinkedList<CommunitySection>();
		while (section != null) {
			hierarchy.addFirst(section);
			section = section.getParent();
		}
		return hierarchy;
	}
	
}
