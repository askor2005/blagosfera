package ru.radom.kabinet.services.communities.sharermember.behavior;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.services.communities.sharermember.SharerCommunityMemberService;
import ru.radom.kabinet.services.communities.sharermember.dto.ApproveCommunityMembersDto;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;
import ru.radom.kabinet.services.communities.sharermember.dto.LeaveCommunityMembersDto;

import java.util.List;

/**
 *
 * Created by vgusev on 27.07.2016.
 */
@Service("publishEventsCommunityMemberBehavior")
@Transactional
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PublishEventsCommunityMemberBehavior implements ISharerCommunityMemberBehavior {

    private ISharerCommunityMemberBehavior delegate;

    @Autowired
    private SharerCommunityMemberService sharerCommunityMemberService;

    public PublishEventsCommunityMemberBehavior(ISharerCommunityMemberBehavior delegate) {
        this.delegate = delegate;
    }

    @Override
    public CommunityMemberResponseDto acceptInvite(CommunityMember member, boolean notifySignEvent) {
        CommunityMemberResponseDto result = delegate.acceptInvite(member, notifySignEvent);
        sharerCommunityMemberService.publishCommunityMemberEventsAfterTransactionCommit(result.getEvents());
        return result;
    }

    @Override
    public CommunityMemberResponseDto request(Community community, User requester, boolean notifySignEvent) {
        CommunityMemberResponseDto result = delegate.request(community, requester, notifySignEvent);
        sharerCommunityMemberService.publishCommunityMemberEventsAfterTransactionCommit(result.getEvents());
        return result;
    }

    @Override
    public CommunityMemberResponseDto acceptRequests(List<CommunityMember> members, User accepter, boolean notifySignEvent) {
        CommunityMemberResponseDto result = delegate.acceptRequests(members, accepter, notifySignEvent);
        sharerCommunityMemberService.publishCommunityMemberEventsAfterTransactionCommit(result.getEvents());
        return result;
    }

    @Override
    public CommunityMemberResponseDto rejectRequestsFromCommunityOwner(List<CommunityMember> members, User rejecter) {
        CommunityMemberResponseDto result = delegate.rejectRequestsFromCommunityOwner(members, rejecter);
        sharerCommunityMemberService.publishCommunityMemberEventsAfterTransactionCommit(result.getEvents());
        return result;
    }

    @Override
    public CommunityMemberResponseDto cancelRequestFromMember(CommunityMember member, User memberUser) {
        CommunityMemberResponseDto result = delegate.cancelRequestFromMember(member, memberUser);
        sharerCommunityMemberService.publishCommunityMemberEventsAfterTransactionCommit(result.getEvents());
        return result;
    }

    @Override
    public CommunityMemberResponseDto requestToExcludeFromCommunityOwner(CommunityMember member, User excluder) {
        CommunityMemberResponseDto result = delegate.requestToExcludeFromCommunityOwner(member, excluder);
        sharerCommunityMemberService.publishCommunityMemberEventsAfterTransactionCommit(result.getEvents());
        return result;
    }

    @Override
    public CommunityMemberResponseDto requestToExcludeFromMember(CommunityMember member, User leaver) {
        CommunityMemberResponseDto result = delegate.requestToExcludeFromMember(member, leaver);
        sharerCommunityMemberService.publishCommunityMemberEventsAfterTransactionCommit(result.getEvents());
        return result;
    }

    @Override
    public void cancelRequestToLeaveFromMember(CommunityMember member) {
        delegate.cancelRequestToLeaveFromMember(member);
    }

    @Override
    public CommunityMemberResponseDto acceptRequestsToExcludeFromCommunity(List<CommunityMember> members, User excluder, boolean notifySignEvent) {
        CommunityMemberResponseDto result = delegate.acceptRequestsToExcludeFromCommunity(members, excluder, notifySignEvent);
        sharerCommunityMemberService.publishCommunityMemberEventsAfterTransactionCommit(result.getEvents());
        return result;
    }

    @Override
    public LeaveCommunityMembersDto getLeaveCommunityMembers(Community community, User excluder) {
        LeaveCommunityMembersDto result = delegate.getLeaveCommunityMembers(community, excluder);
        return result;
    }

    @Override
    public ApproveCommunityMembersDto getApproveCommunityMembers(Community community, User approver) {
        ApproveCommunityMembersDto result = delegate.getApproveCommunityMembers(community, approver);
        return result;
    }
}
