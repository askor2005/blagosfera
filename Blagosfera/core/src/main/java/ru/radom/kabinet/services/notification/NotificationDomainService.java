package ru.radom.kabinet.services.notification;

import ru.askor.blagosfera.domain.notification.Notification;
import ru.askor.blagosfera.domain.notification.NotificationPriority;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 14.04.2016.
 */
public interface NotificationDomainService {

    Notification getById(Long id);

    Notification save(Notification notification);

    Notification delete(Long id);

    List<Notification> getList(Long userId, Date startDate, Date endDate,
                               boolean isIncludeRead, boolean isIncludeUnread,
                               NotificationPriority priority, int page, int perPage);

    List<Notification> getLastUnreadNotifications(Long userId, int count);

    void markAsRead(Long id);

    int getUnreadCount(Long userId);

    int getUnreadBlockingCount(Long userId);
}
