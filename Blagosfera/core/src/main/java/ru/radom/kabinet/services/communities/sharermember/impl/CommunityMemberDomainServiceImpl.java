package ru.radom.kabinet.services.communities.sharermember.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.security.RosterService;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityMemberRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityPostRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.domain.community.*;
import ru.radom.kabinet.collections.CommunityMemberStatusList;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.communities.CommunityPostEntity;
import ru.radom.kabinet.services.communities.CommunityPostRequestDomainService;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 18.03.2016.
 */
@Service
@Transactional
public class CommunityMemberDomainServiceImpl implements CommunityMemberDomainService {
    @Autowired
    private RosterService rosterService;

    private static final String GET_BY_ID_FULL_CACHE = "getByIdFullMember";

    private static final String GET_BY_COMMUNITY_ID_AND_USER_ID_FULL_CACHE = "getByCommunityIdAndUserIdFullMember";

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityMemberRepository communityMemberRepository;

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private CommunityMemberDao communityMemberDao;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityPostRequestDomainService communityPostRequestDomainService;

    private CommunityMember getById(Long id, boolean withInviter, boolean withUser, boolean withCommunity, boolean withPosts) {
        return CommunityMemberEntity.toDomainSafe(communityMemberRepository.findOne(id), withInviter, withUser, withCommunity, withPosts);
    }

    //@Cacheable(value = GET_BY_ID_FULL_CACHE, key = "#id")
    @Override
    public CommunityMember getByIdFullData(Long id) {
        return getById(id, true, true, true, true);
    }

    @Override
    public List<CommunityMember> getByIds(List<Long> ids, boolean withInviter, boolean withUser, boolean withCommunity, boolean withPosts) {
        return CommunityMemberEntity.toDomainList(communityMemberRepository.findByIdIn(ids));
    }

    @Override
    public CommunityMember save(CommunityMember member) {
        CommunityMemberEntity entity;
        if (member.getId() == null) {
            entity = new CommunityMemberEntity();
        } else {
            entity = communityMemberRepository.getById(member.getId());
        }
        ExceptionUtils.check(entity == null, "Участник с ИД " + member.getId() + " не найден");
        ExceptionUtils.check(
                member.getId() == null && (member.getCommunity() == null || member.getCommunity().getId() == null),
                "Не установлено объединение участника"
        );
        ExceptionUtils.check(
                member.getId() == null && (member.getUser() == null || member.getUser().getId() == null),
                "Не установлен пользователь участника объединения"
        );
        ExceptionUtils.check(
                member.getStatus() == null,
                "Не статус участника объединения"
        );
        if (member.getId() == null) {
            if (member.getInviter() != null && member.getInviter().getId() != null) {
                entity.setInviter(userRepository.getOne(member.getInviter().getId()));
            }
            entity.setCommunity(communityRepository.getOne(member.getCommunity().getId()));
            entity.setUser(userRepository.getOne(member.getUser().getId()));
            entity.setCreator(member.isCreator());
            entity.setRequestDate(member.getRequestDate());
        }
        ExceptionUtils.check(
                member.getPosts() == null && entity.getPosts() != null && !entity.getPosts().isEmpty(),
                "Не переданы должности участника"
        );
        if (member.getPosts() != null) {
            List<Long> postIds = member.getPosts().stream().filter(communityPost -> communityPost.getId() != null).map(CommunityPost::getId).collect(Collectors.toList());
            List<CommunityPostEntity> postEntities = communityPostRepository.findByIdIn(postIds);
            entity.setPosts(postEntities);
        }
        // TODO Если новый статус - MEMBER то отправить событие
        /*if (member.getStatus() != null && member.getStatus().equals(CommunityMemberStatus.MEMBER) &&
                !member.getStatus().equals(entity.getStatus())) {
            blagosferaEventPublisher.publishEvent(new CommunityMemberEvent(this, CommunityEventType.ADD_MEMBER_TO_COMMUNITY, member));
        }*/
        entity.setStatus(member.getStatus());
        entity = communityMemberRepository.save(entity);
        return CommunityMemberEntity.toDomainSafe(entity, true, true, true, true);
    }

    //@Cacheable(value = GET_BY_COMMUNITY_ID_AND_USER_ID_FULL_CACHE, key = "{#communityId, #userId}")
    @Override
    public CommunityMember getByCommunityIdAndUserId(Long communityId, Long userId) {
        return CommunityMemberEntity.toDomainSafe(communityMemberRepository.findByCommunity_IdAndUser_Id(communityId, userId),
                true, true, true, true);
    }

    @Override
    public List<CommunityMember> getByCommunityIdAndStatus(Long communityId, CommunityMemberStatus status) {
        return CommunityMemberEntity.toDomainList(communityMemberRepository.findByCommunity_IdAndStatus(communityId, status));
    }
    @Override
    public List<CommunityMember> getByCommunityIdChildren(Long communityId, CommunityMemberStatus status) {
        CommunityEntity communityEntity =  communityRepository.findOne(communityId);
        ArrayList<CommunityMemberEntity> result = new ArrayList<>();
        for (CommunityEntity child : communityEntity.getChildren()) {
            result.addAll(getChildMembersRecursive(child,status));
        }
        return CommunityMemberEntity.toDomainList(result);
    }
    private List<CommunityMemberEntity> getChildMembersRecursive(CommunityEntity communityEntity,CommunityMemberStatus status) {
     ArrayList<CommunityMemberEntity> result = new ArrayList<>(communityMemberRepository.findByCommunity_IdAndStatus(communityEntity.getId(), status));
        for (CommunityEntity child : communityEntity.getChildren()) {
            result.addAll(getChildMembersRecursive(child,status));
        }
        return result;
    }

    private void deletePostRequests(List<CommunityPostRequest> communityPostRequests) {
        if (communityPostRequests != null && !communityPostRequests.isEmpty()) {
            for (CommunityPostRequest communityPostRequest : communityPostRequests) {
                communityPostRequestDomainService.delete(communityPostRequest.getId());
            }
        }
    }

    /*@Caching(evict = {
            @CacheEvict(value = GET_BY_ID_FULL_CACHE, key = "#id")
    })*/
    @Override
    public CommunityMember delete(Long id) {
        CommunityMember result = CommunityMemberEntity.toDomainSafe(communityMemberRepository.findOne(id), false, false, false, false);
        List<CommunityPostRequest> communityPostRequests = communityPostRequestDomainService.getReceivedCommunityPostRequests(id);
        deletePostRequests(communityPostRequests);
        communityPostRequests = communityPostRequestDomainService.getSendedCommunityPostRequests(id);
        deletePostRequests(communityPostRequests);
        communityMemberRepository.delete(id);
        return result;
    }

    @Override
    public boolean exists(Long communityId, Long userId, CommunityMemberStatus status) {
        CommunityMemberEntity member = communityMemberRepository.findByCommunity_IdAndUser_IdAndStatus(communityId, userId, status);
        return member != null;
    }

    @Override
    public boolean exists(Long communityId, Long userId) {
        CommunityMemberEntity member = communityMemberRepository.findByCommunity_IdAndUser_Id(communityId, userId);
        return member != null;
    }

    @Override
    public List<CommunityMember> getList(Community community, CommunityMemberStatusList statusList, int firstResult, int maxResults, String query, List<Long> excludeUserIds) {
        return getList(community.getId(), statusList, firstResult, maxResults, query, excludeUserIds);
    }

    @Override
    public List<CommunityMember> getList(Long communityId, CommunityMemberStatusList statusList, int firstResult, int maxResults, String query, List<Long> excludeUserIds) {
        return CommunityMemberEntity.toDomainList(communityMemberDao.getList(communityRepository.getOne(communityId), statusList, firstResult, maxResults, query, excludeUserIds)).
                stream().map(communityMember -> {communityMember.setOnline(rosterService.isUserOnline(communityMember.getUser().getEmail()));
                                                return communityMember;}).collect(Collectors.toList());
    }

    @Override
    public List<CommunityMember> getList(Long communityId, CommunityMemberStatusList statusList) {
        return CommunityMemberEntity.toDomainList(communityMemberDao.getList(communityRepository.getOne(communityId), statusList, 0, Integer.MAX_VALUE, null, null));
    }

    @Override
    public List<CommunityMember> getByCommunityCreator(Long creatorId, List<CommunityMemberStatus> statusList, int page, int perPage) {
        return CommunityMemberEntity.toDomainList(communityMemberDao.getByCommynityCreator(creatorId, statusList, page, perPage));
    }

    @Override
    public int getMembersCount(Long communityId, List<CommunityMemberStatus> memberStatuses) {
        return communityMemberRepository.getCountMembers(communityId, memberStatuses);
    }
}
