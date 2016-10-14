package ru.radom.kabinet.dao.discussion;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.askor.blagosfera.domain.RadomAccount;

import java.util.List;

@Repository("discussionDao")
public class DiscussionDao extends Dao<Discussion> {

    public List<Discussion> discussionsForAuthor(UserEntity author) {
        return find(getCriteria()
        		.add(Restrictions.eq("author.id", author))
        		.add(Restrictions.eq("scope", author))
        		.addOrder(Order.desc("id")));
    }

    public List<Discussion> discussionsAll() {
        return find(getCriteria().addOrder(Order.desc("id")));
    }

    public List<Discussion> discussionsForScope(RadomAccount scope) {
    	return find(getCriteria()
                .add(Restrictions.eq("scope", scope))
                .addOrder(Order.desc("id")));
    }
}
