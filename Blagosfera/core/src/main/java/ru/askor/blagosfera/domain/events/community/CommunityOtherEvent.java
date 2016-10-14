package ru.askor.blagosfera.domain.events.community;

import lombok.Getter;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.UserEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Некое событие связанное с объединением
 * Created by vgusev on 01.09.2015.
 */
@Getter
public class CommunityOtherEvent extends CommunityEvent {

    // Получатели оповещения
    private List<User> receivers;

    // Параметры оповещения
    private Map<String, String> parameters = new HashMap<>();

    public CommunityOtherEvent(Object source, CommunityEventType type, Community community, List<User> receivers) {
        super(source, type, community);
        this.receivers = receivers;
    }

    public CommunityOtherEvent(Object source, CommunityEventType type, Community community, List<User> receivers, Map<String, String> parameters) {
        super(source, type, community);
        this.receivers = receivers;
        this.parameters = parameters;
    }
}
