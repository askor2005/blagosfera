package ru.askor.blagosfera.data.jpa.repositories.notification;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.askor.blagosfera.domain.notification.NotificationPriority;
import ru.radom.kabinet.model.chat.ChatMessageReceiver;
import ru.radom.kabinet.model.notifications.NotificationEntity;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 14.04.2016.
 */
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>, JpaSpecificationExecutor<NotificationEntity> {

    @Query(
        "select n from NotificationEntity n where n.user.id = :userId and n.date between :startDate and :endDate " +
        "and ((n.read = true and true = :isIncludeRead) or (n.read = false and true = :isIncludeUnread)) " +
        "and n.priority = :priority order by n.date desc"
    )
    List<NotificationEntity> getList(
            @Param("userId") Long userId, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
            @Param("isIncludeRead") boolean isIncludeRead, @Param("isIncludeUnread") boolean isIncludeUnread,
            @Param("priority") NotificationPriority priority, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update NotificationEntity n set n.read = true where n.id = :notificationId")
    void markAsRead(@Param("notificationId") Long notificationId);

    @Query("select count(n) from NotificationEntity n where n.user.id = :userId and n.read = false")
    int getUnreadCount(@Param("userId") Long userId);

    @Query("select count(n) from NotificationEntity n where n.user.id = :userId and n.read = false and n.priority = :notificationPriority")
    int getUnreadCount(@Param("userId") Long userId, @Param("notificationPriority") NotificationPriority notificationPriority);
}
