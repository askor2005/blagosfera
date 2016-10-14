package ru.radom.kabinet.dto.notification;

import lombok.Data;

/**
 *
 * Created by vgusev on 17.04.2016.
 */
@Data
public class NotificationPageDataDto {

    private boolean isHasUnreadBlockingNotifications;

    public NotificationPageDataDto(boolean isHasUnreadBlockingNotifications) {
        this.isHasUnreadBlockingNotifications = isHasUnreadBlockingNotifications;
    }
}
