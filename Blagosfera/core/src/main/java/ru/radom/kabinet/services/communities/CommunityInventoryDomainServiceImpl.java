package ru.radom.kabinet.services.communities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.CommunityInventoryUnitRepository;
import ru.askor.blagosfera.data.jpa.repositories.CommunityInventoryUnitTypeRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityMemberRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.specifications.CommunityInventoryUnitSpecifications;
import ru.askor.blagosfera.domain.community.CommunityInventoryUnit;
import ru.askor.blagosfera.domain.community.CommunityInventoryUnitType;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.communities.inventory.CommunityInventoryUnitEntity;
import ru.radom.kabinet.model.communities.inventory.CommunityInventoryUnitTypeEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 31.03.2016.
 */
@Service
@Transactional
public class CommunityInventoryDomainServiceImpl implements CommunityInventoryDomainService {

    @Autowired
    private CommunityInventoryUnitTypeRepository communityInventoryUnitTypeRepository;

    @Autowired
    private CommunityInventoryUnitRepository communityInventoryUnitRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityMemberRepository communityMemberRepository;

    @Override
    public CommunityInventoryUnit getById(Long id) {
        return CommunityInventoryUnitEntity.toDomainSafe(communityInventoryUnitRepository.findOne(id));
    }

    public List<CommunityInventoryUnit> getList(Long communityId, Long typeId) {
        return getList(communityId, typeId, null);
    }

    public List<CommunityInventoryUnit> getList(Long communityId, Long typeId, String number) {
        CommunityEntity communityEntity = communityRepository.findOne(communityId);

        //communitiesService.checkPermission(communityDomain, SecurityUtils.getUser(), "SETTINGS_INVENTORY", "У Вас нет прав на управление инвентаризацией");

        Specifications<CommunityInventoryUnitEntity> filterSpec = Specifications.where(
                CommunityInventoryUnitSpecifications.numberLike(number));

        if (typeId != null) {
            CommunityInventoryUnitTypeEntity type = communityInventoryUnitTypeRepository.findOne(typeId);
            filterSpec.and(CommunityInventoryUnitSpecifications.typeIs(type));
        }

        Specifications<CommunityInventoryUnitEntity> communitySpec = Specifications.where(
                CommunityInventoryUnitSpecifications.ownedBy(communityEntity))
                .or(CommunityInventoryUnitSpecifications.leasedTo(communityEntity));

        return CommunityInventoryUnitEntity.toListDomain(communityInventoryUnitRepository.findAll(Specifications.where(filterSpec.and(communitySpec))));
    }

    @Override
    public CommunityInventoryUnit save(CommunityInventoryUnit unit) {
        CommunityEntity communityEntity = null;
        if (unit.getCommunity() != null && unit.getCommunity().getId() != null) {
            communityEntity = communityRepository.getOne(unit.getCommunity().getId());
        }

        CommunityEntity leasedToEntity = null;
        if (unit.getLeasedTo() != null && unit.getLeasedTo().getId() != null) {
            leasedToEntity = communityRepository.getOne(unit.getLeasedTo().getId());
        }

        CommunityMemberEntity responsibleEntity = null;
        if (unit.getResponsible() != null && unit.getResponsible().getId() != null) {
            responsibleEntity =  communityMemberRepository.getOne(unit.getResponsible().getId());
        }

        CommunityInventoryUnitTypeEntity typeEntity = null;
        if (unit.getType() != null && unit.getType().getId() != null) {
            typeEntity = communityInventoryUnitTypeRepository.getOne(unit.getType().getId());
        }

        CommunityInventoryUnitEntity entity;
        if (unit.getId() == null) {
            entity = new CommunityInventoryUnitEntity();
            entity.setId(unit.getId());
            entity.setGuid(unit.getGuid());
        } else {
            entity = communityInventoryUnitRepository.findOne(unit.getId());
        }
        entity.setDescription(unit.getDescription());
        entity.setNumber(unit.getNumber());
        entity.setPhoto(unit.getPhoto());
        entity.setCommunity(communityEntity);
        entity.setLeasedTo(leasedToEntity);
        entity.setResponsible(responsibleEntity);
        entity.setType(typeEntity);
        entity = communityInventoryUnitRepository.save(entity);
        return CommunityInventoryUnitEntity.toDomainSafe(entity);
    }

    @Override
    public CommunityInventoryUnit delete(Long id) {
        CommunityInventoryUnit result = getById(id);
        communityInventoryUnitRepository.delete(id);
        return result;
    }

    @Override
    public List<CommunityInventoryUnitType> getAllTypes() {
        return CommunityInventoryUnitTypeEntity.toListDomain(communityInventoryUnitTypeRepository.findAll());
    }

    @Override
    public CommunityInventoryUnit findByCommunityIdAndNumber(Long communityId, String number) {
        CommunityInventoryUnitEntity communityInventoryUnitEntity = communityInventoryUnitRepository.findOneByCommunity_IdAndNumber(communityId, number);
        return CommunityInventoryUnitEntity.toDomainSafe(communityInventoryUnitEntity);
    }

    @Override
    public String getGuidById(Long id) {
        return communityInventoryUnitRepository.getGuidById(id);
    }

    @Override
    public CommunityInventoryUnit getByGuid(String guid) {
        return CommunityInventoryUnitEntity.toDomainSafe(communityInventoryUnitRepository.findOneByGuid(guid));
    }
}
