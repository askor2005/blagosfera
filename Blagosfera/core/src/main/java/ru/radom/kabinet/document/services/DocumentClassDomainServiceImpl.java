package ru.radom.kabinet.document.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentClassDataSourceRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentClassRepository;
import ru.askor.blagosfera.domain.document.DocumentClass;
import ru.askor.blagosfera.domain.document.DocumentClassDataSource;
import ru.radom.kabinet.document.model.DocumentClassDataSourceEntity;
import ru.radom.kabinet.document.model.DocumentClassEntity;

import java.util.List;

/**
 *
 * Created by vgusev on 11.04.2016.
 */
@Service
@Transactional
public class DocumentClassDomainServiceImpl implements DocumentClassDomainService {

    @Autowired
    private DocumentClassRepository documentClassRepository;

    @Autowired
    private DocumentClassDataSourceRepository documentClassDataSourceRepository;

    @Override
    public DocumentClass getById(Long id) {
        return DocumentClassEntity.toDomainSafe(documentClassRepository.findOne(id));
    }

    @Override
    public DocumentClass getByTemplateId(Long templateId) {
        return DocumentClassEntity.toDomainSafe(documentClassRepository.getByDocumentTemplateId(templateId));
    }

    @Override
    public DocumentClassDataSource getDataSourceById(Long dataSourceId) {
        return null;
    }

    @Override
    public List<DocumentClassDataSource> getDataSourcesByTemplateId(Long templateId) {
        return DocumentClassDataSourceEntity.toDomainList(documentClassDataSourceRepository.getByDocumentTemplateId(templateId));
    }
}
