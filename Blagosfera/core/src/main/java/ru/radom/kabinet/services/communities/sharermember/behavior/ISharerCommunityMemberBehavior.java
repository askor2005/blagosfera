package ru.radom.kabinet.services.communities.sharermember.behavior;

import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.services.communities.sharermember.dto.ApproveCommunityMembersDto;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;
import ru.radom.kabinet.services.communities.sharermember.dto.LeaveCommunityMembersDto;

import java.util.List;

/**
 * Интерфейс методов принятия и выхода участника фищ лица из объединения
 * Created by vgusev on 28.10.2015.
 */
public interface ISharerCommunityMemberBehavior {

    /**
     * Принять инвайт на вступление в объединение
     * @param member участник которого приглашали
     * @param notifySignEvent флаг - нужно ли отправлять оповещение о том, что нужно подписать документ
     * @return
     */
    //acceptInvite
    CommunityMemberResponseDto acceptInvite(CommunityMember member, boolean notifySignEvent);

    /**
     * Запрос участника вступить в объединение
     * @param community
     * @param requester
     * @return
     */
    // TODO Теперь вступление в отрытое объединение происходит тоже через этот метод
    //request
    //join
    CommunityMemberResponseDto request(Community community, User requester, boolean notifySignEvent);

    /**
     * Принять запросы на вступление в объединение
     * @param members
     * @param accepter
     * @return
     */
    //acceptRequest
    CommunityMemberResponseDto acceptRequests(List<CommunityMember> members, User accepter, boolean notifySignEvent);

    /**
     * Отклонить запросы на вступление в объединение от уполномоченного в объединении
     * @param members
     * @param rejecter
     * @return
     */
    //rejectRequest
    CommunityMemberResponseDto rejectRequestsFromCommunityOwner(List<CommunityMember> members, User rejecter);

    /**
     * Отмена запроса на вступление в объединение от участника
     * @param member
     * @param memberUser участник который создавал запрос на вступление
     * @return
     */
    //cancelRequest
    CommunityMemberResponseDto cancelRequestFromMember(CommunityMember member, User memberUser);

    /**
     * Запрос на исключение участника из объединения от уполномоченного в объединении
     * @param member
     * @param excluder уполномоченный участник объединения, который может исключать участников
     * @return
     */
    //exclude
    CommunityMemberResponseDto requestToExcludeFromCommunityOwner(CommunityMember member, User excluder);

    /**
     * Запрос на выход из объединения от участника объединения
     * @param member
     * @param leaver
     * @return
     */
    //leave
    CommunityMemberResponseDto requestToExcludeFromMember(CommunityMember member, User leaver);

    /**
     * Отмена запроса на выход из объединения от участника
     * @param member
     * @return
     */
    //cancelRequestToLeave
    void cancelRequestToLeaveFromMember(CommunityMember member);

    /**
     * Принять запросы на выход из объединения
     * @param members
     * @param excluder
     * @param notifySignEvent
     * @return
     */
    CommunityMemberResponseDto acceptRequestsToExcludeFromCommunity(List<CommunityMember> members, User excluder, boolean notifySignEvent);

    /**
     * Получить участников, которые создали запрос на выход из объединения
     * @param community
     * @param excluder
     * @return
     */
    LeaveCommunityMembersDto getLeaveCommunityMembers(Community community, User excluder);

    /**
     * Получить участников, которые создали запрос на вход в объединение
     * @param community
     * @param approver
     * @return
     */
    ApproveCommunityMembersDto getApproveCommunityMembers(Community community, User approver);

}
