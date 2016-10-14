package ru.radom.kabinet.dto.notification;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.askor.blagosfera.domain.notification.Notification;
import ru.askor.blagosfera.domain.notification.NotificationPriority;
import ru.askor.blagosfera.domain.notification.NotificationType;
import ru.radom.kabinet.json.FullDateSerializer;

import java.util.*;

/**
 * 
 * Created by vgusev on 17.04.2016.
 */
public class NotificationDto {

    private Long id;
    private String subject;
    private String shortText;
    private String text;
    private NotificationPriority priority;
    @JsonSerialize(using = FullDateSerializer.class)
    private Date date;
    private boolean isRead;
    private List<NotificationLinkDto> links = new ArrayList<>();
    private NotificationSenderDto sender;
    private NotificationType type;
    private Map<String, String> data = new HashMap<>();

    public NotificationDto(Notification notification) {
        setId(notification.getId());
        setSubject(notification.getSubject());
        setShortText(notification.getShortText());
        setText(notification.getText());
        setPriority(notification.getPriority());
        setDate(notification.getDate());
        setRead(notification.isRead());
        getLinks().addAll(NotificationLinkDto.toDtoList(notification.getLinks()));
        setSender(NotificationSenderDto.toDtoSafe(notification.getSender()));
        setType(notification.getType());
        getData().putAll(notification.getData());
    }

    public static List<NotificationDto> toDtoList(List<Notification> notifications) {
        List<NotificationDto> result = null;
        if (notifications != null) {
            result = new ArrayList<>();
            for (Notification notification : notifications) {
                result.add(new NotificationDto(notification));
            }
        }
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public List<NotificationLinkDto> getLinks() {
        return links;
    }

    public NotificationSenderDto getSender() {
        return sender;
    }

    public void setSender(NotificationSenderDto sender) {
        this.sender = sender;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Map<String, String> getData() {
        return data;
    }
}
