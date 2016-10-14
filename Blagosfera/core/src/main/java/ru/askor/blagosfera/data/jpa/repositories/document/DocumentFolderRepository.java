package ru.askor.blagosfera.data.jpa.repositories.document;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.document.model.DocumentFolderEntity;

public interface DocumentFolderRepository extends JpaRepository<DocumentFolderEntity, Long> {

}
