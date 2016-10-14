package ru.askor.blagosfera.data.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.communities.inventory.CommunityInventoryUnitTypeEntity;

public interface CommunityInventoryUnitTypeRepository extends JpaRepository<CommunityInventoryUnitTypeEntity, Long>  {
}
