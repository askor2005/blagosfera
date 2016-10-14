package ru.radom.kabinet.web.communities.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityAccessType;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.radom.kabinet.json.ShortDateSerializer;
import ru.radom.kabinet.json.TimeStampDateSerializer;
import ru.radom.kabinet.utils.ImagesUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 11.03.2016.
 */
@Data
public class CommunityNewsPageDto {

    private Long id;

    private String name;

    private String link;

    private boolean isVerified;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date verificationDate;

    private CommunityVerifierUserDto verifier;

    private String avatar;

    private int membersCount;

    private int subgroupsCount;

    private CommunityAccessType accessType;

    @JsonSerialize(using = ShortDateSerializer.class)
    private Date createdAt;

    private boolean isVisible;

    private String announcement;

    private String creatorLink;

    private String creatorAvatar;

    private Long creatorId;

    private String creatorIkp;

    private String creatorFullName;

    private boolean isRoot;

    private CommunityMemberDto selfMember;

    private OkvedDto mainOkved;

    private List<OkvedDto> additionalOkveds;

    /**
     * Форма оранизации юр лица
     */
    private ListEditorItemDto associationForm;

    /**
     * Сферы деятельности
     */
    private List<ListEditorItemDto> activityScopes;

    private ParticipantsTypes type;

    private CommunityMenuDataDto menuData;

    public static CommunityNewsPageDto toDto(Community community, CommunityMember communityMember) {
        CommunityNewsPageDto result = new CommunityNewsPageDto();

        result.setId(community.getId());
        result.setName(community.getFullRuName());
        result.setLink(community.getLink());
        result.setVerified(community.isVerified());
        result.setVerificationDate(community.getVerificationDate());
        result.setVerifier(CommunityVerifierUserDto.toDto(community.getVerifier()));
        result.setAvatar(ImagesUtils.getResizeUrl(community.getAvatarUrl(), "c250"));
        result.setMembersCount(community.getMembersCount());
        result.setSubgroupsCount(community.getSubgroupsCount());
        result.setAccessType(community.getAccessType());
        result.setCreatedAt(community.getCreatedAt());
        result.setVisible(community.isVisible());
        result.setAnnouncement(community.getAnnouncement());
        result.setCreatorLink(community.getCreator().getLink());
        result.setCreatorAvatar(community.getCreator().getAvatar());
        result.setCreatorId(community.getCreator().getId());
        result.setCreatorIkp(community.getCreator().getIkp());
        result.setCreatorFullName(community.getCreator().getFullName());
        result.setRoot(community.getRoot() == null);

        if (communityMember != null) {
            result.setSelfMember(new CommunityMemberDto(communityMember));
        }

        if (community.getMainOkved() != null) {
            result.setMainOkved(new OkvedDto(community.getMainOkved()));
        }
        if (community.getOkveds() != null) {
            result.setAdditionalOkveds(OkvedDto.toListDto(community.getOkveds()));
        }

        if (community.getAssociationForm() != null) {
            ListEditorItemDto associationForm = new ListEditorItemDto();
            associationForm.setId(community.getAssociationForm().getId());
            associationForm.setCode(community.getAssociationForm().getCode());
            associationForm.setText(community.getAssociationForm().getText());
            result.setAssociationForm(associationForm);
        }

        if (community.getActivityScopes() != null) { // TODO нужна ли загрузка дерева
            result.setActivityScopes(new ArrayList<>());
            for (ListEditorItem communityActivityScope : community.getActivityScopes()) {
                ListEditorItemDto listEditorItemDto = new ListEditorItemDto();
                listEditorItemDto.setId(communityActivityScope.getId());
                listEditorItemDto.setCode(communityActivityScope.getCode());
                listEditorItemDto.setText(communityActivityScope.getText());
                result.getActivityScopes().add(listEditorItemDto);
            }
        }

        result.setType(community.getCommunityType());
        result.setMenuData(new CommunityMenuDataDto(community));

        return result;
    }
}
