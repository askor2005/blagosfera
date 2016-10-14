package ru.radom.kabinet.services.event;

import ru.askor.blagosfera.domain.events.chat.ChatEvent;
import ru.askor.blagosfera.domain.events.chat.DialogEvent;
import ru.askor.blagosfera.domain.events.community.CommunityMemberEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEvent;
import ru.askor.blagosfera.domain.events.news.NewsEvent;
import ru.askor.blagosfera.domain.events.user.*;

/**
 * Created by mnikitin on 12.05.2016.
 */
public interface ApplicationEventListener {

    void onCommunityMemberEvent(CommunityMemberEvent event);

    void onNewsEvent(NewsEvent event);

    void onChatEvent(ChatEvent event);

    void onDialogEvent(DialogEvent event);

    void onContactEvent(ContactEvent event);

    void onSharerEvent(SharerEvent event);

    void onRatingEvent(RatingEvent event);

    void onRegistrationEvent(RegistrationEvent event);

    void onDocumentEvent(FlowOfDocumentStateEvent event);

    void onUserMessageEvent(UserMessageEvent userMessageEvent);
}
