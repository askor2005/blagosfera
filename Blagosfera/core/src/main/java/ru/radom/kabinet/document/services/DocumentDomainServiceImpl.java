package ru.radom.kabinet.document.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentParameterRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentParticipantRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentRepository;
import ru.askor.blagosfera.domain.document.*;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.document.dao.FlowOfDocumentDao;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.document.model.DocumentParameterEntity;
import ru.radom.kabinet.document.model.DocumentParticipantEntity;
import ru.radom.kabinet.model.notifications.SystemAccountEntity;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.services.systemAccount.SystemAccountService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.*;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
@Service
@Transactional
public class DocumentDomainServiceImpl implements DocumentDomainService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private FlowOfDocumentDao documentDao;

    @Autowired
    private DocumentParticipantRepository documentParticipantRepository;

    @Autowired
    private DocumentParameterRepository documentParameterRepository;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private SystemAccountService systemAccountService;

    @Override
    public Document getById(Long id) {
        DocumentEntity entity = documentRepository.findOne(id);
        return DocumentEntity.toDomainSafe(entity, true, getDocumentCreator(entity));
    }

    @Override
    public Document getByCode(String code) {
        DocumentEntity entity = documentRepository.findByCode(code);
        return DocumentEntity.toDomainSafe(entity, true, getDocumentCreator(entity));
    }

    @Override
    public Document getByHashCode(String hashCode) {
        DocumentEntity entity = documentRepository.findByHashCode(hashCode);
        return DocumentEntity.toDomainSafe(entity, true, getDocumentCreator(entity));
    }

    @Override
    public Document save(Document document) {
        ExceptionUtils.check(document == null, "Не передан объект документа для сохранения");

        DocumentEntity entity;
        if (document.getId() != null) {
            entity = documentRepository.findOne(document.getId());
        } else {
            entity = new DocumentEntity();
        }

        Long creatorId = null;
        if (document.getCreator() != null) {
            if (document.getCreator() instanceof User) {
                creatorId = document.getCreator().getId();
            }
        }

        entity.setContent(document.getContent());
        entity.setName(document.getName());
        entity.setShortName(document.getShortName());
        entity.setActive(document.isActive());
        entity.setCode(document.getCode());
        entity.setCreateDate(document.getCreateDate());
        entity.setCreatorId(creatorId);
        entity.setDocumentClassId(document.getDocumentClassId());
        entity.setExpiredDate(document.getExpiredDate());
        //entity.setFolder(null); // TODO;
        entity.setHashCode(document.getHashCode());
        entity.setHashCodeForSignature(document.getHashCodeForSignature());
        entity.setCanUnsignDocument(document.isCanUnsignDocument());
        entity.setNeedSignByEDS(document.isNeedSignByEDS());
        entity.setPdfExportArguments(document.getPdfExportArguments());

        entity = documentRepository.save(entity);
        entity.getParticipants().addAll(saveParticipants(document.getParticipants(), null, entity));

        if (document.getParameters() != null) {
            for (DocumentParameter documentParameter : document.getParameters()) {
                DocumentParameterEntity documentParameterEntity;
                if (documentParameter.getId() == null) {
                    documentParameterEntity = new DocumentParameterEntity();
                } else {
                    documentParameterEntity = documentParameterRepository.getOne(documentParameter.getId());
                }
                documentParameterEntity.setName(documentParameter.getName());
                documentParameterEntity.setValue(documentParameter.getValue());
                documentParameterEntity.setDocument(entity);
                documentParameterRepository.save(documentParameterEntity);
            }
        }


        return DocumentEntity.toDomainSafe(entity, true, getDocumentCreator(entity));
    }

    @Override
    public DocumentParameter saveDocumentParameter(Long documentId, DocumentParameter documentParameter) {
        DocumentParameterEntity documentParameterEntity = new DocumentParameterEntity();
        documentParameterEntity.setDocument(documentRepository.getOne(documentId));
        documentParameterEntity.setName(documentParameter.getName());
        documentParameterEntity.setValue(documentParameter.getValue());
        documentParameterEntity = documentParameterRepository.save(documentParameterEntity);
        return DocumentParameterEntity.toDomainSafe(documentParameterEntity);
    }

    // Сохранить участников документа
    private List<DocumentParticipantEntity> saveParticipants(List<DocumentParticipant> participants, DocumentParticipantEntity parentDocumentParticipantEntity, DocumentEntity documentEntity) {
        List<DocumentParticipantEntity> result = new ArrayList<>();
        if (participants != null) {
            for (DocumentParticipant participant : participants) {
                DocumentParticipantEntity participantEntity = saveParticipant(participant, parentDocumentParticipantEntity, documentEntity);
                result.add(participantEntity);
                if (participant.getChildren() != null) {
                    for (DocumentParticipant child : participant.getChildren()) {
                        child.setParent(participant);
                    }
                    List<DocumentParticipantEntity> children = saveParticipants(participant.getChildren(), participantEntity, documentEntity);
                    participantEntity.setChildren(children);
                }
            }
        }
        return result;
    }

    private DocumentParticipantEntity saveParticipant(DocumentParticipant documentParticipant, DocumentParticipantEntity parentDocumentParticipantEntity, DocumentEntity documentEntity) {
        DocumentParticipantEntity entity;
        if (documentParticipant.getId() != null) {
            entity = documentParticipantRepository.getOne(documentParticipant.getId());
        } else {
            entity = new DocumentParticipantEntity();
        }
        entity.setSignature(documentParticipant.getSignature());
        entity.setSignDate(documentParticipant.getSignDate());
        entity.setIsSigned(documentParticipant.isSigned());

        /*DocumentParticipantEntity parent = null;
        if (documentParticipant.getParent() != null && documentParticipant.getParent().getId() != null) {
            parent = documentParticipantRepository.getOne(documentParticipant.getParent().getId());
        }*/
        entity.setParent(parentDocumentParticipantEntity);
        entity.setParticipantTypeName(documentParticipant.getParticipantTypeName());
        entity.setParticipantTemplateTypeName(documentParticipant.getParticipantTemplateTypeName());
        entity.setIsNeedSignDocument(documentParticipant.isNeedSignDocument());
        entity.setSourceParticipantId(documentParticipant.getSourceParticipantId());
        if (parentDocumentParticipantEntity == null) {
            entity.setDocument(documentEntity);
        }

        entity = documentParticipantRepository.save(entity);
        return entity;

    }

    @Override
    public List<Document> filter(Long documentClassId, Date createDateStart, Date createDateEnd, String name, Map<Long, List<String>> participantsFilters, String content) {
        List<DocumentEntity> documentEntities = documentDao.filter(documentClassId, createDateStart, createDateEnd, name, participantsFilters, content);
        return DocumentEntity.toDomainList(documentEntities, true, getDocumentCreatorMap(documentEntities));
    }

    @Override
    public List<Document> findNotSignedDocuments(List<String> participantTypes, Long sourceParticipantId) {
        List<DocumentEntity> documentEntities = documentRepository.findNotSignedDocuments(sourceParticipantId, participantTypes);
        return DocumentEntity.toDomainList(documentEntities, true, getDocumentCreatorMap(documentEntities));
    }

    @Override
    public List<Document> findByParameterAndParticipant(String parameterName, String participantTypeName, Long participantId) {
        List<DocumentEntity> documentEntities = documentDao.findByParameterAndParticipant(parameterName, participantTypeName, participantId);
        return DocumentEntity.toDomainList(documentEntities, true, getDocumentCreatorMap(documentEntities));
    }

    @Override
    public Document getByParticipantId(Long participantId) {
        DocumentParticipantEntity entity = documentParticipantRepository.findOne(participantId);
        DocumentEntity documentEntity = null;
        if (entity != null) {
            documentEntity = entity.getDocument();
        }
        return DocumentEntity.toDomainSafe(documentEntity, true, getDocumentCreator(documentEntity));
    }

    @Override
    public int countByCodePrefix(String codePrefix) {
        return documentDao.countByCodePrefix(codePrefix);
    }

    private DocumentCreator getDocumentCreator(DocumentEntity documentEntity) {
        DocumentCreator result = null;
        if (documentEntity != null && documentEntity.getCreatorId() != null && documentEntity.getCreatorId() > 0) {
            result = userDataService.getByIdMinData(documentEntity.getCreatorId());
        } else if (documentEntity != null) {
            result = systemAccountService.getById(SystemAccountEntity.BLAGOSFERA_ID);
        }
        return result;
    }

    private Map<Long, DocumentCreator> getDocumentCreatorMap(List<DocumentEntity> documentEntities) {
        Map<Long, DocumentCreator> documentCreatorMap = null;
        if (documentEntities != null) {
            documentCreatorMap = new HashMap<>();
            for (DocumentEntity entity : documentEntities) {
                documentCreatorMap.put(entity.getId(), getDocumentCreator(entity));
            }
        }
        return documentCreatorMap;
    }
}
