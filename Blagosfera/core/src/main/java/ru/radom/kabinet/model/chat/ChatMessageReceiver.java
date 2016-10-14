package ru.radom.kabinet.model.chat;

import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;

/**
 *
 * Created by vgusev on 02.10.2015.
 */
@Entity
@Table(name = "chat_message_receivers")
public class ChatMessageReceiver extends LongIdentifiable {

    /**
     * Получатель сообщения
     */
    @JoinColumn(name = "receiver_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity receiver;

    /**
     * Сообщение
     */
    @JoinColumn(name = "message_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ChatMessage chatMessage;

    public ChatMessageReceiver() {}

    public ChatMessageReceiver(UserEntity receiver, ChatMessage chatMessage, boolean read) {
        this.receiver = receiver;
        this.chatMessage = chatMessage;
        this.read = read;
    }

    /**
     * Статус прочитано\не прочитано сообщение
     */
    @Column(name = "read", nullable = false)
    private boolean read;

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
