package ru.radom.kabinet.services.notification;

import ru.askor.blagosfera.domain.notification.NotificationLink;
import ru.radom.kabinet.model.notifications.NotificationLinkEntity;
import ru.askor.blagosfera.domain.notification.NotificationLinkType;

/**
 * Обёртка ссылок для оповещения по почте и в системе
 * Created by vgusev on 16.09.2015.
 */
public class NotifyLink extends NotificationLink {

    public NotifyLink(String title, String url, boolean ajax, boolean makrAsRead, NotificationLinkType type, int position, String code) {
        this(title, url, ajax, makrAsRead, type, position);
        this.code = code;
    }

    public NotifyLink(String title, String url, boolean ajax, boolean makrAsRead, NotificationLinkType type, int position) {
        setTitle(title);
        setUrl(url);
        setAjax(ajax);
        setMarkAsRead(makrAsRead);
        setType(type);
        setPosition(position);
    }

    private String code;

    public String getCode() {
        return code;
    }
}
