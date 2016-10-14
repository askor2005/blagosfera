package ru.askor.blagosfera.data.jpa.repositories.document;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.askor.blagosfera.domain.document.DocumentParticipant;
import ru.radom.kabinet.document.model.DocumentParticipantEntity;

/**
 *
 * Created by vgusev on 08.04.2016.
 */
public interface DocumentParticipantRepository extends JpaRepository<DocumentParticipantEntity, Long> {

}
