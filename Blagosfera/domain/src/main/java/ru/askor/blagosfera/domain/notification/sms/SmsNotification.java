package ru.askor.blagosfera.domain.notification.sms;

/**
 * Created by mnikitin on 15.06.2016.
 */
public class SmsNotification {

    private SmsNotificationType type;
    private String tel;
    private String text;
    private String checksum;

    public SmsNotification() {
    }

    public SmsNotification(SmsNotificationType type, String tel, String text) {
        this(type, tel, text, null);
    }

    public SmsNotification(SmsNotificationType type, String tel, String text, String checksum) {
        this.type = type;
        this.tel = tel.trim().replaceAll(" ", "").replaceAll("-", "");
        this.text = text;
        this.checksum = checksum;
    }

    public String getDataAsString() {
        return type.getName() + tel + text;
    }

    public SmsNotificationType getType() {
        return type;
    }

    public void setType(SmsNotificationType type) {
        this.type = type;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel.trim().replaceAll(" ", "").replaceAll("-", "");
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
