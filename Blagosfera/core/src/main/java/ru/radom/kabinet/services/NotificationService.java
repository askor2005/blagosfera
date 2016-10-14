package ru.radom.kabinet.services;

import ru.askor.blagosfera.domain.notification.Notification;

/**
 * Created by mnikitin on 25.05.2016.
 */
public interface NotificationService {

    Notification markAsRead(Long notificationId);

    void markAllAsRead(Long userId);

    Notification delete(Long notificationId);

    Notification addNotification(Notification notification);
}
