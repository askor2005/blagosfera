package ru.askor.blagosfera.domain.events.community;

import java.util.List;

public interface PublishCommunityMemberEventsCallback {

    void publishCommunityMemberEvents(List<CommunityMemberEvent> events);
}
