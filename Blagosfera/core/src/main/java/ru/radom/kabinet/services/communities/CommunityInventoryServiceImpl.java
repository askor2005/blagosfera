package ru.radom.kabinet.services.communities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityInventoryUnit;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.utils.StringUtils;

import java.util.UUID;

@Transactional
@Service
public class CommunityInventoryServiceImpl implements CommunityInventoryService {

    @Autowired
    private CommunityInventoryDomainService communityInventoryDomainService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    public CommunityInventoryUnit saveUnit(CommunityInventoryUnit unit, Long communityId) {
        if (unit.getId() != null && !unit.getCommunity().getId().equals(communityId)) {
            throw new CommunityInventoryException("Объект принадлежит другому объединению.");
        }

        if (unit.getLeasedTo() != null && unit.getLeasedTo().getId() != null && unit.getLeasedTo().getId().equals(communityId)) {
            throw new CommunityInventoryException("Объект не может быть сдан в аренду владельцу.");
        }

        if (unit.getId() == null) {
            unit.setGuid(UUID.randomUUID().toString());

            if (unit.getCommunity() == null) {
                throw new CommunityInventoryException("Не задано объединение");
            }
        }

        if (unit.getType() == null || unit.getType().getId() == null) {
            throw new CommunityInventoryException("Не задан тип");
        }

        if (StringUtils.isEmpty(unit.getNumber())) {
            throw new CommunityInventoryException("Не задан инвентарный номер");
        }

        if (StringUtils.isEmpty(unit.getPhoto())) {
            throw new CommunityInventoryException("Не загружено фото");
        }

        CommunityInventoryUnit existingUnit = communityInventoryDomainService.findByCommunityIdAndNumber(unit.getCommunity().getId(), unit.getNumber());

        if ((existingUnit != null) && (!existingUnit.getId().equals(unit.getId()))) {
            throw new CommunityInventoryException("Инвентарный номер занят");
        }

        CommunityMember responsible = communityMemberDomainService.getByIdFullData(unit.getResponsible().getId());

        if (unit.getResponsible() != null && !responsible.getCommunity().getId().equals(unit.getCommunity().getId())) {
            throw new CommunityInventoryException("Выбранное ответственное лицо не состоит в объединении");
        }

        return communityInventoryDomainService.save(unit);
    }

    public CommunityInventoryUnit deleteUnit(Long unitId, Long communityId) {
        CommunityInventoryUnit unit = communityInventoryDomainService.getById(unitId);

        if (unit.getCommunity().getId().equals(communityId)) {
            communityInventoryDomainService.delete(unitId);
        } else if ((unit.getLeasedTo() != null) && unit.getLeasedTo().getId().equals(communityId)) {
            unit.setLeasedTo(null);
            communityInventoryDomainService.save(unit);
        }

        return unit;
    }

    /*public CommunityInventoryUnitEntity getUnit(CommunityEntity community, CommunityInventoryUnitEntity unit) {
        // TODO Переделать
        Community communityDomain = community.toDomain();

        communitiesService.checkPermission(communityDomain, SecurityUtils.getUser(), "SETTINGS_INVENTORY", "У Вас нет прав на управление инвентаризацией");
        return unit;
    }*/
}
