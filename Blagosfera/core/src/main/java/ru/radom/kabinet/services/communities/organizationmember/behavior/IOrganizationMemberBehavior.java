package ru.radom.kabinet.services.communities.organizationmember.behavior;

import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.OrganizationCommunityMember;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.services.communities.organizationmember.dto.ApproveOrganizationCommunityMembersDto;
import ru.radom.kabinet.services.communities.organizationmember.dto.LeaveOrganizationCommunityMembersDto;
import ru.radom.kabinet.services.communities.organizationmember.dto.OrganizationMembersHandleResult;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 20.10.2015.
 */
public interface IOrganizationMemberBehavior {

    // Запрос на вступление в объединение от организации - кандидата
    OrganizationMembersHandleResult requestToJoinInCommunity(Community community, Community candidateToMember);

    // Отказ о вступлении в объединение несколких организаций
    OrganizationMembersHandleResult rejectRequests(Community community, List<OrganizationCommunityMember> organizationCommunityMembers, User currentUser);

    // Отмена вступления в объединение от имени руководителя организации
    OrganizationMembersHandleResult cancelRequest(OrganizationCommunityMember organizationCommunityMember);

    // Принятие запроса на вступление в объединение нескольких участников
    OrganizationMembersHandleResult acceptToJoinInCommunity(Community community, List<OrganizationCommunityMember> organizationCommunityMembers, User currentUser);

    // Запрос от руководтсва организации - члена на выход из объединения
    OrganizationMembersHandleResult requestFromOrganizationToExcludeFromCommunity(OrganizationCommunityMember organizationCommunityMember);

    // Запрос на исключение организации из объединения руководством объединения
    OrganizationMembersHandleResult requestFromCommunityOwnerToExcludeFromCommunity(OrganizationCommunityMember organizationCommunityMember, User currentUser);

    // Принятие запроса на выход из объединения
    OrganizationMembersHandleResult acceptExcludeFromCommunity(Community community, List<OrganizationCommunityMember> organizationCommunityMembers, User currentUser);

    // Отмена запроса на выход из объедиения
    OrganizationMembersHandleResult cancelExcludeRequest(OrganizationCommunityMember organizationCommunityMember);

    // Получить всех участников, у которых есть запрос на выход из объединения с выполненным условием
    LeaveOrganizationCommunityMembersDto getLeaveCommunityMembers(Community community, Long userId);

    // Получить всех участников, у которых есть запрос на вход в объединение с выполненным условием
    ApproveOrganizationCommunityMembersDto getApproveCommunityMembers(Community community, Long userId);
}
