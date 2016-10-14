package ru.radom.kabinet.dto.notification;

import lombok.Data;
import ru.askor.blagosfera.domain.notification.Notification;

import java.util.List;

/**
 *
 * Created by vgusev on 17.04.2016.
 */
@Data
public class NotificationsDto {

    private List<NotificationDto> notifications;

    private int count;

    public NotificationsDto(List<Notification> notifications, int count) {
        setNotifications(NotificationDto.toDtoList(notifications));
        setCount(count);
    }
}
