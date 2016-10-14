package ru.askor.blagosfera.domain.events.notification;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.radom.kabinet.model.notifications.extra.GcmPushNotificationStatus;
import ru.radom.kabinet.services.notification.PushToDevicesCallback;

import java.util.List;

public class PushToDevicesCallbackEvent extends BlagosferaEvent {

    private List<String> deviceIds;
    private String apiKey;
    private List<GcmPushNotificationStatus> statuses;
    private PushToDevicesCallback callback;

    public PushToDevicesCallbackEvent(Object source, List<String> deviceIds, String apiKey, List<GcmPushNotificationStatus> statuses, PushToDevicesCallback callback) {
        super(source);
        this.deviceIds = deviceIds;
        this.apiKey = apiKey;
        this.statuses = statuses;
        this.callback = callback;
    }

    public void doCallback() {
        callback.pushToDevices(deviceIds, apiKey, statuses);
    }
}
