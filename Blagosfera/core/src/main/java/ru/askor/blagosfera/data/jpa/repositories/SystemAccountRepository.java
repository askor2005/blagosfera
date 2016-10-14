package ru.askor.blagosfera.data.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.notifications.SystemAccountEntity;

/**
 *
 * Created by vgusev on 14.04.2016.
 */
public interface SystemAccountRepository extends JpaRepository<SystemAccountEntity, Long> {

}
