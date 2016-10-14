package ru.askor.blagosfera.data.jpa.repositories.ecoadvisor;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.ecoadvisor.AdvisorSystemParametersEntity;

public interface AdvisorSystemParametersRepository extends JpaRepository<AdvisorSystemParametersEntity, Long> {
}
