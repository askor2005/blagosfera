package ru.radom.kabinet.document.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentFolderRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentRepository;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentFolder;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.document.model.DocumentFolderEntity;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * Created by vgusev on 02.08.2016.
 */
@Service
@Transactional
public class DocumentFolderDataServiceImpl implements DocumentFolderDataService {

    @Autowired
    private DocumentFolderRepository documentFolderRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentDomainService documentDomainService;

    @Override
    public DocumentFolder getById(Long id) {
        DocumentFolder reuslt = null;
        DocumentFolderEntity entity = documentFolderRepository.findOne(id);
        if (entity != null) {
            reuslt = entity.toDomain(true, true);
        }
        return reuslt;
    }

    @Override
    public DocumentFolder delete(Long id) {
        DocumentFolderEntity entity = documentFolderRepository.findOne(id);
        ExceptionUtils.check(entity == null, "Каталог документов с ИД " + id + " не наден");
        documentFolderRepository.delete(id);
        return entity.toDomain(true, true);
    }

    @Override
    public DocumentFolder save(DocumentFolder documentFolder) {
        DocumentFolderEntity entity;
        if (documentFolder.getId() == null) {
            entity = new DocumentFolderEntity();
        } else {
            entity = documentFolderRepository.findOne(documentFolder.getId());
        }
        entity.setDescription(documentFolder.getDescription());
        entity.setName(documentFolder.getName());
        entity = documentFolderRepository.save(entity);
        DocumentFolder result = entity.toDomain(true, true);
        if (documentFolder.getDocuments() != null) {
            addDocumentsToDocumentFolder(documentFolder.getDocuments(), entity);
            entity = documentFolderRepository.findOne(entity.getId());
            result = entity.toDomain(true, true);
        }
        return result;
    }


    private void addDocumentsToDocumentFolder(Collection<Document> documents, DocumentFolderEntity documentFolderEntity) {
        Set<DocumentEntity> documentEntities = new HashSet<>();
        for (Document document : documents) {
            DocumentEntity documentEntity = documentRepository.getOne(document.getId());
            documentEntity.setFolder(documentFolderEntity);
            documentEntities.add(documentEntity);
        }
        documentFolderEntity.getDocuments().clear();
        documentFolderEntity.getDocuments().addAll(documentEntities);
    }
}
