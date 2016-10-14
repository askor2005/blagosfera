package ru.radom.kabinet.dao.notifications;


import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.notifications.extra.GcmPushNotificationStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.util.List;

@Repository("GcmPushNotificationStatusDao")
public class GcmPushNotificationStatusDao extends Dao<GcmPushNotificationStatus> {

    @PersistenceContext(unitName = "kabinetPU", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    /**
     * Позволяет найти последнее непрочитанное и не показанное push ведомление для указанного получателя и устройства.
     *
     * @param user   получатель
     * @param deviceId идентификатор устройства
     * @return GcmPushNotificationStatus или null, если сущность не была найдена
     */
    public GcmPushNotificationStatus getLastUnreadAndNotShowed(UserEntity user, String deviceId) {
        List<GcmPushNotificationStatus> statuses = em.createQuery("SELECT g FROM GcmPushNotificationStatus AS g WHERE " +
                "g.user = :user AND g.deviceId = :deviceId AND g.isPushed = false " +
                "AND ( g.chatMessage IS NOT NULL OR g.notification IN (SELECT n FROM NotificationEntity AS n WHERE n.read = false )) " +
                "ORDER BY g.id DESC", getPersistentClass())
                .setParameter("user", user)
                .setParameter("deviceId", deviceId)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();

        if (statuses == null || statuses.isEmpty()) {
            return null;
        } else {
            return statuses.get(0);
        }
    }


    /**
     * Отмечает все push уведомления как отправленные
     * по идентификатору исходного уведомления
     *
     * @param notificationId
     */
    public void setIsPushedForAllByNotificationId(Long notificationId) {
        em.createQuery("UPDATE GcmPushNotificationStatus g SET g.isPushed = true " +
                "WHERE g.notification.id = :notificationId AND g.isPushed = false")
                .setParameter("notificationId", notificationId)
                .executeUpdate();
    }


    /**
     * Отмечает все push уведомления как отправленные
     * по идентификаторам сообщения чата и пользователя*
     * @param chatMessageId
     * @param sharerId
     */
    public void setIsPushedForAllByChatMessageIdAndSharerId(Long chatMessageId, Long sharerId) {
        em.createQuery("UPDATE GcmPushNotificationStatus g SET g.isPushed = true " +
                "WHERE g.chatMessage.id = :chatMessageId AND g.sharer.id = :sharerId AND g.isPushed = false")
                .setParameter("chatMessageId", chatMessageId)
                .setParameter("sharerId", sharerId)
                .executeUpdate();
    }

    /**
     * Отмечает все уведомления пользователя как отправленные
     * по переданной в параметрах теме уведомления
     * @param userId
     * @param subject
     */
    public void setIsPushedAllStatusesBySharerAndNotification(Long userId, String subject) {
        em.createQuery("UPDATE GcmPushNotificationStatus g SET g.isPushed = true " +
                "WHERE g.user.id = :userId AND g.isPushed = false " +
                "AND g.notification IN (SELECT n FROM NotificationEntity AS n WHERE n.subject = :subject)")
                .setParameter("userId", userId)
                .setParameter("subject", subject)
                .executeUpdate();
    }


    /**
     * Отмечает все уведомления пользователя о сообщениях в чат, как отправленные.
     * @param userId
     */
    public void setIsPushedForAllChatStatusesBySharer(Long userId) {
        em.createQuery("UPDATE GcmPushNotificationStatus g SET g.isPushed = true " +
                "WHERE g.user.id = :userId AND g.chatMessage IS NOT NULL AND g.isPushed = false")
                .setParameter("userId", userId)
                .executeUpdate();
    }

}
