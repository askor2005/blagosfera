package ru.radom.kabinet.services.communities;

import ru.askor.blagosfera.domain.community.CommunityInventoryUnit;
import ru.askor.blagosfera.domain.community.CommunityInventoryUnitType;

import java.util.List;

/**
 *
 * Created by vgusev on 31.03.2016.
 */
public interface CommunityInventoryDomainService {

    CommunityInventoryUnit getById(Long id);

    List<CommunityInventoryUnit> getList(Long communityId, Long typeId);

    List<CommunityInventoryUnit> getList(Long communityId, Long typeId, String number);

    CommunityInventoryUnit save(CommunityInventoryUnit unit);

    CommunityInventoryUnit delete(Long id);

    List<CommunityInventoryUnitType> getAllTypes();

    /**
     * Найти по ИД объединения и иинвентарному номеру
     * @param communityId
     * @param number
     * @return
     */
    CommunityInventoryUnit findByCommunityIdAndNumber(Long communityId, String number);

    /**
     * Получить guid предмета по ИД
     * @param id
     * @return
     */
    String getGuidById(Long id);

    /**
     * Загрузить по guid
     * @param guid
     * @return
     */
    CommunityInventoryUnit getByGuid(String guid);
}
