package ru.radom.kabinet.services.communities.sharermember;

import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.collections.CommunityMemberStatusList;

import java.util.List;

/**
 *
 * Created by vgusev on 18.03.2016.
 */
public interface CommunityMemberDomainService {

    CommunityMember getByIdFullData(Long id);

    List<CommunityMember> getByIds(List<Long> ids, boolean withInviter, boolean withUser, boolean withCommunity, boolean withPosts);

    CommunityMember save(CommunityMember member);

    CommunityMember getByCommunityIdAndUserId(Long communityId, Long userId);

    List<CommunityMember> getByCommunityIdAndStatus(Long communityId, CommunityMemberStatus status);

    List<CommunityMember> getByCommunityIdChildren(Long communityId, CommunityMemberStatus status);

    CommunityMember delete(Long id);

    boolean exists(Long communityId, Long userId, CommunityMemberStatus status);

    boolean exists(Long communityId, Long userId);

    List<CommunityMember> getList(Community community, CommunityMemberStatusList statusList, int firstResult, int maxResults, String query, List<Long> excludeUserIds);

    /**
     * Загрузить список участников объединения
     * @param communityId ИД объединения
     * @param firstResult индекс первого элемента
     * @param maxResults количество элементов
     * @param query строка поиска пользователя
     * @param excludeUserIds ИД участников, которых не нужно включать в результат
     * @return
     */
    List<CommunityMember> getList(Long communityId, CommunityMemberStatusList statusList, int firstResult, int maxResults, String query, List<Long> excludeUserIds);

    /**
     *
     * @param communityId
     * @param statusList
     * @return
     */
    List<CommunityMember> getList(Long communityId, CommunityMemberStatusList statusList);

    List<CommunityMember> getByCommunityCreator(Long creatorId, List<CommunityMemberStatus> statusList, int page, int perPage);

    /**
     * Количество участников объединения
     * @param communityId
     * @return
     */
    int getMembersCount(Long communityId, List<CommunityMemberStatus> memberStatuses);

}
