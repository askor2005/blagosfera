package ru.radom.kabinet.services.communities;

import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityPost;
import ru.radom.kabinet.dto.community.CommunityUserPost;
import ru.radom.kabinet.model.communities.CommunityEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 18.03.2016.
 */
public interface CommunityPostDomainService {

    // TODO Переделать на доменную модель

    /**
     * Получить пост руководителя
     * @param community
     * @return
     */
    CommunityPost getCeo(Community community);

    /**
     * Получить пост по названию
     * @param community
     * @param name
     * @return
     */
    CommunityPost getByName(Community community, String name);

    /**
     *
     * @param id
     * @return
     */
    CommunityPost getByIdFullData(Long id);

    /**
     *
     * @param id
     * @return
     */
    CommunityPost getById(Long id, boolean withCommunity, boolean withMembers, boolean withPermissions, boolean withSchema);

    /**
     * Проверить существование должности
     * @param post
     * @return
     */
    boolean checkPost(CommunityPost post);

    /**
     *
     * @param community
     * @param postName
     * @return
     */
    boolean checkPost(Community community, String postName);

    /**
     * Сохранить или обновить
     * @param post
     */
    CommunityPost save(CommunityPost post);

    /**
     *
     * @param id
     */
    void delete(Long id);

    /**
     *
     * @param post
     */
    void delete(CommunityPost post);

    /**
     * Загрузка должностей по ИД объединения
     * Загружаются данные должностей без связанных объектов
     * @param communityId
     * @param withCommunity
     * @param withMembers
     * @param withPermissions
     * @param withSchema
     * @return
     */
    List<CommunityPost> getByCommunityId(Long communityId, boolean withCommunity, boolean withMembers, boolean withPermissions, boolean withSchema);

    /**
     * Загрузить должности участников объединения
     * @param communityId
     * @param start
     * @param limit
     * @return
     */
    List<CommunityUserPost> getCommunityUserPosts(Long communityId, int start, int limit);

    /**
     * Количество должностей участников объединения
     * @param communityId
     * @return
     */
    int getCommunityUserPostsCount(Long communityId);
}
