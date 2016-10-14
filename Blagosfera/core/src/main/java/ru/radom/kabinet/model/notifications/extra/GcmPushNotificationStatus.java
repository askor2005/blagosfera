package ru.radom.kabinet.model.notifications.extra;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.ChatMessage;
import ru.radom.kabinet.model.notifications.NotificationEntity;

import javax.persistence.*;

/**
 * Статус push уведомления, по которму к gcm был сделан запрос.
 */
@Entity
@Table(name = "gcm_push_notification_statuses")
public class GcmPushNotificationStatus extends LongIdentifiable {

    //Исходное уведомление
    @JoinColumn(name = "notification_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private NotificationEntity notification;

    @JoinColumn(name = "chat_message_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatMessage chatMessage;

    //Номер устройства, которому предназначено уведомление
    @Column(name = "device_id", nullable = false)
    private String deviceId;

    //Получатель уведомления
    @JoinColumn(name = "sharer_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    //Флаг отправки данных уведомления на сторону клиента
    @Column(name = "is_pushed", nullable = false)
    private boolean isPushed;

    public NotificationEntity getNotification() {
        return notification;
    }

    public void setNotification(NotificationEntity notification) {
        this.notification = notification;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public UserEntity getSharer() {
        return user;
    }

    public void setSharer(UserEntity userEntity) {
        this.user = userEntity;
    }

    public boolean getIsPushed() {
        return isPushed;
    }

    public void setIsPushed(boolean isPushed) {
        this.isPushed = isPushed;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}
