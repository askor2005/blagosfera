package ru.askor.blagosfera.domain.notification.sms;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by mnikitin on 15.06.2016.
 */
public enum SmsNotificationType {

    SMS("sms"),
    VIBER("viber"),
    WHATSAPP("whatsapp"),
    TELEGRAM("telegram");

    private String name;

    SmsNotificationType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
