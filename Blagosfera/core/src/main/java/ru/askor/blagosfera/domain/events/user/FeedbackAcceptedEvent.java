package ru.askor.blagosfera.domain.events.user;

import lombok.Getter;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.invite.Invitation;
import ru.askor.blagosfera.domain.support.SupportRequest;
import ru.askor.blagosfera.domain.user.User;

import java.util.List;

/**
 * Created by vtarasenko on 18.05.2016.
 */
@Getter
public class FeedbackAcceptedEvent extends BlagosferaEvent {
    private final User receiver;
    private SupportRequest supportRequest;
    private List<User> adminReceivers;
    public  FeedbackAcceptedEvent(Object source, User receiver,List<User> adminReceivers,SupportRequest supportRequest) {
        super(source);
        this.adminReceivers = adminReceivers;
        this.receiver = receiver;
        this.supportRequest = supportRequest;
    }

}
