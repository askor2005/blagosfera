package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunityPermission;
import ru.askor.blagosfera.domain.community.CommunityPost;
import ru.askor.blagosfera.domain.document.templatesettings.dto.DocumentTemplateSettingDto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 30.03.2016.
 */
@Data
public class CommunityPostDto {

    private Long id;

    private String name;

    private int position;

    private int vacanciesCount;

    private boolean isCeo;

    private CommunityPostPageSchemaUnitDto schemaUnit;

    private List<CommunityPermissionDto> permissions;

    private String appointBehavior;

    private List<DocumentTemplateSettingDto> documentTemplateSettings;

    /*private Long templateId;

    private String templateName;*/

    public CommunityPostDto(CommunityPost communityPost) {
        setId(communityPost.getId());
        setName(communityPost.getName());
        setPosition(communityPost.getPosition());
        setVacanciesCount(communityPost.getVacanciesCount());
        setCeo(communityPost.isCeo());
        if (communityPost.getPermissions() != null) {
            this.permissions = new ArrayList<>();
            for (CommunityPermission communityPermission : communityPost.getPermissions()) {
                this.permissions.add(new CommunityPermissionDto(communityPermission));
            }
        }
        if (communityPost.getSchemaUnit() != null) {
            setSchemaUnit(new CommunityPostPageSchemaUnitDto(communityPost.getSchemaUnit()));
        }
        setAppointBehavior(communityPost.getAppointBehavior());
        setDocumentTemplateSettings(DocumentTemplateSettingDto.toDtoList(communityPost.getDocumentTemplateSettings()));
        /*if (communityPost.getAppointBehavior() == null && communityPost.getDocumentTemplate() != null) {
            setAppointBehavior("documentBehavior");
            setTemplateId(communityPost.getDocumentTemplate().getId());
            setTemplateName(communityPost.getDocumentTemplate().getName());
        } else {
            setAppointBehavior("defaultBehavior");
        }*/
    }

    public static List<CommunityPostDto> toListDto(List<CommunityPost> communityPosts) {
        List<CommunityPostDto> result = null;
        if (communityPosts != null) {
            result = new ArrayList<>();
            for (CommunityPost communityPost : communityPosts) {
                result.add(new CommunityPostDto(communityPost));
            }
        }
        return result;
    }
}
