package ru.askor.blagosfera.data.jpa.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.askor.blagosfera.domain.notification.NotificationPriority;
import ru.radom.kabinet.model.notifications.NotificationEntity;

import java.util.Date;

/**
 *
 * Created by vgusev on 18.04.2016.
 */
public class NotificationSpecifications {

    public static Specification<NotificationEntity> getByUser(Long userId) {
        return (root, cq, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<NotificationEntity> betweenDate(Date startDate, Date endDate) {
        return (root, cq, cb) -> cb.between(root.get("date"), startDate, endDate);
    }

    public static Specification<NotificationEntity> getByPriority(NotificationPriority notificationPriority) {
        return (root, cq, cb) -> cb.equal(root.get("priority"), notificationPriority);
    }

    public static Specification<NotificationEntity> onlyRead() {
        return (root, cq, cb) -> cb.equal(root.get("read"), true);
    }

    public static Specification<NotificationEntity> onlyUnRead() {
        return (root, cq, cb) -> cb.equal(root.get("read"), false);
    }

}
