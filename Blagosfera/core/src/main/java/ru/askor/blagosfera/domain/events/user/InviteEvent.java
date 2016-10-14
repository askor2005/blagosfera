package ru.askor.blagosfera.domain.events.user;

import lombok.Getter;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.invite.Invitation;
import ru.askor.blagosfera.domain.user.User;

/**
 * Событие, возникающее при работе с приглашениями пользователей в систему
 */
@Getter
public class InviteEvent extends BlagosferaEvent {

    private final InviteEventType type;
    private final User sender;
    private final User receiver;
    private final Invitation invite;
    private final String password;

    public InviteEvent(Object source, InviteEventType type, User sender, User receiver, Invitation invite) {
        super(source);

        this.password = null;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.invite = invite;
    }

    public InviteEvent(Object source, InviteEventType type, User receiver, Invitation invite, String password) {
        super(source);
        this.sender = null;
        this.type = type;
        this.password = password;
        this.receiver = receiver;
        this.invite = invite;
    }

}
