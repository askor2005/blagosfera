package ru.radom.kabinet.dto.notification;

import lombok.Data;
import ru.askor.blagosfera.domain.notification.NotificationLink;
import ru.askor.blagosfera.domain.notification.NotificationLinkType;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by vgusev on 17.04.2016.
 */
@Data
public class NotificationLinkDto {

    private Long id;
    private String title;
    private String url;
    private boolean isAjax;
    private boolean isMarkAsRead;
    private NotificationLinkType type;
    private int position;

    public NotificationLinkDto(NotificationLink notificationLink) {
        setId(notificationLink.getId());
        setTitle(notificationLink.getTitle());
        setUrl(notificationLink.getUrl());
        setAjax(notificationLink.isAjax());
        setMarkAsRead(notificationLink.isMarkAsRead());
        setType(notificationLink.getType());
        setPosition(notificationLink.getPosition());
    }

    public static NotificationLinkDto toDtoSafe(NotificationLink notificationLink) {
        NotificationLinkDto result = null;
        if (notificationLink != null) {
            result = new NotificationLinkDto(notificationLink);
        }
        return result;
    }

    public static List<NotificationLinkDto> toDtoList(List<NotificationLink> notificationLinks) {
        List<NotificationLinkDto> result = null;
        if (notificationLinks != null) {
            result = new ArrayList<>();
            for (NotificationLink notificationLink : notificationLinks) {
                result.add(toDtoSafe(notificationLink));
            }
        }
        return result;
    }
}
