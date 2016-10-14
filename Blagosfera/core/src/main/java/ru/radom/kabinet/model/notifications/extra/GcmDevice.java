package ru.radom.kabinet.model.notifications.extra;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;

/**
 * Зарегистрированное в сервисе GCM устройство.
 */
@Entity
@Table(name = "gcm_devices")
public class GcmDevice extends LongIdentifiable {

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @JoinColumn(name = "sharer_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
