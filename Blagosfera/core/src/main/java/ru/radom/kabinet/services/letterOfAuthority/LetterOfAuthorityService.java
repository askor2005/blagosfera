package ru.radom.kabinet.services.letterOfAuthority;

import ru.askor.blagosfera.domain.letterofauthority.LetterOfAuthorityRole;
import ru.askor.blagosfera.domain.loa.LetterOfAuthorityScope;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityPermissionEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.letterofauthority.CommunityRoleTypes;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityAttributeEntity;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityEntity;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityRoleEntity;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.radom.kabinet.web.lettersofauthority.dto.LetterOfAuthorityDto;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface LetterOfAuthorityService {

    // Количество элементов в таблице
    int COUNT_ELEMENTS_IN_GRID_PAGE = 15;

    // Типы объектов в рамках которых создаётся доверенность
    Map<String, String> SCOPE_TYPES = new HashMap<String, String>(){{
        put(Discriminators.COMMUNITY, "Объединение юр. лицо");
        put(Discriminators.SHARER, "Физ лицо");
    }};

    // Типы ролей в объединении для фильтрации
    Map<String, String> SCOPE_ROLE_TYPES = new HashMap<String, String>(){{
        put(CommunityRoleTypes.COMMUNITY_POST, "Должность в объединении");
        put(CommunityRoleTypes.COMMUNITY_PERMISSION, "Права в объединении");
        put(CommunityRoleTypes.COMMUNITY_FIELD_SHARER, "Участник установленный в полях объединения");
        put(CommunityRoleTypes.COMMUNITY_FIELD_SHARER_LIST, "Список участников установленный в полях объединения");
    }};

    List<CommunityPermissionEntity> getAllCommunityPermissions(); // Все права доступа в объединениях
    List<FieldEntity> getSharerCommunityFields(); // Список полей в объединениях с типом SHARER
    List<FieldEntity> getSharerListCommunityFields(); // Список полей в объединениях с типом SHARER_LIST

    List<LetterOfAuthorityRoleEntity> getAllLetterOfAuthorityRoles();
    List<LetterOfAuthorityRoleEntity> getLetterOfAuthorityRolesByFilter(String nameSearchString, int page);
    int getCountLetterOfAuthorityRolesByFilter(String nameSearchString);
    void createLetterOfAuthorityRole(LetterOfAuthorityRoleEntity letterOfAuthorityRoleEntityForm, Long listEditorItemId);
    void updateLetterOfAuthorityRole(LetterOfAuthorityRoleEntity letterOfAuthorityRoleEntityForm, Long listEditorItemId);
    void deleteLetterOfAuthorityRole(Long id);
    List<LetterOfAuthorityRole> getLetterOfAuthorityRoles(String scopeType);
    List<RadomAccount> getRadomAccountsByRole(String roleKey);
    PossibleDelegates getPossibleDelegates(String roleKey, Long radomAccountId, int page, String searchString);
    void createLetterOfAuthority(String roleKey, Date expiredDate, Long radomAccountId, Long delegateId, Map<String, String> attributes);
    void createLetterOfAuthority(String roleKey, Date expiredDate, Long radomAccountId, Long delegateId, Map<String, String> attributes, Long ownerId);
    void updateLetterOfAuthority(LetterOfAuthorityDto letterOfAuthorityDto);
    void deleteLetterOfAuthority(Long letterOfAuthorityId);

    LetterOfAuthorityEntity getLetterOfAuthority(Long id);
    List<LetterOfAuthorityEntity> getOwnerLettersOfAuthority(String searchString, int page); // Список доверенностей, выданных текущим пользователем
    List<LetterOfAuthorityEntity> getMyLettersOfAuthority(String searchString, int page); // Список доверенностей, выданных текущему пользователю

    boolean checkLetterOfAuthority(String roleKey); // Проверка валидности доверенности у текущего пользователя
    @Deprecated
    boolean checkLetterOfAuthority(String roleKey, UserEntity delegate); // Проверка валидности доверенности у переданного пользователя
    @Deprecated
    boolean checkLetterOfAuthority(String roleKey, UserEntity delegate, RadomAccount scope);
    @Deprecated
    boolean checkLetterOfAuthority(String roleKey, UserEntity delegate, RadomAccount scope, Map<String, String> attributes); // Проверка валидности доверенности у переданного пользователя в рамках объекта доверенности
    @Deprecated
    boolean checkLetterOfAuthority(String roleKey, UserEntity delegate, RadomAccount scope, List<String> nonExistentAttributes);


    boolean checkLetterOfAuthority(String roleKey, User delegate); // Проверка валидности доверенности у переданного пользователя
    boolean checkLetterOfAuthority(String roleKey, User delegate, LetterOfAuthorityScope scope);
    boolean checkLetterOfAuthority(String roleKey, User delegate, LetterOfAuthorityScope scope, Map<String, String> attributes); // Проверка валидности доверенности у переданного пользователя в рамках объекта доверенности
    boolean checkLetterOfAuthority(String roleKey, User delegate, LetterOfAuthorityScope scope, List<String> nonExistentAttributes);


    List<LetterOfAuthorityAttributeEntity> getAttributes(Long letterOfAuthorityId, String name, int page);
    int getAttributesCount(Long letterOfAuthorityId, String name);
    void saveAttribute(Long letterOfAuthorityId, String name, String value);
    void updateAttribute(Long id, String name, String value);
    void deleteAttribute(Long id);

    void checkLetterOfAuthorities();
}
