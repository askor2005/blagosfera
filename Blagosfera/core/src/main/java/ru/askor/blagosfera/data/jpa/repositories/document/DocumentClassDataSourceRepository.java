package ru.askor.blagosfera.data.jpa.repositories.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.document.model.DocumentClassDataSourceEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
public interface DocumentClassDataSourceRepository extends JpaRepository<DocumentClassDataSourceEntity, Long> {

    /**
     * Загрузить источники данных класса документов по ИД шаблона
     * @param templateId
     * @return
     */
    @Query("select dcds from DocumentClassDataSourceEntity dcds join dcds.documentType dc join dc.documentTemplates dt where dt.id = :templateId")
    List<DocumentClassDataSourceEntity> getByDocumentTemplateId(@Param("templateId") Long templateId);
}
