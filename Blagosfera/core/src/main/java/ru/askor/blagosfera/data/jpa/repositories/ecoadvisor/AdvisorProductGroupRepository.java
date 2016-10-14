package ru.askor.blagosfera.data.jpa.repositories.ecoadvisor;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.ecoadvisor.AdvisorProductGroupEntity;

/**
 * Created by Maxim Nikitin on 24.02.2016.
 */
public interface AdvisorProductGroupRepository extends JpaRepository<AdvisorProductGroupEntity, Long> {
}
