package ru.radom.kabinet.web.admin.dto;

import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityPermissionEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DTO для получения данных о ролях и для сохранения роли
 * Created by vgusev on 19.11.2015.
 */
public class CommunityPermissionDto {

    private Long id;
    private String title;
    private String name; // Поле не обновляется!!!
    private String description;
    private List<Long> associationForms;
    private List<Long> communities;
    private boolean securityRole;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getAssociationForms() {
        return associationForms;
    }

    public void setAssociationForms(List<Long> associationForms) {
        this.associationForms = associationForms;
    }

    public List<Long> getCommunities() {
        return communities;
    }

    public void setCommunities(List<Long> communities) {
        this.communities = communities;
    }

    public boolean isSecurityRole() {
        return securityRole;
    }

    public void setSecurityRole(boolean securityRole) {
        this.securityRole = securityRole;
    }

    public static List<CommunityPermissionDto> fromEntities(List<CommunityPermissionEntity> communityPermissions) {
        List<CommunityPermissionDto> result;
        if (communityPermissions != null && communityPermissions.size() > 0) {
            result = new ArrayList<>();
            for (CommunityPermissionEntity communityPermission : communityPermissions) {
                result.add(fromEntity(communityPermission));
            }
        } else {
            result = Collections.emptyList();
        }
        return result;
    }

    public static CommunityPermissionDto fromEntity(CommunityPermissionEntity communityPermission) {
        CommunityPermissionDto result = new CommunityPermissionDto();
        result.setId(communityPermission.getId());
        result.setTitle(communityPermission.getTitle());
        result.setName(communityPermission.getName());
        result.setDescription(communityPermission.getDescription());
        if (communityPermission.getCommunityAssociationForms() != null) {
            List<Long> associationForms = new ArrayList<>();
            for (RameraListEditorItem item : communityPermission.getCommunityAssociationForms()) {
                associationForms.add(item.getId());
            }
            result.setAssociationForms(associationForms);
        }
        if (communityPermission.isSecurityRole() && communityPermission.getCommunities() != null) {
            List<Long> communities = new ArrayList<>();
            for (CommunityEntity community : communityPermission.getCommunities()) {
                communities.add(community.getId());
            }
            result.setCommunities(communities);
        }
        result.setSecurityRole(communityPermission.isSecurityRole());
        return result;
    }
}
