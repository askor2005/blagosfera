package ru.radom.kabinet.services.communities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityMemberRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityPostRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityPostRequestRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.domain.community.CommunityPostRequest;
import ru.radom.kabinet.model.communities.postrequest.CommunityPostRequestEntity;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.List;

/**
 *
 * Created by vgusev on 21.03.2016.
 */
@Service
@Transactional
public class CommunityPostRequestDomainServiceImpl implements CommunityPostRequestDomainService {

    @Autowired
    private CommunityPostRequestRepository communityPostRequestRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityMemberRepository communityMemberRepository;

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Override
    public CommunityPostRequest getById(Long id) {
        return CommunityPostRequestEntity.toDomainSafe(communityPostRequestRepository.findOne(id));
    }

    @Override
    public List<CommunityPostRequest> getReceivedCommunityPostRequests(Long communityMemberId) {
        return CommunityPostRequestEntity.toDomainList(communityPostRequestRepository.findByReceiver_Id(communityMemberId));
    }

    @Override
    public List<CommunityPostRequest> getSendedCommunityPostRequests(Long communityMemberId) {
        return CommunityPostRequestEntity.toDomainList(communityPostRequestRepository.findBySender_Id(communityMemberId));
    }

    @Override
    public CommunityPostRequest save(CommunityPostRequest communityPostRequest) {
        CommunityPostRequestEntity entity;
        if (communityPostRequest.getId() == null) {
            entity = new CommunityPostRequestEntity();
        } else {
            entity = communityPostRequestRepository.findOne(communityPostRequest.getId());
        }
        ExceptionUtils.check(
                communityPostRequest.getCommunity() == null || communityPostRequest.getCommunity().getId() == null,
                "Не установлено объединение запроса на должность"
        );
        ExceptionUtils.check(
                communityPostRequest.getSender() == null || communityPostRequest.getSender().getId() == null,
                "Не установлен участник который отрпавил запрос на должность"
        );
        ExceptionUtils.check(
                communityPostRequest.getReceiver() == null || communityPostRequest.getReceiver().getId() == null,
                "Не установлен участник которому отправили запрос на должность"
        );
        ExceptionUtils.check(
                communityPostRequest.getCommunityPost() == null || communityPostRequest.getCommunityPost().getId() == null,
                "Не установлена должность"
        );
        ExceptionUtils.check(
                communityPostRequest.getStatus() == null,
                "Не установлен статус запроса на должность"
        );

        entity.setCommunity(communityRepository.getOne(communityPostRequest.getCommunity().getId()));
        entity.setReceiver(communityMemberRepository.getOne(communityPostRequest.getReceiver().getId()));
        entity.setSender(communityMemberRepository.getOne(communityPostRequest.getSender().getId()));
        entity.setCommunityPost(communityPostRepository.getOne(communityPostRequest.getCommunityPost().getId()));
        entity.setStatus(communityPostRequest.getStatus());
        entity = communityPostRequestRepository.save(entity);
        return CommunityPostRequestEntity.toDomainSafe(entity);
    }

    @Override
    public CommunityPostRequest delete(Long id) {
        CommunityPostRequest result = CommunityPostRequestEntity.toDomainSafe(communityPostRequestRepository.findOne(id));
        communityPostRequestRepository.delete(id);
        return result;
    }
}
