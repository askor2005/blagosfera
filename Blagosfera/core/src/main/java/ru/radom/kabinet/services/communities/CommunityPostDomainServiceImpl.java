package ru.radom.kabinet.services.communities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityMemberRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityPermissionRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityPostRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentTemplateRepository;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentTemplateSettingRepository;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityPermission;
import ru.askor.blagosfera.domain.community.CommunityPost;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSetting;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.communities.CommunityPostDao;
import ru.radom.kabinet.dao.communities.schema.CommunitySchemaUnitDao;

import ru.radom.kabinet.document.model.DocumentTemplateEntity;
import ru.radom.kabinet.dto.community.CommunityUserPost;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.communities.CommunityPermissionEntity;
import ru.radom.kabinet.model.communities.CommunityPostEntity;
import ru.radom.kabinet.model.document.DocumentTemplateSettingEntity;
import ru.radom.kabinet.services.document.DocumentTemplateSettingService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 18.03.2016.
 */
@Service
@Transactional
public class CommunityPostDomainServiceImpl implements CommunityPostDomainService {

    @Autowired
    private CommunityPostDao communityPostDao;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunitySchemaUnitDao communitySchemaUnitDao;

    @Autowired
    private CommunityMemberRepository communityMemberRepository;

    @Autowired
    private CommunityPermissionRepository communityPermissionRepository;

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private DocumentTemplateRepository documentTemplateRepository;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private DocumentTemplateSettingService documentTemplateSettingService;

    @Autowired
    private DocumentTemplateSettingRepository documentTemplateSettingRepository;

    @Override
    public CommunityPost getCeo(Community community) {
        CommunityEntity communityEntity = communityRepository.findOne(community.getId());
        return CommunityPostEntity.toDomainSafe(communityPostDao.getCeo(communityEntity), false, false, false, false);
    }

    @Override
    public CommunityPost getByName(Community community, String name) {
        CommunityEntity communityEntity = communityRepository.findOne(community.getId());
        return CommunityPostEntity.toDomainSafe(communityPostDao.getByName(communityEntity, name), false, false, false, false);
    }

    @Override
    public CommunityPost getByIdFullData(Long id) {
        return getById(id, true, true, true, true);
    }

    @Override
    public CommunityPost getById(Long id, boolean withCommunity, boolean withMembers, boolean withPermissions, boolean withSchema) {
        return CommunityPostEntity.toDomainSafe(communityPostRepository.findOne(id), withCommunity, withMembers, withPermissions, withSchema);
    }

    @Override
    public boolean checkPost(CommunityPost post) {
        return communityPostDao.checkName(post);
    }

    @Override
    public boolean checkPost(Community community, String postName) {
        CommunityEntity communityEntity = communityRepository.findOne(community.getId());
        return communityPostDao.checkName(communityEntity, postName);
    }

    @Override
    public CommunityPost save(CommunityPost post) {
        CommunityPostEntity communityPostEntity;
        if (post.getId() == null) {
            communityPostEntity = new CommunityPostEntity();
        } else {
            communityPostEntity = communityPostDao.getById(post.getId());
        }
        communityPostEntity.setPosition(post.getPosition());
        communityPostEntity.setName(post.getName());
        communityPostEntity.setAppointBehavior(post.getAppointBehavior());
        communityPostEntity.setCeo(post.isCeo());
        communityPostEntity.setCommunity(communityRepository.findOne(post.getCommunity().getId()));
        communityPostEntity.setVacanciesCount(post.getVacanciesCount());
        //communityPostEntity.setMnemo(post.getMnemo());
        if (post.getSchemaUnit() != null) {
            communityPostEntity.setSchemaUnit(communitySchemaUnitDao.loadById(post.getSchemaUnit().getId()));
        }
        if (post.getMembers() != null) {
            List<CommunityMemberEntity> communityMemberEntities = new ArrayList<>();
            for (CommunityMember communityMember : post.getMembers()) {
                communityMemberEntities.add(communityMemberRepository.getOne(communityMember.getId()));
            }
            communityPostEntity.setMembers(communityMemberEntities);
        }
        if (post.getPermissions() != null) {
            List<CommunityPermissionEntity> communityPermissionEntities = new ArrayList<>();
            for (CommunityPermission communityPermission : post.getPermissions()) {
                communityPermissionEntities.add(communityPermissionRepository.getOne(communityPermission.getId()));
            }
            communityPostEntity.setPermissions(communityPermissionEntities);
        }
        /*communityPostEntity.setDocumentTemplate(null);
        if (post.getDocumentTemplate() != null && post.getDocumentTemplate().getId() != null) {
            DocumentTemplateEntity documentTemplateEntity = documentTemplateRepository.getOne(post.getDocumentTemplate().getId());
            if (documentTemplateEntity != null) {
                communityPostEntity.setDocumentTemplate(documentTemplateEntity);
            }
        }*/
        communityPostEntity.getDocumentTemplateSettings().clear();
        if (post.getDocumentTemplateSettings() != null) {
            List<DocumentTemplateSettingEntity> documentTemplateSettingEntities = new ArrayList<>();
            for (DocumentTemplateSetting documentTemplateSetting : post.getDocumentTemplateSettings()) {
                documentTemplateSetting = documentTemplateSettingService.save(documentTemplateSetting);
                DocumentTemplateSettingEntity documentTemplateSettingEntity = documentTemplateSettingRepository.getOne(documentTemplateSetting.getId());
                documentTemplateSettingEntities.add(documentTemplateSettingEntity);
            }
            communityPostEntity.getDocumentTemplateSettings().addAll(documentTemplateSettingEntities);
        }

        communityPostEntity = communityPostRepository.save(communityPostEntity);
        return CommunityPostEntity.toDomainSafe(communityPostEntity, true, true, true, true);
    }

    @Override
    public void delete(Long id) {
        communityPostRepository.delete(id);
    }

    @Override
    public void delete(CommunityPost post) {
        delete(post.getId());
    }

    @Override
    public List<CommunityPost> getByCommunityId(Long communityId, boolean withCommunity, boolean withMembers, boolean withPermissions, boolean withSchema) {
        return CommunityPostEntity.toListDomain(communityPostDao.getByCommunityId(communityId), withCommunity, withMembers, withPermissions, withSchema);
    }

    @Override
    public List<CommunityUserPost> getCommunityUserPosts(Long communityId, int start, int limit) {
        List<CommunityUserPost> communityUserPosts = communityPostDao.getCommunityPosts(communityId, limit, start);
        if (communityUserPosts != null) {
            for (CommunityUserPost communityUserPost : communityUserPosts) {
                User user = userDataService.getByIdMinData(communityUserPost.getUserId());
                communityUserPost.setUserName(user.getName());
            }
        }
        return communityUserPosts;
    }

    @Override
    public int getCommunityUserPostsCount(Long communityId) {
        return communityPostDao.getCommunityPostsCount(communityId);
    }
}
