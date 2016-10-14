package ru.radom.kabinet.web.chat.dto;

import ru.radom.kabinet.model.ContactEntity;
import ru.radom.kabinet.model.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 24.11.2015.
 */
public class ContactDto {

    /**
     * ИД участника у контакта
     */
    private Long id;
    /**
     * ИКП участника
     */
    private String ikp;
    /**
     * Ссылка на участника у контакта
     */
    private String link;
    /**
     * Аватар участника у контакта
     */
    private String avatar;
    /**
     *  Полное имя участника у контакта
     */
    private String fullName;

    /**
     *
     */
    private boolean online;

    /**
     * Количество непрочитанных сообщений от контакта
     */
    private int countMessages;

    /**
     * ИД диалога
     */
    private Long dialogId;

    public ContactDto() {
    }

    public ContactDto(Long id, String ikp, String link, String avatar, String fullName) {
        this.id = id;
        this.ikp = ikp;
        this.link = link;
        this.avatar = avatar;
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIkp() {
        return ikp;
    }

    public void setIkp(String ikp) {
        this.ikp = ikp;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getCountMessages() {
        return countMessages;
    }

    public void setCountMessages(int countMessages) {
        this.countMessages = countMessages;
    }

    public Long getDialogId() {
        return dialogId;
    }

    public void setDialogId(Long dialogId) {
        this.dialogId = dialogId;
    }

    public static final List<ContactDto> toDtoList(List<ContactEntity> contacts) {
        List<ContactDto> result = new ArrayList<>();
        for (ContactEntity contact : contacts) {result.add(toDto(contact));}
        return result;
    }

    public static final ContactDto toDto(ContactEntity contact) {
        UserEntity other = contact.getOther();
        return new ContactDto(other.getId(), other.getIkp(), other.getLink(), other.getAvatar(), other.getFullName());
    }
}
