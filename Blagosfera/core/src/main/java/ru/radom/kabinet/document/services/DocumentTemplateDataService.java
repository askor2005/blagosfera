package ru.radom.kabinet.document.services;

import ru.askor.blagosfera.domain.document.DocumentTemplate;

import java.util.List;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
public interface DocumentTemplateDataService {

    DocumentTemplate getById(Long id);

    DocumentTemplate getByCode(String code);

    List<DocumentTemplate> getAll();

    List<DocumentTemplate> getFilteredTemplate(String namePart, Long templateClassId, Integer page, Integer perPage);
}
