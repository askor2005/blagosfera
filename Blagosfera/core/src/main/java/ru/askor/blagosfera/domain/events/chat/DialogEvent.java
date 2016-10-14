package ru.askor.blagosfera.domain.events.chat;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.DialogEntity;

public class DialogEvent extends BlagosferaEvent {

    private DialogEventType eventType;
    private DialogEntity dialog;
    private UserEntity userEntity;
    private Long senderId;

    public DialogEvent(Object source, DialogEventType eventType, DialogEntity dialog, Long senderId, UserEntity userEntity) {
        super(source);
        this.eventType = eventType;
        this.dialog = dialog;
        this.userEntity = userEntity;
        this.senderId = senderId;
    }

    public DialogEventType getEventType() {
        return eventType;
    }

    public DialogEntity getDialog() {
        return dialog;
    }

    public UserEntity getSharer() {
        return userEntity;
    }

    public Long getSenderId() {
        return senderId;
    }
}
