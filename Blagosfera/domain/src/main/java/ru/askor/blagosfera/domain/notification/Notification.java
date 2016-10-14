package ru.askor.blagosfera.domain.notification;

import ru.askor.blagosfera.domain.user.User;

import java.util.*;

/**
 *
 * Created by vgusev on 14.04.2016.
 */
public class Notification {

    private Long id;
    private User user;
    private NotificationSender sender;
    private boolean isRead;
    private NotificationPriority priority;
    private String subject;
    private String shortText;
    private String text;
    private Date date;
    private List<NotificationLink> links = new ArrayList<>();
    private NotificationType type;
    private Map<String, String> data = new HashMap<>();

    public Notification() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public NotificationSender getSender() {
        return sender;
    }

    public void setSender(NotificationSender sender) {
        this.sender = sender;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<NotificationLink> getLinks() {
        return links;
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
