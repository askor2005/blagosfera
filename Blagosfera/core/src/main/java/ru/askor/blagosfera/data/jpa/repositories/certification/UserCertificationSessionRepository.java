package ru.askor.blagosfera.data.jpa.repositories.certification;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.UserCertificationSessionEntity;

public interface UserCertificationSessionRepository extends JpaRepository<UserCertificationSessionEntity, Long> {

    UserCertificationSessionEntity findOneByRegistrator_IdAndUser_IdAndEndDateNull(Long registratorId, Long userId);

    UserCertificationSessionEntity findOneBySessionIdAndEndDateNull(String sessionId);
}
