package ru.askor.blagosfera.data.jpa.repositories.crypt;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.crypt.SessionKeyEntity;

/**
 * Created by Maxim Nikitin on 08.02.2016.
 */
public interface SessionKeyRepository extends JpaRepository<SessionKeyEntity, Long> {

}
