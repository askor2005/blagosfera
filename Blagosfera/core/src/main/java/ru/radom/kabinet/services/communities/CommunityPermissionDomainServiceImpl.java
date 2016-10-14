package ru.radom.kabinet.services.communities;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityPermission;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;
import ru.radom.kabinet.dao.communities.CommunityPermissionDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.model.communities.CommunityPermissionEntity;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by vgusev on 20.03.2016.
 */
@Service
@Transactional
public class CommunityPermissionDomainServiceImpl implements CommunityPermissionDomainService {

    @Autowired
    private CommunityPermissionDao communityPermissionDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    @Autowired
    private CommunityMemberDao communityMemberDao;

    @Override
    public CommunityPermission getById(Long id, boolean withCommunities, boolean withAssociationForms) {
        CommunityPermission result = null;
        CommunityPermissionEntity communityPermissionEntity = communityPermissionDao.getById(id);
        if (communityPermissionEntity != null) {
            result = communityPermissionEntity.toDomain(withCommunities, withAssociationForms);
        }
        return result;
    }

    @Override
    public void delete(Long id) {
        communityPermissionDao.delete(id);
    }

    @Override
    public void save(CommunityPermission communityPermission) {
        ExceptionUtils.check(StringUtils.isBlank(communityPermission.getTitle()), "Не передано название роли");

        CommunityPermissionEntity communityPermissionEntity;
        if (communityPermission.getId() == null) {
            communityPermissionEntity = new CommunityPermissionEntity();
        } else {
            communityPermissionEntity = communityPermissionDao.getById(communityPermission.getId());
        }

        communityPermissionEntity.setTitle(communityPermission.getTitle());
        communityPermissionEntity.setName(communityPermission.getName());
        communityPermissionEntity.setPosition(communityPermission.getPosition());
        communityPermissionEntity.setDescription(communityPermission.getDescription());
        communityPermissionEntity.setSecurityRole(communityPermission.isSecurityRole());

        if (communityPermissionEntity.getCommunityAssociationForms() != null) {
            communityPermissionEntity.getCommunityAssociationForms().clear();
        }
        if (communityPermissionEntity.getCommunities() != null) {
            communityPermissionEntity.getCommunities().clear();
        }
        if (communityPermission.isSecurityRole()) {
            if (communityPermission.getCommunities() != null) {
                List<Long> communityIds = new ArrayList<>();
                for (Community community : communityPermission.getCommunities()) {
                    ExceptionUtils.check(community.getId() == null, "Не передан ИД объединения");
                    communityIds.add(community.getId());
                }
                communityPermissionEntity.setCommunities(
                        Sets.newHashSet(communityDao.getByIds(communityIds))
                );
            }
        } else {
            if (communityPermission.getAssociationForms() != null) {
                List<Long> associationFormIds = new ArrayList<>();
                for (ListEditorItem associationForm : communityPermission.getAssociationForms()) {
                    ExceptionUtils.check(associationForm.getId() == null, "Не передан ИД формы объединения");
                    associationFormIds.add(associationForm.getId());
                }
                communityPermissionEntity.setCommunityAssociationForms(
                        Sets.newHashSet(rameraListEditorItemDAO.getByIds(associationFormIds))
                );
            }
        }
        communityPermissionDao.saveOrUpdate(communityPermissionEntity);
    }

    @Override
    public Set<String> getPermissions(CommunityMember member) {
        Set<String> result = null;
        if (member != null) {
            result = communityPermissionDao.getPermissions(communityMemberDao.loadById(member.getId()));
        }
        return result;
    }

    @Override
    public List<CommunityPermission> getByCommunityId(Long communityId) {
        return CommunityPermissionEntity.toListDomain(communityPermissionDao.getByCommunityId(communityId), false, false);
    }
}
