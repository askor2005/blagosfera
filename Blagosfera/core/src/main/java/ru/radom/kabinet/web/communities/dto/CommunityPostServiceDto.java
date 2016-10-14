package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityPermission;
import ru.askor.blagosfera.domain.community.CommunityPost;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnit;
import ru.askor.blagosfera.domain.document.DocumentTemplate;
import ru.askor.blagosfera.domain.document.templatesettings.dto.DocumentTemplateSettingDto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 31.03.2016.
 */
@Data
public class CommunityPostServiceDto {

    private Long id;

    private String name;

    private int position;

    private int vacanciesCount;

    private Long communityId;

    private List<Long> permissionIds;

    private boolean isCeo;

    private Long schemaUnitId;

    private String appointBehavior;

    private List<DocumentTemplateSettingDto> documentTemplateSettings;

    //private Long documentTemplateId;

    public CommunityPost toDomain() {
        Community community = new Community();
        community.setId(getCommunityId());

        /*DocumentTemplate documentTemplate = null;
        if (getDocumentTemplateId() != null) {
            documentTemplate = new DocumentTemplate();
            documentTemplate.setId(getDocumentTemplateId());
        }*/

        List<CommunityPermission> communityPermissions = new ArrayList<>();
        if (getPermissionIds() != null) {
            for (Long permissionId : getPermissionIds()) {
                CommunityPermission communityPermission = new CommunityPermission();
                communityPermission.setId(permissionId);
                communityPermissions.add(communityPermission);
            }
        }

        CommunitySchemaUnit communitySchemaUnit = new CommunitySchemaUnit();
        communitySchemaUnit.setId(getSchemaUnitId());

        CommunityPost communityPost = new CommunityPost();
        communityPost.setId(getId());
        communityPost.setName(getName());
        communityPost.setPosition(getPosition());
        communityPost.setVacanciesCount(getVacanciesCount());
        communityPost.setCommunity(community);
        communityPost.setPermissions(communityPermissions);
        communityPost.setCeo(isCeo());
        communityPost.setSchemaUnit(communitySchemaUnit);
        communityPost.setAppointBehavior(getAppointBehavior());
        communityPost.setDocumentTemplateSettings(DocumentTemplateSettingDto.toDomainList(documentTemplateSettings));

        //communityPost.setDocumentTemplate(documentTemplate);
        return communityPost;
    }
}
