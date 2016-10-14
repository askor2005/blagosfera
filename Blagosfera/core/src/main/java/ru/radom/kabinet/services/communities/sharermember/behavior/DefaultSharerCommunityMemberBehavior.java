package ru.radom.kabinet.services.communities.sharermember.behavior;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityAccessType;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.events.community.CommunityMemberEvent;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.CommunityException;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.SharerCommunityMemberService;
import ru.radom.kabinet.services.communities.sharermember.dto.ApproveCommunityMembersDto;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;
import ru.radom.kabinet.services.communities.sharermember.dto.LeaveCommunityMembersDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vgusev on 28.10.2015.
 */
@Transactional
@Service
public class DefaultSharerCommunityMemberBehavior implements ISharerCommunityMemberBehavior, ApplicationContextAware {

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private CommunityDataService communityDataService;

    private ApplicationContext applicationContext;

    /**
     * Проверить роль доступа к объединению
     *
     * @param community
     * @param user
     * @param permission
     * @param errorMessage
     * @return
     */
    public void checkPermission(Community community, User user, String permission, String errorMessage) {
        if (!communitiesService.hasPermission(community.getId(), user.getId(), permission)) {
            throw new CommunityException(errorMessage);
        }
    }

    
    @Override
    public CommunityMemberResponseDto acceptInvite(CommunityMember member, boolean notifySignEvent) {
        member.setStatus(CommunityMemberStatus.MEMBER);
        communityMemberDomainService.save(member);
        return new CommunityMemberResponseDto(member, new CommunityMemberEvent(this, CommunityEventType.ACCEPT_INVITE, member));
    }

    
    @Override
    public CommunityMemberResponseDto request(Community community, User requester, boolean notifySignEvent) {
        CommunityMember member = new CommunityMember();
        member.setCommunity(community);
        member.setRequestDate(new Date());
        member.setUser(requester);

        CommunityMemberStatus status;
        CommunityMemberEvent event;
        CommunityEventType eventType;

        if (CommunityAccessType.OPEN == community.getAccessType()) {
            status = CommunityMemberStatus.MEMBER;
            eventType = CommunityEventType.JOIN;
        } else {
            status = CommunityMemberStatus.REQUEST;
            eventType = CommunityEventType.REQUEST;
        }

        member.setStatus(status);
        member = communityMemberDomainService.save(member);
        event = new CommunityMemberEvent(this, eventType, member);

        return new CommunityMemberResponseDto(member, event);
    }

    
    @Override
    public CommunityMemberResponseDto acceptRequests(List<CommunityMember> members, User accepter, boolean notifySignEvent) {
        CommunityMemberResponseDto result;
        Community community = members.get(0).getCommunity();
        checkPermission(community, accepter, SharerCommunityMemberService.REQUESTS_PERMISSION, "У Вас нет прав на управление этим запросом");
        List<CommunityMemberEvent> events = new ArrayList<>();
        for (CommunityMember member : members) {
            SharerCommunityMemberService.check(!CommunityMemberStatus.REQUEST.equals(member.getStatus()), "Неверный статус запроса");
            member.setStatus(CommunityMemberStatus.MEMBER);
            communityMemberDomainService.save(member);
            events.add(new CommunityMemberEvent(this, CommunityEventType.ACCEPT_REQUEST, member));
        }
        if (members.size() > 1) {
            result = new CommunityMemberResponseDto(events);
        } else {
            result = new CommunityMemberResponseDto(members.get(0), events.get(0));
        }
        return result;
    }

    
    @Override
    public CommunityMemberResponseDto rejectRequestsFromCommunityOwner(List<CommunityMember> members, User rejecter) {
        CommunityMemberResponseDto result;
        List<CommunityMemberEvent> events = new ArrayList<>();
        for (CommunityMember member : members) {
            checkPermission(member.getCommunity(), rejecter, SharerCommunityMemberService.REQUESTS_PERMISSION, "У Вас нет прав на управление этим запросом");
            SharerCommunityMemberService.check(!CommunityMemberStatus.REQUEST.equals(member.getStatus()), "Неверный статус запроса");
            communityMemberDomainService.delete(member.getId());
            member.setStatus(null);
            events.add(new CommunityMemberEvent(this, CommunityEventType.REJECT_REQUEST, member));
        }
        if (members.size() > 1) {
            result = new CommunityMemberResponseDto(events);
        } else {
            result = new CommunityMemberResponseDto(members.get(0), events.get(0));
        }
        return result;
    }

    
    @Override
    public CommunityMemberResponseDto cancelRequestFromMember(CommunityMember member, User memberUser) {
        //SharerCommunityMemberService.check(!CommunityMemberStatus.REQUEST.equals(member.getStatus()), "Неверный статус запроса");
        communityMemberDomainService.delete(member.getId());
        member.setStatus(null);
        return new CommunityMemberResponseDto(member, new CommunityMemberEvent(this, CommunityEventType.CANCEL_REQUEST, member));
    }

    
    @Override
    public CommunityMemberResponseDto requestToExcludeFromCommunityOwner(CommunityMember member, User excluder) {
        checkPermission(member.getCommunity(), excluder, SharerCommunityMemberService.EXCLUDE_PERMISSION, "У Вас нет прав на исключение участников из этого объединения");
        SharerCommunityMemberService.check(!CommunityMemberStatus.MEMBER.equals(member.getStatus()), "Неверный статус участника");

        for (Community childCommunity : member.getCommunity().getChildren()) {
            CommunityMember childMember = communityMemberDomainService.getByCommunityIdAndUserId(childCommunity.getId(), member.getUser().getId());

            if (childMember != null) {
                requestToExcludeFromCommunityOwner(childMember, excluder);
            }
        }

        communityMemberDomainService.delete(member.getId());
        member.setStatus(null);
        return new CommunityMemberResponseDto(member, new CommunityMemberEvent(this, CommunityEventType.EXCLUDE, member));
    }

    
    @Override
    public CommunityMemberResponseDto requestToExcludeFromMember(CommunityMember member, User leaver) {
        List<Community> child = communityDataService.getByParentId(member.getCommunity().getId());
        if (child != null) {
            for (Community childCommunity : child) {
                CommunityMember childMember = communityMemberDomainService.getByCommunityIdAndUserId(childCommunity.getId(), leaver.getId());
                if (childMember != null) {
                    requestToExcludeFromMember(childMember, leaver);
                }
            }
        }
        communityMemberDomainService.delete(member.getId());
        member.setStatus(null);

        return new CommunityMemberResponseDto(member, new CommunityMemberEvent(this, CommunityEventType.LEAVE, member));
    }

    @Override
    public void cancelRequestToLeaveFromMember(CommunityMember member) {
        throw new RuntimeException("Обычное объединение не поддерживает выход участников через запрос");
    }

    @Override
    public CommunityMemberResponseDto acceptRequestsToExcludeFromCommunity(List<CommunityMember> members, User excluder, boolean notifySignEvent) {
        throw new RuntimeException("Обычное объединение не поддерживает выход участников через запрос");
    }

    @Override
    public LeaveCommunityMembersDto getLeaveCommunityMembers(Community community, User excluder) {
        throw new RuntimeException("Обычное объединение не поддерживает выход участников через запрос");
    }

    @Override
    public ApproveCommunityMembersDto getApproveCommunityMembers(Community community, User approver) {
        throw new RuntimeException("Обычное объединение не поддерживает приняте участников через запрос с условием");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
