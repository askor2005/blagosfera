package ru.radom.kabinet.services.notification;

import ru.radom.kabinet.model.notifications.extra.GcmPushNotificationStatus;

import java.util.List;

public interface PushToDevicesCallback {

    void pushToDevices(List<String> deviceIds, String apiKey, List<GcmPushNotificationStatus> statuses);
}
