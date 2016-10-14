package ru.radom.kabinet.services.communities;

import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityPermission;

import java.util.List;
import java.util.Set;

/**
 *
 * Created by vgusev on 19.11.2015.
 */
public interface CommunityPermissionDomainService {

    /**
     *
     * @param id
     * @return
     */
    CommunityPermission getById(Long id, boolean withCommunities, boolean withAssociationForms);

    /**
     * Загрузка списка прав по ИД объединения без связанных объектов
     * @param communityId
     * @return
     */
    List<CommunityPermission> getByCommunityId(Long communityId);

    /**
     *
     * @param communityPermission
     */
    void save(CommunityPermission communityPermission);

    /**
     *
     * @param id
     */
    void delete(Long id);

    /**
     * Набор прав участника объединения
     * @param member
     * @return
     */
    Set<String> getPermissions(CommunityMember member);

}
