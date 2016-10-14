package ru.askor.blagosfera.data.jpa.repositories.notifications;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.notifications.extra.GcmDevice;

import java.util.List;


public interface GcmDeviceRepository extends JpaRepository<GcmDevice, Long> {

    GcmDevice findOneByDeviceId(String deviceId);

    List<GcmDevice> findAllByUserId(Long userId);
}
