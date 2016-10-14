package ru.radom.kabinet.services.communities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityDocumentRequestRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentRepository;
import ru.askor.blagosfera.data.jpa.specifications.community.CommunityDocumentRequestSpecifications;
import ru.askor.blagosfera.domain.community.CommunityDocumentRequest;
import ru.askor.blagosfera.domain.community.CommunityDocumentRequestsPage;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.document.Document;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.model.communities.CommunityDocumentRequestEntity;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.List;

/**
 *
 * Created by vgusev on 22.07.2016.
 */
@Service
@Transactional
public class CommunityDocumentRequestServiceImpl implements CommunityDocumentRequestService {

    @Autowired
    private CommunityDocumentRequestRepository communityDocumentRequestRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Override
    public CommunityDocumentRequest getById(Long id) {
        CommunityDocumentRequestEntity entity = communityDocumentRequestRepository.findOne(id);
        return CommunityDocumentRequestEntity.toSafeDomain(entity);
    }

    @Override
    public CommunityDocumentRequest save(CommunityDocumentRequest communityDocumentRequest) {
        ExceptionUtils.check(communityDocumentRequest == null, "Не передан объект для сохранения");
        ExceptionUtils.check(communityDocumentRequest.getCommunity() == null || communityDocumentRequest.getCommunity().getId() == null, "Не установлено объединение");
        ExceptionUtils.check(communityDocumentRequest.getUser() == null || communityDocumentRequest.getUser().getId() == null, "Не установлен пользователь");
        //ExceptionUtils.check(communityDocumentRequest.getDocuments() == null || communityDocumentRequest.getDocuments().isEmpty(), "Не установлен список документов");
        CommunityDocumentRequestEntity entity;
        if (communityDocumentRequest.getId() == null) {
            entity = new CommunityDocumentRequestEntity();
        } else {
            entity = communityDocumentRequestRepository.getOne(communityDocumentRequest.getId());
        }
        entity.setCommunity(communityRepository.getOne(communityDocumentRequest.getCommunity().getId()));
        entity.setUser(userRepository.getOne(communityDocumentRequest.getUser().getId()));
        entity.getDocuments().clear();
        for (Document document : communityDocumentRequest.getDocuments()) {
            DocumentEntity documentEntity = documentRepository.getOne(document.getId());
            entity.getDocuments().add(documentEntity);
        }
        entity = communityDocumentRequestRepository.save(entity);

        return entity.toDomain();
    }

    @Override
    public void delete(Long id) {
        communityDocumentRequestRepository.delete(id);
    }

    @Override
    public void deleteRequestAndMember(Long id) {
        CommunityDocumentRequestEntity communityDocumentRequestEntity = communityDocumentRequestRepository.getOne(id);
        if (communityDocumentRequestEntity != null) {
            CommunityMember communityMember = communityMemberDomainService.getByCommunityIdAndUserId(
                    communityDocumentRequestEntity.getCommunity().getId(),
                    communityDocumentRequestEntity.getUser().getId()
            );
            if (communityMember != null) {
                communityMemberDomainService.delete(communityMember.getId());
            }
            communityDocumentRequestRepository.delete(communityDocumentRequestEntity.getId());
        }
    }

    @Override
    public void deleteRequestAndMember(Long communityId, Long userId) {
        CommunityDocumentRequestEntity communityDocumentRequestEntity = communityDocumentRequestRepository.findByCommunity_IdAndUser_Id(communityId, userId);
        CommunityMember communityMember = communityMemberDomainService.getByCommunityIdAndUserId(
                communityId,
                userId
        );
        if (communityMember != null) {
            communityMemberDomainService.delete(communityMember.getId());
        }
        if (communityDocumentRequestEntity != null) {
            communityDocumentRequestRepository.delete(communityDocumentRequestEntity.getId());
        }
    }

    @Override
    public List<CommunityDocumentRequest> getByUserId(Long userId) {
        List<CommunityDocumentRequestEntity> entities = communityDocumentRequestRepository.findByUser_Id(userId);
        return CommunityDocumentRequestEntity.toDomainList(entities);
    }

    @Override
    public List<CommunityDocumentRequest> getByCommunityId(Long communityId) {
        List<CommunityDocumentRequestEntity> entities = communityDocumentRequestRepository.findByCommunity_Id(communityId);
        return CommunityDocumentRequestEntity.toDomainList(entities);
    }

    @Override
    public CommunityDocumentRequest getByCommunityAndUser(Long communityId, Long userId) {
        CommunityDocumentRequestEntity entity = communityDocumentRequestRepository.findByCommunity_IdAndUser_Id(communityId, userId);
        return CommunityDocumentRequestEntity.toSafeDomain(entity);
    }

    @Override
    public CommunityDocumentRequestsPage getByUserIdPage(Long userId, int page, int perPage) {
        Pageable pageable = new PageRequest(page, perPage);
        Page<CommunityDocumentRequestEntity> communityDocumentRequests = communityDocumentRequestRepository.findAll(CommunityDocumentRequestSpecifications.userId(userId), pageable);

        CommunityDocumentRequestsPage result = new CommunityDocumentRequestsPage();
        result.setCommunityDocumentRequests(CommunityDocumentRequestEntity.toDomainList(communityDocumentRequests.getContent()));
        result.setCount(communityDocumentRequests.getTotalElements());

        return result;
    }

}
