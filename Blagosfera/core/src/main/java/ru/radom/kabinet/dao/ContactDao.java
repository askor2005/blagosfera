package ru.radom.kabinet.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.*;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.ContactEntity;
import ru.radom.kabinet.model.ContactStatus;
import ru.radom.kabinet.model.ContactsGroupEntity;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.web.chat.dto.ContactDto;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@Repository("contactDao")
public class ContactDao extends Dao<ContactEntity> {

    @Autowired
    private SharerDao sharerDao;

	public ContactEntity getBySharers(Long userId, Long otherId) {
		return findFirst(Restrictions.eq("user.id", userId), Restrictions.eq("other.id", otherId));
	}

	public List<ContactEntity> getOnline(UserEntity userEntity) {
		return getContacts(userEntity, true);
	}

	/**
	 * Получить контакты пользователя, если <code>online != null</code> то проверяется статус
	 */
	public List<ContactEntity> getContacts(UserEntity user, Boolean online) {
		Criteria criteria = getCriteria();
		criteria.createAlias("other", "otherAlias");
		criteria.add(Restrictions.eq("user", user));
		criteria.add(Restrictions.eq("sharerStatus", ContactStatus.ACCEPTED));
		criteria.add(Restrictions.eq("otherStatus", ContactStatus.ACCEPTED));
		if(online != null) {
			//criteria.add(Restrictions.eq("otherAlias.online", online));
		}
		return find(criteria);
	}

	/**
	 * Поиск контактов, которые не удалены
	 * @param user
	 * @param online
	 * @param deleted
	 * @return
	 */
	public List<ContactEntity> getContacts(Long userId, Boolean online, boolean deleted) {
		Criteria criteria = getCriteria();
		criteria.createAlias("other", "otherAlias");
		criteria.add(Restrictions.eq("otherAlias.deleted", deleted));
		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("sharerStatus", ContactStatus.ACCEPTED));
		criteria.add(Restrictions.eq("otherStatus", ContactStatus.ACCEPTED));
		if(online != null) {
			//criteria.add(Restrictions.eq("otherAlias.online", online));
		}
		return find(criteria);
	}

	public List<ContactEntity> getList(Long userId, Collection<UserEntity> others) {
		return find(Restrictions.eq("user.id", userId), Restrictions.in("other", others));
	}

	public Criteria getSearchCriteria(UserEntity user, String query, ContactStatus sharerStatus, ContactStatus otherStatus, ContactsGroupEntity group) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.eq("user", user));
		if (sharerStatus != null) {
			conjunction.add(Restrictions.eq("sharerStatus", sharerStatus));
		}
		if (otherStatus != null) {
			conjunction.add(Restrictions.eq("otherStatus", otherStatus));
		}
		if (group != null) {
			if (group.getId() != null) {
				conjunction.add(Restrictions.eq("contactsGroup", group));
			} else {
				conjunction.add(Restrictions.isNull("contactsGroup"));
			}
		}
		if (StringUtils.hasLength(query)) {
			conjunction.add(Restrictions.ilike("otherAlias.searchString", query, MatchMode.ANYWHERE));
		}
		conjunction.add(Restrictions.eq("otherAlias.deleted", false));

		return getCriteria().createAlias("other", "otherAlias").add(conjunction);
	}
	
	public List<ContactEntity> searchContacts(Long userId, String query, int firstResult, int maxResults, ContactStatus sharerStatus, ContactStatus otherStatus, ContactsGroupEntity group, String orderBy, boolean asc) {
        UserEntity userEntity;
        if (userId == null)
            userEntity = sharerDao.getById(SecurityUtils.getUser().getId());
        else
            userEntity = sharerDao.getById(userId);

        return find(getSearchCriteria(userEntity, query, sharerStatus, otherStatus, group)
				.setFirstResult(firstResult)
				.setMaxResults(maxResults)
				.addOrder(asc ? Order.asc("otherAlias." + orderBy) : Order.desc("otherAlias." + orderBy)));
	}

	public long searchContactsCount(UserEntity userEntity, String query, ContactStatus sharerStatus, ContactStatus otherStatus, ContactsGroupEntity group) {
		return (long) getSearchCriteria(userEntity, query, sharerStatus, otherStatus, group).setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public long getNewRequestsCount(Long userId) {


		return (long) getCriteria()
				.createAlias("other", "otherAlias")
				.add(Restrictions.eq("otherAlias.deleted", false))
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("sharerStatus", ContactStatus.NEW))
				.add(Restrictions.eq("otherStatus", ContactStatus.ACCEPTED))
				.setProjection(Projections.rowCount()).uniqueResult();
	}

	/*public long getDefaultGroupCount(Long userId) {
		return (long) getCriteria().createAlias("other", "otherAlias").add(Restrictions.eq("otherAlias.deleted", false)).add(Restrictions.eq("user.id", userId)).add(Restrictions.isNull("contactsGroup")).add(Restrictions.eq("sharerStatus", ContactStatus.ACCEPTED)).add(Restrictions.eq("otherStatus", ContactStatus.ACCEPTED)).setProjection(Projections.rowCount()).uniqueResult();
	}*/

	public long getContactsCount(UserEntity userEntity) {
		return searchContactsCount(userEntity, null, ContactStatus.ACCEPTED, ContactStatus.ACCEPTED, null);
	}
}
