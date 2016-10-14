package ru.radom.kabinet.dao.discussion;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.discussion.DiscussionTopic;

import java.util.List;

@Repository
public class DiscussionTopicDao extends Dao<DiscussionTopic> {
	public List<DiscussionTopic> findByParent(DiscussionTopic topic) {
		return find(Restrictions.eq("parent", topic));
	}
	
	public List<DiscussionTopic> findTop() {
		return find(Restrictions.isNull("parent"));
	}
}
