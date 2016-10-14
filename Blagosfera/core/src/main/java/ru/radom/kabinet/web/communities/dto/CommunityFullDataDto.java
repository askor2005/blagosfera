package ru.radom.kabinet.web.communities.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityAccessType;
import ru.askor.blagosfera.domain.community.CommunityData;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.field.FieldsGroup;
import ru.radom.kabinet.json.ShortDateSerializer;
import ru.radom.kabinet.json.TimeStampDateDeserializer;
import ru.radom.kabinet.json.TimeStampDateSerializer;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.ImagesUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 22.03.2016.
 */
@Data
public class CommunityFullDataDto {

    private Long id;

    private String name;

    private String shortRuName;

    private String link;

    private boolean isVerified;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    @JsonDeserialize(using = TimeStampDateDeserializer.class)
    private Date verificationDate;

    private CommunityVerifierUserDto verifier;

    private String avatar;

    private int membersCount;

    private int subgroupsCount;

    private CommunityAccessType accessType;

    @JsonSerialize(using = ShortDateSerializer.class)
    @JsonDeserialize(using = TimeStampDateDeserializer.class)
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

    private Long parentId;

    private List<CommunityFieldGroupDto> fieldGroups;

    private CommunityMenuDataDto menuData;

    public CommunityFullDataDto() {}
    
    public CommunityFullDataDto(Community community, CommunityMember selfMember) {
        setId(community.getId());
        setName(community.getFullRuName());
        setShortRuName(community.getShortRuName());
        setLink(community.getLink());
        setVerified(community.isVerified());
        setVerificationDate(community.getVerificationDate());
        setVerifier(CommunityVerifierUserDto.toDto(community.getVerifier()));
        setAvatar(ImagesUtils.getResizeUrl(community.getAvatarUrl(), "c250"));
        setMembersCount(community.getMembersCount());
        setSubgroupsCount(community.getSubgroupsCount());
        setAccessType(community.getAccessType());
        setCreatedAt(community.getCreatedAt());
        setVisible(community.isVisible());
        setAnnouncement(community.getAnnouncement());
        setCreatorLink(community.getCreator().getLink());
        setCreatorAvatar(community.getCreator().getAvatar());
        setCreatorId(community.getCreator().getId());
        setCreatorIkp(community.getCreator().getIkp());
        setCreatorFullName(community.getCreator().getFullName());
        setRoot(community.getRoot() == null);

        if (selfMember != null) {
            setSelfMember(new CommunityMemberDto(selfMember));
        }

        if (community.getMainOkved() != null) {
            setMainOkved(new OkvedDto(community.getMainOkved()));
        }
        if (community.getOkveds() != null) {
            setAdditionalOkveds(OkvedDto.toListDto(community.getOkveds()));
        }

        if (community.getAssociationForm() != null) {
            setAssociationForm(new ListEditorItemDto(community.getAssociationForm()));
        }
        if (community.getActivityScopes() != null) { // TODO нужна ли загрузка дерева
            setActivityScopes(ListEditorItemDto.toListDto(community.getActivityScopes()));
        }

        setType(community.getCommunityType());

        setParentId(community.getParent() != null ? community.getParent().getId() : null);

        if (community.getCommunityData() != null && community.getCommunityData().getFieldGroups() != null) {
            List<FieldsGroup> fieldGroups = community.getCommunityData().getFieldGroups();
            List<CommunityFieldGroupDto> communityFieldGroupsDto = fieldGroups.stream().map(CommunityFieldGroupDto::new).collect(Collectors.toList());
            for (CommunityFieldGroupDto communityFieldGroup : communityFieldGroupsDto) {
                if (communityFieldGroup != null && communityFieldGroup.getFields() != null) {
                    Iterator<CommunityFieldDto> communityFieldIterator = communityFieldGroup.getFields().iterator();
                    while(communityFieldIterator.hasNext()) {
                        CommunityFieldDto communityField = communityFieldIterator.next();
                        // Если текущий пользователь не является пользователем объединения, то скрыть от него скрытые поля
                        if (communityField.isHideable() && selfMember == null && communityField.isHidden()) {
                            communityFieldIterator.remove();
                        }
                    }
                }
            }

            setFieldGroups(communityFieldGroupsDto);
        }

        setMenuData(new CommunityMenuDataDto(community));
    }

    public Community toDomain() {
        Community result = new Community();
        result.setId(getId());
        result.setFullRuName(getName());
        result.setLink(getLink());
        result.setVerified(isVerified());
        result.setVerificationDate(getVerificationDate());
        result.setAccessType(getAccessType());
        result.setVisible(isVisible());
        result.setAnnouncement(getAnnouncement());
        result.setCommunityType(getType());
        if (getMainOkved() != null) {
            result.setMainOkved(getMainOkved().toDomain());
        }
        if (getAdditionalOkveds() != null) {
            result.getOkveds().addAll(OkvedDto.toListDomain(getAdditionalOkveds()));
        }
        if (getAssociationForm() != null) {
            result.setAssociationForm(getAssociationForm().toDomain());
        }
        if (getActivityScopes() != null) {
            result.getActivityScopes().addAll(ListEditorItemDto.toListDomain(getActivityScopes()));
        }

        if (getFieldGroups() != null) {
            CommunityData communityData = new CommunityData();
            communityData.setFieldGroups(CommunityFieldGroupDto.toListDomain(getFieldGroups()));
            result.setCommunityData(communityData);
        }

        if (getParentId() != null) {
            Community parentCommunity = new Community();
            parentCommunity.setId(getParentId());
            result.setParent(parentCommunity);
        }

        return result;
    }
}
