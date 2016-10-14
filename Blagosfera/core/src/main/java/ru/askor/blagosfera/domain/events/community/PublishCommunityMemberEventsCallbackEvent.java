package ru.askor.blagosfera.domain.events.community;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;

import java.util.List;

public class PublishCommunityMemberEventsCallbackEvent extends BlagosferaEvent {

    private List<CommunityMemberEvent> events;
    private PublishCommunityMemberEventsCallback callback;

    public PublishCommunityMemberEventsCallbackEvent(Object source, List<CommunityMemberEvent> events, PublishCommunityMemberEventsCallback callback) {
        super(source);
        this.events = events;
        this.callback = callback;
    }

    public void doCallback() {
        callback.publishCommunityMemberEvents(events);
    }
}
