package ru.askor.blagosfera.domain.events.chat;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.radom.kabinet.model.chat.ChatMessage;

/**
 * @author dfilinberg
 */
public class ChatEvent extends BlagosferaEvent {

    private final ChatEventType type;
    private final ChatMessage message;

    public ChatEvent(Object source, ChatEventType type, ChatMessage message) {
        super(source);

        this.type = type;
        this.message = message;
    }

    public ChatEventType getType() {
        return type;
    }

    public ChatMessage getMessage() {
        return message;
    }

}
