package ru.askor.blagosfera.data.jpa.repositories.ecoadvisor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.ecoadvisor.AdvisorProductEntity;

public interface AdvisorProductRepository extends JpaRepository<AdvisorProductEntity, Long> {

    AdvisorProductEntity findOneByCodeAndParameters_CommunityId(String code, Long communityId);

    Page<AdvisorProductEntity> findByParameters_CommunityId(Long communityId, Pageable pageable);

    Page<AdvisorProductEntity> findByGroup_IdAndParameters_CommunityId(Long groupId, Long communityId, Pageable pageable);

    AdvisorProductEntity findOneByIdAndParameters_CommunityId(Long id, Long communityId);
}
