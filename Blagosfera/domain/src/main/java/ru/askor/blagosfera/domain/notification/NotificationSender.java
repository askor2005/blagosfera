package ru.askor.blagosfera.domain.notification;

/**
 *
 * Created by vgusev on 14.04.2016.
 */
public interface NotificationSender {

    Long getId();

    String getName();

    String getAvatar();

    String getIkp();

    String getLink();

}
