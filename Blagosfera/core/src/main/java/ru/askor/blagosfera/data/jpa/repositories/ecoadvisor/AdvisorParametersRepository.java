package ru.askor.blagosfera.data.jpa.repositories.ecoadvisor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.radom.kabinet.model.ecoadvisor.AdvisorParametersEntity;

public interface AdvisorParametersRepository extends JpaRepository<AdvisorParametersEntity, Long> {

    @Query("SELECT ap FROM AdvisorParametersEntity ap where (ap.id = 0) and (ap.communityId = 0)")
    AdvisorParametersEntity findDefault();

    AdvisorParametersEntity findOneByCommunityId(Long communityId);
}
