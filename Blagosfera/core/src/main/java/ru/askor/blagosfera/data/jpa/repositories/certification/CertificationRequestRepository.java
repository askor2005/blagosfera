package ru.askor.blagosfera.data.jpa.repositories.certification;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.registration.RegistrationRequest;
import ru.radom.kabinet.model.registration.RegistrationRequestStatus;

import java.util.List;

/**
 * Created by Maxim Nikitin on 03.02.2016.
 */
public interface CertificationRequestRepository extends JpaRepository<RegistrationRequest, Long> {

    List<RegistrationRequest> findAllByRegistrator_IdAndStatus(Long registratorId, RegistrationRequestStatus status);
}
