package ru.askor.blagosfera.domain.events.community;

import lombok.Getter;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;

@Getter
public class CommunityEvent extends BlagosferaEvent {

    private CommunityEventType type;
    private Community community;

    public CommunityEvent(Object source, CommunityEventType type, Community community) {
        super(source);
        this.type = type;
        this.community = community;
    }
}
