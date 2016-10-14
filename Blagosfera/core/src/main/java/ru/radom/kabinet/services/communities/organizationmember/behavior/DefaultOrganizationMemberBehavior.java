package ru.radom.kabinet.services.communities.organizationmember.behavior;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.community.OrganizationCommunityMember;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.community.OrganizationCommunityMemberEvent;
import ru.askor.blagosfera.domain.events.community.OrganizationCommunityMemberEventType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityException;
import ru.radom.kabinet.services.communities.organizationmember.OrganizationMemberDomainService;
import ru.radom.kabinet.services.communities.organizationmember.dto.ApproveOrganizationCommunityMembersDto;
import ru.radom.kabinet.services.communities.organizationmember.dto.LeaveOrganizationCommunityMembersDto;
import ru.radom.kabinet.services.communities.organizationmember.dto.OrganizationMembersHandleResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Действия над участниками объединения - организациями
 * Created by vgusev on 20.10.2015.
 */
@Service
@Transactional
public class DefaultOrganizationMemberBehavior implements IOrganizationMemberBehavior {

    @Autowired
    private OrganizationMemberDomainService organizationMemberDomainService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    /**
     * Проверить роль доступа к объединению
     * @param community
     * @param userId
     * @param permission
     * @param errorMessage
     * @return
     */
    public void checkPermission(Community community, Long userId, String permission, String errorMessage) {
        if (!communitiesService.hasPermission(community.getId(), userId, permission)) {
            throw new CommunityException(errorMessage);
        }
    }

    // Простой запрос на вступление
    @Override
    public OrganizationMembersHandleResult requestToJoinInCommunity(Community community, Community candidateToMember) {
        OrganizationCommunityMember organizationCommunityMember = new OrganizationCommunityMember();
        organizationCommunityMember.setCommunity(community);
        organizationCommunityMember.setOrganization(candidateToMember);
        organizationCommunityMember.setStatus(CommunityMemberStatus.REQUEST);
        organizationCommunityMember = organizationMemberDomainService.save(organizationCommunityMember);

        // Событие запроса на вступление
        blagosferaEventPublisher.publishEvent(new OrganizationCommunityMemberEvent(this, OrganizationCommunityMemberEventType.REQUEST, candidateToMember, community, organizationCommunityMember));
        return new OrganizationMembersHandleResult();
    }

    // Отколнение запроса на вступление
    @Override
    public OrganizationMembersHandleResult rejectRequests(Community community, List<OrganizationCommunityMember> organizationCommunityMembers, User currentUser) {
        checkPermission(community, currentUser.getId(), "REQUESTS", "У Вас нет прав на управление этим запросом");
        // Удаляем запросы
        for (OrganizationCommunityMember organizationCommunityMember : organizationCommunityMembers){
            organizationMemberDomainService.delete(organizationCommunityMember.getId());
            // Событие отколнения запроса на вступление
            blagosferaEventPublisher.publishEvent(new OrganizationCommunityMemberEvent(this, OrganizationCommunityMemberEventType.REJECT_REQUEST, organizationCommunityMember.getOrganization(), community));
        }
        return new OrganizationMembersHandleResult();
    }

    @Override
    public OrganizationMembersHandleResult cancelRequest(OrganizationCommunityMember organizationCommunityMember) {
        // Удаляем запрос
        organizationMemberDomainService.delete(organizationCommunityMember.getId());
        return new OrganizationMembersHandleResult();
    }


    
    @Override
    public OrganizationMembersHandleResult acceptToJoinInCommunity(Community community, List<OrganizationCommunityMember> organizationCommunityMembers, User currentUser) {
        checkPermission(community, currentUser.getId(), "REQUESTS", "У Вас нет прав на управление этим запросом");
        for (OrganizationCommunityMember organizationCommunityMember : organizationCommunityMembers) {
            organizationCommunityMember.setStatus(CommunityMemberStatus.MEMBER);
            organizationMemberDomainService.save(organizationCommunityMember);

            // Событие принятия в объединение
            blagosferaEventPublisher.publishEvent(new OrganizationCommunityMemberEvent(this, OrganizationCommunityMemberEventType.ACCEPT_TO_JOIN, organizationCommunityMember.getOrganization(), community));
        }
        return new OrganizationMembersHandleResult();
    }


    @Override
    public OrganizationMembersHandleResult requestFromOrganizationToExcludeFromCommunity(OrganizationCommunityMember organizationCommunityMember) {
        // Просто удаляем участника
        organizationMemberDomainService.delete(organizationCommunityMember.getId());
        return new OrganizationMembersHandleResult();
    }

    @Override
    public OrganizationMembersHandleResult requestFromCommunityOwnerToExcludeFromCommunity(OrganizationCommunityMember organizationCommunityMember, User currentUser) {
        checkPermission(organizationCommunityMember.getCommunity(), currentUser.getId(), "EXCLUDE", "У Вас нет прав на исключение участников из этого объединения");
        // Просто удаляем участника
        organizationMemberDomainService.delete(organizationCommunityMember.getId());

        // Событие исключения из объединения
        blagosferaEventPublisher.publishEvent(new OrganizationCommunityMemberEvent(this, OrganizationCommunityMemberEventType.EXCLUDE, organizationCommunityMember.getOrganization(), organizationCommunityMember.getCommunity()));

        return new OrganizationMembersHandleResult();
    }

    @Override
    public OrganizationMembersHandleResult acceptExcludeFromCommunity(Community community, List<OrganizationCommunityMember> organizationCommunityMembers, User currentUser) {
        throw new RuntimeException("Стандартный выход из объединения не поддержиывает подтверждения на выход");
    }

    @Override
    public OrganizationMembersHandleResult cancelExcludeRequest(OrganizationCommunityMember organizationCommunityMember) {
        throw new RuntimeException("В обычных объединениях не нужно делать запрос на выход");
    }

    @Override
    public LeaveOrganizationCommunityMembersDto getLeaveCommunityMembers(Community community, Long userId) {
        throw new RuntimeException("Стандартный выход из объединения не поддерживает подтверждения");
    }

    @Override
    public ApproveOrganizationCommunityMembersDto getApproveCommunityMembers(Community community, Long userId) {
        throw new RuntimeException("Стандартный вход в объединения не поддерживает подтверждения");
    }
}
