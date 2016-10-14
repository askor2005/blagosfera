package ru.askor.blagosfera.domain.events.community;

import lombok.Getter;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityPost;
import ru.askor.blagosfera.domain.user.User;

/**
 * Событие - необходимо назначить участника объединения на пост
 * Created by vgusev on 03.09.2015.
 */
@Getter
public class CommunityMemberAppointRequestEvent extends CommunityMemberAppointEvent {

    // Инструкция по назначению на пост
    private String appointInstruction;

    public CommunityMemberAppointRequestEvent(
            Object source, CommunityEventType type, CommunityMember member, User appointer,
            CommunityPost post, String appointInstruction) {
        super(source, type, member, appointer, post);

        this.appointInstruction = appointInstruction;
    }
}
