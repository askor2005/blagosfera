package ru.askor.blagosfera.domain.events.user;

import lombok.Getter;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.user.User;

/**
 * @author dfilinberg
 */
@Getter
public class SharerEvent extends BlagosferaEvent {

    private final SharerEventType type;
    private final User user;

    public SharerEvent(Object source, SharerEventType type, User user) {
        super(source);

        this.type = type;
        this.user = user;
    }
}
