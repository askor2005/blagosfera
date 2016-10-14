package ru.askor.blagosfera.data.jpa.repositories.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.notifications.NotificationLinkEntity;

/**
 *
 * Created by vgusev on 14.04.2016.
 */
public interface NotificationLinkRepository extends JpaRepository<NotificationLinkEntity, Long> {

}
