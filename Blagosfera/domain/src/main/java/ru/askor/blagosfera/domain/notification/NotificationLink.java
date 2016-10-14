package ru.askor.blagosfera.domain.notification;

import lombok.Data;

/**
 *
 * Created by vgusev on 16.04.2016.
 */
@Data
public class NotificationLink {

    private Long id;

    private String title;

    private String url;

    private boolean isAjax;

    private boolean isMarkAsRead;

    private NotificationLinkType type;

    private int position;

}
