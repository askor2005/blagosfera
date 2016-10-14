package ru.radom.kabinet.document.services;

import ru.askor.blagosfera.domain.document.DocumentClass;
import ru.askor.blagosfera.domain.document.DocumentClassDataSource;

import java.util.List;

/**
 *
 * Created by vgusev on 11.04.2016.
 */
public interface DocumentClassDomainService {

    /**
     * Загрузить по ИД
     * @param id
     * @return
     */
    DocumentClass getById(Long id);

    /**
     * Загрузить класс документов по ИД шаблона
     * @param templateId
     * @return
     */
    DocumentClass getByTemplateId(Long templateId);

    /**
     * Загрузить истоник данных по ИД
     * @param dataSourceId
     * @return
     */
    DocumentClassDataSource getDataSourceById(Long dataSourceId);

    /**
     *
     * @param templateId
     * @return
     */
    List<DocumentClassDataSource> getDataSourcesByTemplateId(Long templateId);
}
