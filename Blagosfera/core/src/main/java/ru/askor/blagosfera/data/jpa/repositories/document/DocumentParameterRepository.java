package ru.askor.blagosfera.data.jpa.repositories.document;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.document.model.DocumentFolderEntity;
import ru.radom.kabinet.document.model.DocumentParameterEntity;

/**
 *
 * Created by vgusev on 14.04.2016.
 */
public interface DocumentParameterRepository extends JpaRepository<DocumentParameterEntity, Long> {
}
