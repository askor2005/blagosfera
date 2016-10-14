package ru.radom.kabinet.dto.notification;

import lombok.Data;
import ru.askor.blagosfera.domain.notification.NotificationSender;

/**
 *
 * Created by vgusev on 17.04.2016.
 */
@Data
public class NotificationSenderDto {

    private String name;

    private String link;

    private String avatar;

    public NotificationSenderDto(NotificationSender notificationSender) {
        setName(notificationSender.getName());
        setLink(notificationSender.getLink());
        setAvatar(notificationSender.getAvatar());
    }

    public static NotificationSenderDto toDtoSafe(NotificationSender notificationSender) {
        NotificationSenderDto result = null;
        if (notificationSender != null) {
            result = new NotificationSenderDto(notificationSender);
        }
        return result;
    }
}
