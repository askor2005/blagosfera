package ru.askor.blagosfera.domain.events.community;

import lombok.Getter;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityPost;
import ru.askor.blagosfera.domain.community.CommunityPostRequest;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.BatchVoting;

/**
 * События назначения на должность
 * Created by rkorablin on 12.05.2015.
 */
@Getter
public class CommunityMemberAppointEvent extends CommunityMemberEvent {

    private User appointer;
    private CommunityPost post;
    private CommunityPostRequest postRequest;
    private BatchVoting batchVoting;

    public CommunityMemberAppointEvent(Object source, CommunityEventType type, CommunityMember member, User appointer, CommunityPost post) {
        super(source, type, member);
        this.appointer = appointer;
        this.post = post;
    }

    public CommunityMemberAppointEvent(Object source, CommunityEventType type, CommunityMember member, User appointer, CommunityPost post, CommunityPostRequest postRequest) {
        super(source, type, member);
        this.appointer = appointer;
        this.post = post;
        this.postRequest = postRequest;
    }

    public CommunityMemberAppointEvent(Object source, CommunityEventType type, CommunityMember member, BatchVoting batchVoting, CommunityPost post) {
        super(source, type, member);
        this.batchVoting = batchVoting;
        this.post = post;
    }
}
