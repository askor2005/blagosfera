package ru.askor.blagosfera.data.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.communities.inventory.CommunityInventoryUnitEntity;

public interface CommunityInventoryUnitRepository extends JpaRepository<CommunityInventoryUnitEntity, Long>, JpaSpecificationExecutor<CommunityInventoryUnitEntity> {

    CommunityInventoryUnitEntity findOneByGuid(String guid);

    CommunityInventoryUnitEntity findOneByCommunity_IdAndNumber(long communityId, String number);

    @Query(value = "select guid from community_inventory_units where id = :id", nativeQuery = true)
    String getGuidById(@Param("id") Long id);
}
