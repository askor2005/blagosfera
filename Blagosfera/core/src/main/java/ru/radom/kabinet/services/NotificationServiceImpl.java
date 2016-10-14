package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.notification.Notification;
import ru.radom.kabinet.dao.notifications.NotificationDao;
import ru.radom.kabinet.dto.notification.NotificationDto;
import ru.radom.kabinet.services.notification.NotificationDomainService;

@Service("notificationService")
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private StompService stompService;

    @Autowired
    private NotificationDomainService notificationDomainService;

    public NotificationServiceImpl() {
    }

    @Override
    public Notification markAsRead(Long notificationId) {
        notificationDomainService.markAsRead(notificationId);
        Notification notification = notificationDomainService.getById(notificationId);
        stompService.send(notification.getUser().getEmail(), "mark_as_read_notification", new NotificationDto(notification));
        return notification;
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationDao.markAsReadAllNotificationsForSharer(userId);
    }

    @Override
    public Notification delete(Long notificationId) {
        Notification notification = notificationDomainService.delete(notificationId);
        stompService.send(notification.getUser().getEmail(), "delete_notification", new NotificationDto(notification));
        return notification;
    }

    @Override
    public Notification addNotification(Notification notification) {
        Notification savedNotification = notificationDomainService.save(notification);
        notification.setId(savedNotification.getId());
        if (!notification.isRead()) stompService.send(notification.getUser().getEmail(), "new_notification", new NotificationDto(notification));
        return notification;
    }
}