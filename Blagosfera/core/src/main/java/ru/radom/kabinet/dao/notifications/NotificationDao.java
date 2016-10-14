package ru.radom.kabinet.dao.notifications;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.notifications.NotificationEntity;
import ru.askor.blagosfera.domain.notification.NotificationPriority;
import ru.askor.blagosfera.domain.notification.NotificationType;
import ru.askor.blagosfera.domain.RadomAccount;

import java.util.Date;
import java.util.List;

@Repository("notificationDao")
public class NotificationDao extends Dao<NotificationEntity> {

	public List<NotificationEntity> getList(Long userId, RadomAccount sender, Date startDate, Date endDate, boolean includeRead, boolean includeUnread, NotificationPriority priority, int firstResult, int maxResults) {
		Conjunction conjunction = new Conjunction();

		conjunction.add(Restrictions.eq("user.id", userId));
		if (sender != null) {
			conjunction.add(Restrictions.eq("sender", sender));
		}
		if (startDate != null) {
			conjunction.add(Restrictions.ge("date", startDate));
		}
		if (endDate != null) {
			conjunction.add(Restrictions.le("date", endDate));
		}
		if (!includeRead) {
			conjunction.add(Restrictions.eq("read", false));
		}
		if (!includeUnread) {
			conjunction.add(Restrictions.eq("read", true));
		}
		if (priority != null) {
			conjunction.add(Restrictions.eq("priority", priority));
		}
		return find(Order.desc("date"), firstResult, maxResults, conjunction);
	}
	
	public NotificationEntity get(NotificationType type, Object object, boolean read) {
		return findFirst(Restrictions.eq("type", type), Restrictions.eq("object", object), Restrictions.eq("read", read));
	}

	/**
	 * Получить оповещение по ссылке и участнику
	 * @param userId
	 * @param link
	 * @param read
	 * @return
	 */
	public List<NotificationEntity> getList(Long userId, String link, boolean read) {
		Criteria criteria = getCriteria().createAlias("links", "notifyLink")
				.add(Restrictions.eq("notifyLink.url", link))
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("read", read));
		return find(criteria);
	}

	private static final String MARK_AS_READ_ALL_NOTIFICATIONS_FOR_SHARER_SQL =
			"UPDATE notifications SET read = true WHERE sharer_id = :sharer_id and read = false;";

	public void markAsReadAllNotificationsForSharer(Long userId) {
		Query query = getCurrentSession().createSQLQuery(MARK_AS_READ_ALL_NOTIFICATIONS_FOR_SHARER_SQL).setLong("sharer_id", userId);
		query.executeUpdate();
	}
	
}
