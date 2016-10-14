package ru.radom.kabinet.services.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.askor.blagosfera.domain.events.chat.ChatEvent;
import ru.askor.blagosfera.domain.events.chat.DialogEvent;
import ru.askor.blagosfera.domain.events.chat.DialogEventType;
import ru.askor.blagosfera.domain.events.community.CommunityMemberEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEvent;
import ru.askor.blagosfera.domain.events.document.FlowOfDocumentStateEventType;
import ru.askor.blagosfera.domain.events.news.NewsEvent;
import ru.askor.blagosfera.domain.events.user.*;
import ru.askor.blagosfera.domain.news.NewsItem;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.news.NewsDao;
import ru.radom.kabinet.dao.news.NewsSubscribeDao;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.dto.CommunityDto;
import ru.radom.kabinet.dto.StringObjectHashMap;
import ru.radom.kabinet.dto.news.NewsDto;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.ChatMessage;
import ru.radom.kabinet.model.chat.ChatMessageReceiver;
import ru.radom.kabinet.model.news.News;
import ru.radom.kabinet.model.news.NewsSubscribe;
import ru.radom.kabinet.services.StompService;
import ru.radom.kabinet.services.news.NewsFilterService;
import ru.radom.kabinet.services.news.NewsLayersService;
import ru.radom.kabinet.services.rating.RatingService;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.web.flowofdocuments.dto.DocumentDataDto;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Слушатель событий приложения
 */
@Service("applicationEventListener")
public class ApplicationEventListenerImpl implements ApplicationEventListener {

    // TODO Переделать

    @Autowired
    private SerializationManager serializationManager;

    @Autowired
    private StompService stompService;

    @Autowired
    private NewsDao newsDao;

    @Autowired
    private NewsFilterService newsFilterService;

    @Autowired
    private NewsLayersService newsLayersService;

    @Autowired
    private NewsSubscribeDao newsSubscribeDao;

    @Autowired
    private DocumentService documentService;

    @TransactionalEventListener
    @Override
    public void onCommunityMemberEvent(CommunityMemberEvent event) {
        CommunityMember member = event.getMember();
        Community community = event.getMember().getCommunity();
        //String type = "community_" + event.getType().name().toLowerCase();
        String type = "community_member_event";
        CommunityDto payload = new CommunityDto(
                community,
                member,
                member.getUser(),
                event.getType().name().toLowerCase()
        );

        stompService.send(community.getCreator().getEmail(), type, payload);
        stompService.send("/topic/community_" + community.getId() + "_" + type, payload);
        stompService.send(member.getUser().getEmail(), type, payload);

        /*switch (event.getType()) {
            case INVITE:
            case CANCEL_INVITE:
            case EXCLUDE:
            case ACCEPT_REQUEST:
            case REJECT_REQUEST:
            case CANCEL_REQUEST_LEAVE:
            case REQUEST_TO_LEAVE:
                stompService.send(member.getUser().getEmail(), type, payload);
                stompService.send("/topic/community_" + community.getId() + "_" + type, payload);
                break;
            case REQUEST:
            case CANCEL_REQUEST:
            case ACCEPT_INVITE:
            case REJECT_INVITE:
            case JOIN:
            case LEAVE:
                stompService.send(community.getCreator().getEmail(), type, payload);
                stompService.send("/topic/community_" + community.getId() + "_" + type, payload);
                stompService.send(member.getUser().getEmail(), type, payload);
                break;
            default:
                break;
        }*/
    }

    @Async
    @Transactional
    @TransactionalEventListener
    @Override
    public void onNewsEvent(NewsEvent event) {
        List<NewsSubscribe> subscribes = newsSubscribeDao.getList(event.getNews().getScope());
        News news;

        switch (event.getType()) {
            case CREATE:
                //Создание новости
                news =  newsDao.getById(event.getNews().getId());

                for (NewsSubscribe subscribe : subscribes) {
                    UserEntity userEntity = subscribe.getUser();

                    if (newsFilterService.isNewsSuitableForSharersFilter(news, userEntity)) {
                        NewsItem payloadDomain = newsLayersService.makeDomainForSharer(news, subscribe.getUser().getId());
                        // TODO Переделать
                        //stompService.send(sharer, "news_create", payloadDomain.toDto());
                    }
                }
                break;
            case EDIT:
                //Редактирование новости
                news =  newsDao.getById(event.getNews().getId());

                for (NewsSubscribe subscribe : subscribes) {
                    UserEntity userEntity = subscribe.getUser();

                    if (newsFilterService.isNewsSuitableForSharersFilter(news, userEntity)) {
                        NewsItem payloadDomain = newsLayersService.makeDomainForSharer(news, subscribe.getUser().getId());
                        // TODO Переделать
                        //stompService.send(sharer, "news_edit", payloadDomain.toDto());
                    }
                }
                break;
            case DELETE:
                //Удаление новости
                news = event.getNews();
                NewsDto deletedNewsDto = new NewsDto();
                deletedNewsDto.id = news.getId();

                for (NewsSubscribe subscribe : subscribes) {
                    UserEntity userEntity = subscribe.getUser();
                    // TODO Переделать
                    //stompService.send(sharer, "news_delete", deletedNewsDto);
                }
                break;
            default:
                break;
        }
    }

    @TransactionalEventListener
    @Override
    public void onChatEvent(ChatEvent event) {
        ChatMessage message = event.getMessage();
        Set<User> receivers = new HashSet<>();

        /*for (Long receiverId : message.getReceivers()) {
            //if (receiverId.equals(message.getSender().getId())) continue;
            receivers.add(sharerDao.getById(receiverId));
        }*/

        Set<ChatMessageReceiver> chatMessageReceivers = message.getChatMessageReceivers();
        receivers.addAll(chatMessageReceivers.stream().map(chatMessageReceiver -> chatMessageReceiver.getReceiver().toDomain()).collect(Collectors.toList()));
        receivers.add(message.getSender().toDomain());

        switch (event.getType()) {
            case ADD:
                stompService.send(receivers, "new_chat_message", serializationManager.serialize(message).toString());
                break;
            case EDIT:
                stompService.send(receivers, "edit_chat_message", serializationManager.serialize(message).toString());
                break;
            case DELETE:
                stompService.send(receivers, "delete_chat_message", serializationManager.serialize(message).toString());
                break;
            default:
                break;
        }
    }

    @TransactionalEventListener
    @Override
    public void onDialogEvent(DialogEvent event) {
        Set<User> receivers = new HashSet<>();

        Set<UserEntity> userEntities = event.getDialog().getUsers();
        for (UserEntity receiver : userEntities) {
            if (receiver.getId().equals(event.getSenderId())) continue;
            receivers.add(receiver.toDomain());
        }

        if (event.getEventType() == DialogEventType.SHARER_DELETED) receivers.add(event.getSharer().toDomain());
        stompService.send(receivers, "chat_state_changed", "\"refresh the page\"");
    }

    @TransactionalEventListener
    @Override
    public void onContactEvent(ContactEvent event) {
        Contact contact = event.getContact();
        User otherUser = contact.getOther();
        switch (event.getType()) {
            case ADD:
                stompService.send(otherUser.getEmail(), "contact_add", serializationManager.serialize(contact.getUser()).toString());
                break;
            case ACCEPTED:
                stompService.send(otherUser.getEmail(), "contact_accepted", serializationManager.serialize(contact.getUser()).toString());
                break;
            case DELETE:
                stompService.send(otherUser.getEmail(), "contact_delete", serializationManager.serialize(contact.getUser()).toString());
                break;
            default:
                break;
        }
    }

    @TransactionalEventListener
    @Override
    public void onSharerEvent(SharerEvent event) {
        User user = event.getUser();
        switch (event.getType()) {
            case ARCHIVED:
                stompService.send(user.getEmail(), "profile_archived", "{}");
                break;
            case DELETED:
                stompService.send(user.getEmail(), "profile_deleted", "{}");
                break;
            default:
                break;
        }
    }

    @TransactionalEventListener
    @Override
    public void onRatingEvent(RatingEvent event) {
        final Map<String, Object> responseData = new StringObjectHashMap();
        responseData.put(RatingService.RATING_KEY, event.getRating());
        responseData.put(RatingService.COUNT_KEY, StringUtils.nvlNumeric(event.getCount()));
        stompService.send("/topic/rating_update", serializationManager.serialize(responseData).toString());
    }

    @TransactionalEventListener
    @Override
    public void onRegistrationEvent(RegistrationEvent event) {
        switch (event.getType()) {
            case VERIFICATION_REQUEST:
            case DELETE_REQUEST:
            case CANCEL_REQUEST:
                final Map<String, Object> responseData = new StringObjectHashMap();
                responseData.put("registratorId", event.getRegistrator().getId());
                stompService.send("/topic/registration_request_update", serializationManager.serialize(responseData).toString());
                break;
        }
    }

    @TransactionalEventListener
    @Override
    public void onDocumentEvent(FlowOfDocumentStateEvent event) {
        if (event.getStateEventType() == FlowOfDocumentStateEventType.DOCUMENT_SIGNED) {
            stompService.send("/topic/document_signed_" + event.getDocument().getId(), DocumentDataDto.toDto(event.getDocument(), documentService.isSignedDocument(event.getDocument()) ));
        }
    }

    @TransactionalEventListener
    @Override
    public void onUserMessageEvent(UserMessageEvent userMessageEvent) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("parameters", userMessageEvent.getParameters());
        payload.put("content", userMessageEvent.getContent());
        stompService.send(userMessageEvent.getUser().getEmail(), "show_user_message", payload);
    }
}
