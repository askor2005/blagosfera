package ru.askor.blagosfera.domain.events.community;

import lombok.Getter;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.user.User;

/**
 * Событие выхода из ПО
 * Created by vgusev on 04.09.2015.
 */
@Getter
public class CommunityCooperativeLeaveEvent extends CommunityEvent {

    // Накопления на книжке пайщика
    private Double bookAccountAmount;

    // Получатель события
    private User receiver;

    public CommunityCooperativeLeaveEvent(Object source, CommunityEventType type, Community community, Double bookAccountAmount, User receiver) {
        super(source, type, community);
        this.bookAccountAmount = bookAccountAmount;
        this.receiver = receiver;
    }

}
