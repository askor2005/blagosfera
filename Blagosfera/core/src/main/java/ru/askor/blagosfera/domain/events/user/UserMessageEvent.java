package ru.askor.blagosfera.domain.events.user;

import lombok.Getter;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.user.User;

import java.util.Map;

/**
 *
 * Created by vgusev on 29.07.2016.
 */
@Getter
public class UserMessageEvent extends BlagosferaEvent {

    private Map<String, Object> parameters;

    private String content;

    private User user;

    public UserMessageEvent(Object source, Map<String, Object> parameters, String content, User user) {
        super(source);

        this.parameters = parameters;
        this.content = content;
        this.user = user;
    }
}
