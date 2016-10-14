package ru.radom.kabinet.services.communities.organizationmember.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.OrganizationCommunityMemberRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentRepository;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.community.OrganizationCommunityMember;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.OrganizationCommunityMemberEntity;
import ru.radom.kabinet.model.communities.OrganizationCommunityMemberParameter;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.organizationmember.OrganizationMemberDomainService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Created by vgusev on 17.03.2016.
 */
@Service
@Transactional
public class OrganizationMemberDomainServiceImpl implements OrganizationMemberDomainService {

    @Autowired
    private OrganizationCommunityMemberRepository organizationCommunityMemberRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private DocumentRepository documentRepository;

    private Community getCommunity(OrganizationCommunityMemberEntity entity) {
        Community result = null;
        if (entity != null && entity.getCommunity() != null && entity.getCommunity().getId() != null) {
            result = communityDomainService.getByIdMediumData(entity.getCommunity().getId());
        }
        return result;
    }

    private Community getOrganization(OrganizationCommunityMemberEntity entity) {
        Community result = null;
        if (entity != null && entity.getOrganization() != null && entity.getOrganization().getId() != null) {
            result = communityDomainService.getByIdMediumData(entity.getOrganization().getId());
        }
        return result;
    }

    private OrganizationCommunityMember toDomain(OrganizationCommunityMemberEntity entity) {
        return OrganizationCommunityMemberEntity.toDomainSafe(entity, getCommunity(entity), getOrganization(entity));
    }

    private List<OrganizationCommunityMember> toDomainList(List<OrganizationCommunityMemberEntity> entities) {
        List<OrganizationCommunityMember> result = null;
        if (entities != null) {
            result = new ArrayList<>();
            for (OrganizationCommunityMemberEntity entity : entities) {
                result.add(toDomain(entity));
            }
        }
        return result;
    }


    @Override
    public OrganizationCommunityMember getById(Long id) {
        return toDomain(organizationCommunityMemberRepository.findOne(id));
    }

    @Override
    public OrganizationCommunityMember save(OrganizationCommunityMember member) {
        ExceptionUtils.check(
                member.getCommunity() == null || member.getCommunity().getId() == null,
                "Не передано объединение"
        );
        ExceptionUtils.check(
                member.getOrganization() == null || member.getOrganization().getId() == null,
                "Не передана организация"
        );
        ExceptionUtils.check(member.getStatus() == null, "Не переданн статус участника юр лица");

        CommunityEntity communityEntity = communityRepository.getOne(member.getCommunity().getId());
        CommunityEntity organizationEntity = communityRepository.getOne(member.getOrganization().getId());

        OrganizationCommunityMemberEntity organizationCommunityMemberEntity;
        if (member.getId() == null) {
            organizationCommunityMemberEntity = new OrganizationCommunityMemberEntity();
        } else {
            organizationCommunityMemberEntity = organizationCommunityMemberRepository.getOne(member.getId());
        }

        DocumentEntity documentEntity = null;
        if (member.getDocument() != null && member.getDocument().getId() != null) {
            documentEntity = documentRepository.getOne(member.getDocument().getId());
        }

        List<OrganizationCommunityMemberParameter> organizationCommunityMemberParameters = null;
        //if (member.get)

        organizationCommunityMemberEntity.setCommunity(communityEntity);
        organizationCommunityMemberEntity.setOrganization(organizationEntity);
        organizationCommunityMemberEntity.setStatus(member.getStatus());
        organizationCommunityMemberEntity.setDocument(documentEntity);
        organizationCommunityMemberEntity.setOrganizationCommunityMemberParameters(organizationCommunityMemberParameters);


        organizationCommunityMemberEntity = organizationCommunityMemberRepository.save(organizationCommunityMemberEntity);

        return toDomain(organizationCommunityMemberEntity);
    }

    @Override
    public OrganizationCommunityMember getByCommunityIdAndOrganizationId(Long communityId, Long organizationId) {
        return toDomain(
                organizationCommunityMemberRepository.findByCommunity_IdAndOrganization_Id(communityId, organizationId)
        );
    }

    @Override
    public List<OrganizationCommunityMember> getByCommunityIdAndOrganizationIds(Long communityId, List<Long> organizationIds) {
        return toDomainList(
                organizationCommunityMemberRepository.findByCommunity_IdAndOrganization_IdIn(communityId, organizationIds)
        );
    }

    @Override
    public List<OrganizationCommunityMember> getByCommunityIdAndStatus(Long communityId, CommunityMemberStatus status) {
        return toDomainList(organizationCommunityMemberRepository.findByCommunity_IdAndStatus(communityId, status));
    }

    @Override
    public void delete(Long id) {
        organizationCommunityMemberRepository.delete(id);
    }

    @Override
    public List<OrganizationCommunityMember> find(Long communityId, CommunityMemberStatus status, String communityName, int page, int perPage) {
        return find(communityId, Collections.singletonList(status), communityName, page, perPage);
    }

    @Override
    public List<OrganizationCommunityMember> find(Long communityId, List<CommunityMemberStatus> statuses, String communityName, int page, int perPage) {
        communityName = communityName == null ? "" : communityName;
        Pageable pageable = new PageRequest(page, perPage);
        return toDomainList(
                organizationCommunityMemberRepository.findByCommunity_IdAndStatusInAndOrganization_NameLikeIgnoreCase(
                        communityId, statuses, "%" + communityName + "%", pageable
                )
        );
    }

    @Override
    public int getMembersCount(Long communityId, List<CommunityMemberStatus> memberStatuses) {
        return organizationCommunityMemberRepository.getCountMembers(communityId, memberStatuses);
    }
}
