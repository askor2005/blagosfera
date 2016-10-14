package ru.askor.blagosfera.data.jpa.repositories.certification;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.UserCertificationDocTypeEntity;

public interface UserCertificationDocTypeRepository extends JpaRepository<UserCertificationDocTypeEntity, Long> {
}
