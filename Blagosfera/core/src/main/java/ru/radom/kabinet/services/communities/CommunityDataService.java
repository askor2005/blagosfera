package ru.radom.kabinet.services.communities;

import ru.askor.blagosfera.domain.community.*;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.askor.blagosfera.domain.field.FieldFile;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.communities.dto.FieldValueParameterDto;
import ru.radom.kabinet.model.communities.CommunityEntity;

import java.util.List;

/**
 * Сервис для работы с моделью объединения
 * Created by vgusev on 08.03.2016.
 */
public interface CommunityDataService {

    /**
     * Загрузить объединение со всеми данными
     * @param communityId
     * @return
     */
    Community getByIdFullData(Long communityId);

    /**
     * Загрузка связанных объектов, минимальных полей и списка полей который передали
     * @param communityId
     * @param fieldNames
     * @return
     */
    Community getByIdMediumData(Long communityId, List<String> fieldNames);

    /**
     * Загрузка связанных объектов, минимальных полей
     * @param communityId
     * @return
     */
    Community getByIdMediumData(Long communityId);

    /**
     * Загрузить объединение с минимальными данными - без полей, без связанных объектов
     * @param communityId
     * @return
     */
    Community getByIdMinData(Long communityId);

    /**
     *
     * @param fieldValueParameters
     * @return
     */
    List<Community> getByFieldsMinData(FieldValueParameterDto... fieldValueParameters);

    /**
     * Загрузить объединения у которых не установлено поле с названием fieldName
     * @param fieldName
     * @return
     */
    List<Community> getByEmptyFieldMinData(String fieldName);
    List<Community> getTopVisitForUser(Long userId, Long page, String name);

    /**
     * Создать или обновить
     * @param community
     * @param editor
     */
    Community save(Community community, User editor);

    /**
     * Удалить
     * @param id
     * @param deleter
     */
    void delete(Long id, User deleter);

    /**
     * Фильтрация списка объединений
     * @param userId
     * @param isAdmin
     * @param statusList
     * @param creator
     * @param firstResult
     * @param maxResults
     * @param query
     * @param accessType
     * @param communityType
     * @param activityScopeId
     * @param parent
     * @param checkParent
     * @param deleted
     * @param orderBy
     * @param asc
     * @return
     */
    List<Community> getList(
            Long userId, boolean isAdmin, List<CommunityMemberStatus> statusList,
            Boolean creator, int firstResult, int maxResults, String query,
            CommunityAccessType accessType, String communityType, Long activityScopeId,
            CommunityEntity parent, boolean checkParent, Boolean deleted, String orderBy, boolean asc);

    /**
     * Количество объединений в фильтрации
     * @param userId
     * @param isAdmin
     * @param statusList
     * @param creator
     * @param query
     * @param accessType
     * @param communityType
     * @param activityScopeId
     * @param parent
     * @param checkParent
     * @param deleted
     * @return
     */
    long getListCount(
            Long userId, boolean isAdmin, List<CommunityMemberStatus> statusList,
            Boolean creator, String query, CommunityAccessType accessType, String communityType,
            Long activityScopeId, CommunityEntity parent, boolean checkParent, Boolean deleted);

    /**
     *
     * @param communityIds
     * @param sharerId
     * @return
     */
    List<CommunityMember> getByCommunityIdsAndUserId(List<Long> communityIds, Long userId);

    /**
     * Загрузить все разделы объединения
     * @return
     */
    List<CommunitySectionDomain> getAllCommunitySections();

    /**
     * Загрузить раздел объединения по ссылке
     * @param link
     * @return
     */
    CommunitySectionDomain getCommunitySectionByLink(String link);

    /**
     * Являетя ли пользователь участником объединения
     * @param communityId
     * @param sharerId
     * @return
     */
    boolean isSharerMember(Long communityId, Long userId);

    /**
     * Являетя ли объединение ПО или КУч
     * @param communityId
     * @return
     */
    boolean isConsumerSociety(Long communityId);

    /**
     * Загрузить по ИД или seoLink
     * @return
     */
    Community getBySeoLinkOrIdMediumData(String seoLink);

    /**
     *
     * @param community
     * @return
     */
    List<Community> getParents(Community community);

    /**
     *получить все объединения в которых пользователь обладает заданной ролью
     * @param user
     * @param permission
     * @return
     */
    List<Community> findCommunitiesByUserPermission(User user, String permission);

    /**
     * получить подгруппы заданного объединения, в которых пользователь обладает заданной ролью
     * @param user
     * @param permission
     * @param parentId
     * @return
     */
    List<Community> findCommunitiesByParentAndUserPermission(User user, String permission, Long parentId);

    /**
     *
     * @param seoLink
     * @return
     */
    Long findCommunityId(String seoLink);

    /**
     *
     * @param associationFormInternalName
     * @return
     */
    Community getByAssociationFormInternalName(String associationFormInternalName);

    /**
     * Загрузить список возможных участников объединения - организаций
     * @param userId
     * @param communityId
     * @param query
     * @param firstResult
     * @param maxResults
     * @return
     */
    List<Community> getPossibleCommunitiesMembers(Long userId, Long communityId, String query, int firstResult, int maxResults);

    /**
     * Получить количество возможных участников объединения - организаций
     * @param userId
     * @param communityId
     * @param query
     * @return
     */
    long getPossibleCommunitiesMembersCount(Long userId, Long communityId, String query);

    /**
     * Загрузить список файлов поля объединения
     * @param communityId
     * @param fieldId
     * @return
     */
    List<FieldFile> getCommunityFieldFiles(Long communityId, Long fieldId);

    /**
     * Показать\скрыть поле у объединения
     * @param communityId
     * @param fieldId
     * @param hidden
     */
    void setFieldValueHidden(Long communityId, Long fieldId, boolean hidden);

    /**
     * Показать\скрыть группу полей у объединения
     * @param communityId
     * @param fieldsGroupId
     * @param hidden
     */
    void setFieldValuesGroupHidden(Long communityId, Long fieldsGroupId, boolean hidden);

    /**
     * Загрузить список объединений по ИД родиетельского объединения
     * @param parentId
     * @return
     */
    List<Community> getByParentId(Long parentId, int page, int perPage);

    /**
     * Загрузить список объединений по ИД родиетельского объединения
     * @param parentId
     * @return
     */
    List<Community> getByParentId(Long parentId);

    /**
     *
     * @param communityId
     * @param documentTemplateSettings
     * @param needCreateDocuments
     */
    List<DocumentTemplateSetting> saveDocumentTemplateSettings(Long communityId, List<DocumentTemplateSetting> documentTemplateSettings, boolean needCreateDocuments);

    List<Community> getCommunitiesCreated(Long userId);

    List<Community> getCommunitiesMember(Long userId);
}
