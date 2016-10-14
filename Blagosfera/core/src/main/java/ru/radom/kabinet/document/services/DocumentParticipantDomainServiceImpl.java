package ru.radom.kabinet.document.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentParticipantRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentRepository;
import ru.askor.blagosfera.domain.document.DocumentParticipant;
import ru.radom.kabinet.document.model.DocumentParameterEntity;
import ru.radom.kabinet.document.model.DocumentParticipantEntity;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
@Service
@Transactional
public class DocumentParticipantDomainServiceImpl implements DocumentParticipantDomainService {

    @Autowired
    private DocumentParticipantRepository documentParticipantRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public DocumentParticipant getById(Long id) {
        return DocumentParticipantEntity.toDomainSafe(documentParticipantRepository.findOne(id), true, true);
    }

    @Override
    public List<DocumentParticipant> getByDocumentId(Long documentId) {
        return null;
    }

    @Override
    public void signDocumentByParticipantId(Long participantId) {
        DocumentParticipantEntity entity = documentParticipantRepository.getOne(participantId);
        entity.setIsSigned(true);
        entity.setSignDate(new Date());
        documentParticipantRepository.save(entity);
    }

    @Override
    public void setSignatureByParticipantId(Long participantId, String base64Signature) {
        DocumentParticipantEntity entity = documentParticipantRepository.getOne(participantId);
        entity.setSignature(base64Signature);
        documentParticipantRepository.save(entity);
    }

    @Override
    public DocumentParticipant delete(Long id) {
        DocumentParticipant result = DocumentParticipantEntity.toDomainSafe(documentParticipantRepository.findOne(id), false, false);
        documentParticipantRepository.delete(id);
        return result;
    }
}
