package ru.askor.blagosfera.data.jpa.repositories.document;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplateEntity, Long> {

    DocumentTemplateEntity findByCode(String code);
}
