package ru.askor.blagosfera.data.jpa.repositories.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.document.model.DocumentClassEntity;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
public interface DocumentClassRepository extends JpaRepository<DocumentClassEntity, Long> {

    /**
     * Загрузить класс документов по ИД шаблона
     * @param templateId
     * @return
     */
    @Query("select dc from DocumentClassEntity dc join dc.documentTemplates dt where dt.id = :templateId")
    DocumentClassEntity getByDocumentTemplateId(@Param("templateId") Long templateId);
}
