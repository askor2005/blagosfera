package ru.askor.blagosfera.domain.events.community;

import lombok.Getter;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.community.CommunityMember;

@Getter
public class CommunityMemberEvent extends CommunityEvent {

    private CommunityMember member;

    public CommunityMemberEvent(Object source, CommunityEventType type, CommunityMember member) {
        super(source, type, member.getCommunity());
        this.member = member;
    }

}
