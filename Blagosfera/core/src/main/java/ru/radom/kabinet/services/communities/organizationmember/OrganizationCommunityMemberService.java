package ru.radom.kabinet.services.communities.organizationmember;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.community.OrganizationCommunityMember;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.organizationmember.behavior.IOrganizationMemberBehavior;
import ru.radom.kabinet.services.communities.organizationmember.behavior.OrganizationMemberBehaviorResolver;
import ru.radom.kabinet.services.communities.organizationmember.dto.ApproveOrganizationCommunityMembersDto;
import ru.radom.kabinet.services.communities.organizationmember.dto.LeaveOrganizationCommunityMembersDto;
import ru.radom.kabinet.services.communities.organizationmember.dto.OrganizationMembersHandleResult;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.util.*;

/**
 *
 * Created by vgusev on 19.10.2015.
 */
@Service
@Transactional
public class OrganizationCommunityMemberService {

    // TODO Надо добавить методы
    // 1 Отказа запроса на вступление (от имени главы объединения или с правами)
    // 2 Отмены запроса на вступление (от имени главы организации или с правами)

    @Autowired
    private OrganizationMemberDomainService organizationMemberDomainService;

    @Autowired
    private OrganizationMemberBehaviorResolver organizationMemberBehaviorResolver;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private UserDataService userDataService;

    private void check(boolean flag, String message) {
        if (!flag) {
            throw new RuntimeException(message);
        }
    }

    private Map<String, Object> getStandardResponse(OrganizationCommunityMember organizationCommunityMember) {
        Map<String, Object> reponse = new HashMap<>();
        reponse.put("community", organizationCommunityMember.getCommunity());
        reponse.put("member", organizationCommunityMember);
        return reponse;
    }

    /**
     * Поиск участников объединения
     * @param communityId
     * @param status
     * @param communityName
     * @param page
     * @param perPage
     * @return
     */
    public List<OrganizationCommunityMember> find(Long communityId, CommunityMemberStatus status, String communityName, int page, int perPage) {
        return organizationMemberDomainService.find(communityId, status, communityName, page, perPage);
    }

    public OrganizationMembersHandleResult requestToJoinInCommunity(Community candidateCommunity, Community community, User currentUser) {
        User director = communitiesService.getCommunityDirector(candidateCommunity);
        check(director != null, "Не установлен директор в организации");
        check(director.getId().equals(currentUser.getId()), "У Вас нет прав на данное объединение");
        // Проверяем, что организация не является участником
        OrganizationCommunityMember findMember = organizationMemberDomainService.getByCommunityIdAndOrganizationId(community.getId(), candidateCommunity.getId());
        check(findMember == null, "Организация уже состоит в объединении либо уже отправлен запрос на вступление в объединение");
        IOrganizationMemberBehavior organizationMemberBehavior = organizationMemberBehaviorResolver.getOrganizationMemberBehavior(community);
        return organizationMemberBehavior.requestToJoinInCommunity(community, candidateCommunity);
    }

    /**
     * Отмена запроса на вступление в объединение
     * @param memberId
     * @return
     */
    public OrganizationMembersHandleResult rejectRequest(Long memberId, User currentUser) {
        OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getById(memberId);
        check((organizationCommunityMember != null &&
                        (organizationCommunityMember.getStatus() == CommunityMemberStatus.REQUEST ||
                         organizationCommunityMember.getStatus() == CommunityMemberStatus.CONDITION_DONE_REQUEST)),
                "Не найден запрос на вступление в объединение");

        Community community = organizationCommunityMember.getCommunity();
        IOrganizationMemberBehavior organizationMemberBehavior = organizationMemberBehaviorResolver.getOrganizationMemberBehavior(community);

        /*Map<String, Object> response = getStandardResponse(organizationCommunityMember);
        response.putAll(organizationMemberBehavior.rejectRequests(community, Collections.singletonList(organizationCommunityMember), currentUser));*/

        // Права проверяются внутри
        return organizationMemberBehavior.rejectRequests(community, Collections.singletonList(organizationCommunityMember), currentUser);
    }

    // Отказ о вступлении в объединение несколких организаций
    public OrganizationMembersHandleResult rejectRequests(List<Long> memberIds, User currentUser){
        List<OrganizationCommunityMember> organizationCommunityMembers = new ArrayList<>();
        Community community = null;
        for (Long memberId : memberIds) {
            OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getById(memberId);
            if (community == null) {
                community = organizationCommunityMember.getCommunity();
            }
            check((organizationCommunityMember != null &&
                            (organizationCommunityMember.getStatus() == CommunityMemberStatus.REQUEST ||
                             organizationCommunityMember.getStatus() == CommunityMemberStatus.CONDITION_DONE_REQUEST)),
                    "Не найден запрос на вступление в объединение");
            check(organizationCommunityMember.getCommunity().getId().equals(community.getId()), "Запрос не принадлежит объединению");
            organizationCommunityMembers.add(organizationCommunityMember);
        }
        IOrganizationMemberBehavior organizationMemberBehavior = organizationMemberBehaviorResolver.getOrganizationMemberBehavior(community);
        // Права проверяются внутри
        return organizationMemberBehavior.rejectRequests(community, organizationCommunityMembers, currentUser);
    }

    // Отмена вступления в объединение от имени руководителя организации
    public OrganizationMembersHandleResult cancelRequest(Long memberId, User currentUser) {
        OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getById(memberId);
        check(organizationCommunityMember != null, "Не найден запрос на встуление в объединение");
        User director = communitiesService.getCommunityDirector(organizationCommunityMember.getOrganization());
        check(director != null, "Не установлен директор в организации");
        check(director.getId().equals(currentUser.getId()), "Вы не являетесь директором организации");
        check(organizationCommunityMember.getStatus() == CommunityMemberStatus.REQUEST ||
              organizationCommunityMember.getStatus() == CommunityMemberStatus.CONDITION_DONE_REQUEST ||
              organizationCommunityMember.getStatus() == CommunityMemberStatus.CONDITION_NOT_DONE_REQUEST,
                "Не правильный статус запроса на встуление в объединение");
        IOrganizationMemberBehavior organizationMemberBehavior = organizationMemberBehaviorResolver.getOrganizationMemberBehavior(organizationCommunityMember.getCommunity());
        /*Map<String, Object> response = getStandardResponse(organizationCommunityMember);
        response.putAll(organizationMemberBehavior.cancelRequest(organizationCommunityMember));
        return response;*/
        return organizationMemberBehavior.cancelRequest(organizationCommunityMember);
    }

    /**
     * Исключить организацию из объединения от имени объединения
     * @param memberId
     * @param currentUser
     * @return
     */
    public OrganizationMembersHandleResult requestFromCommunityOwnerToExcludeFromCommunity(Long memberId, User currentUser) {
        OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getById(memberId);
        check((organizationCommunityMember != null && organizationCommunityMember.getStatus() == CommunityMemberStatus.MEMBER),
                "Организация не состоит в объединении");

        Community community = organizationCommunityMember.getCommunity();
        IOrganizationMemberBehavior organizationMemberBehavior = organizationMemberBehaviorResolver.getOrganizationMemberBehavior(community);
        // Проверка прав внутри реализации
        /*Map<String, Object> response = getStandardResponse(organizationCommunityMember);
        response.putAll(organizationMemberBehavior.requestFromCommunityOwnerToExcludeFromCommunity(organizationCommunityMember, currentUser));*/
        return organizationMemberBehavior.requestFromCommunityOwnerToExcludeFromCommunity(organizationCommunityMember, currentUser);
    }

    /**
     * Исключить организацию из объединения от имени организации
     * @param memberId
     * @param currentUser
     * @return
     */
    public OrganizationMembersHandleResult requestFromOrganizationToExcludeFromCommunity(Long memberId, User currentUser) {
        OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getById(memberId);
        check((organizationCommunityMember != null && organizationCommunityMember.getStatus() == CommunityMemberStatus.MEMBER),
                "Организация не состоит в объединении");

        Community community = organizationCommunityMember.getCommunity();
        Community organization = organizationCommunityMember.getOrganization();

        User director = communitiesService.getCommunityDirector(organization);
        check(director != null, "Не установлен директор в организации");
        check(director.getId().equals(currentUser.getId()), "У Вас нет прав на выход организации из объединения");

        IOrganizationMemberBehavior organizationMemberBehavior = organizationMemberBehaviorResolver.getOrganizationMemberBehavior(community);
        /*Map<String, Object> response = getStandardResponse(organizationCommunityMember);
        response.putAll(organizationMemberBehavior.requestFromOrganizationToExcludeFromCommunity(organizationCommunityMember));*/
        return organizationMemberBehavior.requestFromOrganizationToExcludeFromCommunity(organizationCommunityMember);
    }

    /**
     * Принятие объединения в участники
     * @param memberId
     * @param currentUser
     * @return
     */
    public OrganizationMembersHandleResult acceptToJoinInCommunity(Long memberId, User currentUser) {
        OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getById(memberId);

        check((organizationCommunityMember != null &&
                        (organizationCommunityMember.getStatus() == CommunityMemberStatus.REQUEST ||
                         organizationCommunityMember.getStatus() == CommunityMemberStatus.CONDITION_DONE_REQUEST)),
                "Не найден запрос на вступление в объединение");

        Community community = organizationCommunityMember.getCommunity();
        IOrganizationMemberBehavior organizationMemberBehavior = organizationMemberBehaviorResolver.getOrganizationMemberBehavior(community);

        /*Map<String, Object> response = getStandardResponse(organizationCommunityMember);
        response.putAll(organizationMemberBehavior.acceptToJoinInCommunity(organizationCommunityMember.getCommunity(), Collections.singletonList(organizationCommunityMember), currentUser));*/
        return organizationMemberBehavior.acceptToJoinInCommunity(organizationCommunityMember.getCommunity(), Collections.singletonList(organizationCommunityMember), currentUser);
    }

    /**
     * Принятие организации в участники
     * @param memberIds
     * @param currentUserId
     * @return
     */
    public OrganizationMembersHandleResult acceptToJoinInCommunity(List<Long> memberIds, Long currentUserId) {
        List<OrganizationCommunityMember> organizationCommunityMembers = new ArrayList<>();
        User currentUser = userDataService.getByIdFullData(currentUserId);
        Community community = null;
        for (Long memberId : memberIds) {
            OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getById(memberId);

            check((organizationCommunityMember != null &&
                            (organizationCommunityMember.getStatus() == CommunityMemberStatus.REQUEST ||
                             organizationCommunityMember.getStatus() == CommunityMemberStatus.CONDITION_DONE_REQUEST)),
                    "Организация не состоит в объединении либо неверный статус организации в объединении");

            organizationCommunityMembers.add(organizationCommunityMember);
            if (community == null) {
                community = organizationCommunityMember.getCommunity();
            }
            check(community.getId().equals(organizationCommunityMember.getCommunity().getId()), "Ошибка в ИД участника объединения.");
        }
        IOrganizationMemberBehavior organizationMemberBehavior = organizationMemberBehaviorResolver.getOrganizationMemberBehavior(community);
        return organizationMemberBehavior.acceptToJoinInCommunity(community, organizationCommunityMembers, currentUser);
    }

    /**
     * Вывод организаций из участников
     * @param memberIds
     * @param currentUserId
     * @return
     */
    public OrganizationMembersHandleResult acceptExcludeFromCommunity(List<Long> memberIds, Long currentUserId) {
        List<OrganizationCommunityMember> organizationCommunityMembers = new ArrayList<>();
        User currentUser = userDataService.getByIdFullData(currentUserId);
        Community community = null;
        for (Long memberId : memberIds) {
            OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getById(memberId);
            organizationCommunityMembers.add(organizationCommunityMember);
            if (community == null) {
                community = organizationCommunityMember.getCommunity();
            }
            check(community.getId().equals(organizationCommunityMember.getCommunity().getId()), "Ошибка в ИД участника объединения.");
        }
        IOrganizationMemberBehavior organizationMemberBehavior = organizationMemberBehaviorResolver.getOrganizationMemberBehavior(community);
        return organizationMemberBehavior.acceptExcludeFromCommunity(community, organizationCommunityMembers, currentUser);
    }

    // Отмена запроса на выход из объединения
    public OrganizationMembersHandleResult cancelExcludeRequest(Long memberId, User currentUser) {
        OrganizationCommunityMember organizationCommunityMember = organizationMemberDomainService.getById(memberId);

        check(organizationCommunityMember != null, "Не найден запрос на вступление в объединение");

        User director = communitiesService.getCommunityDirector(organizationCommunityMember.getOrganization());
        check(director != null, "Не установлен директор в организации");
        check(director.getId().equals(currentUser.getId()), "У Вас нет прав на выход организации из объединения");

        check(organizationCommunityMember.getStatus().equals(CommunityMemberStatus.REQUEST_TO_LEAVE),
                "Статус организации не соответвует запросу");

        IOrganizationMemberBehavior organizationMemberBehavior = organizationMemberBehaviorResolver.getOrganizationMemberBehavior(organizationCommunityMember.getCommunity());
        return organizationMemberBehavior.cancelExcludeRequest(organizationCommunityMember);
    }

    /**
     * Кандидаты на выход из объединения через выполнение условий
     * @param communityId
     * @param userId
     * @return
     */
    public LeaveOrganizationCommunityMembersDto getLeaveCommunityMembers(Long communityId, Long userId) {
        Community community = communityDomainService.getByIdFullData(communityId);
        return organizationMemberBehaviorResolver.getOrganizationMemberBehavior(community).getLeaveCommunityMembers(community, userId);
    }

    /**
     * Кандидаты на вступление в объединение через выполнение условий
     * @param communityId
     * @param userId
     * @return
     */
    public ApproveOrganizationCommunityMembersDto getApproveCommunityMembers(Long communityId, Long userId) {
        Community community = communityDomainService.getByIdFullData(communityId);
        return organizationMemberBehaviorResolver.getOrganizationMemberBehavior(community).getApproveCommunityMembers(community, userId);
    }


}