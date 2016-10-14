package ru.radom.kabinet.web.communities.dto;

import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityAccessType;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;

import java.util.Date;

/**
 * Created by vgusev on 09.03.2016.
 */
public class CommunityListItemDto {

    public Long id;
    public String name;
    public String link;
    public boolean isVerified;
    public String avatar;
    public int membersCount;
    public int subgroupsCount;
    public CommunityAccessType accessType;
    public Date createdAt;
    public boolean isVisible;
    public String announcement;
    public String creatorLink;
    public String creatorAvatar;
    public Long creatorId;
    public String creatorIkp;
    public String creatorShortName;
    public Long memberId;
    public CommunityMemberStatus memberStatus;
    public boolean isMemberCreator;
    public boolean isRoot;
    public boolean canDelete;
    public boolean canRestore;
    public boolean deleted;
    public String deleteComment;

    public CommunityListItemDto() {
    }

    public CommunityListItemDto(Community community, CommunityMember communityMember, boolean canDelete, boolean canRestore) {
        id = community.getId();
        name = community.getFullRuName();
        link = community.getLink();
        isVerified = community.isVerified();
        avatar = community.getAvatarUrl();
        membersCount = community.getMembersCount();
        subgroupsCount = community.getSubgroupsCount();
        accessType = community.getAccessType();
        createdAt = community.getCreatedAt();
        isVisible = community.isVisible();
        announcement = community.getAnnouncement();
        creatorLink = community.getCreator().getLink();
        creatorAvatar = community.getCreator().getAvatar();
        creatorId = community.getCreator().getId();
        creatorIkp = community.getCreator().getIkp();
        creatorShortName = community.getCreator().getShortName();
        isRoot = community.getRoot() == null;

        if (communityMember != null) {
            memberId = communityMember.getId();
            memberStatus = communityMember.getStatus();
            isMemberCreator = community.getCreator().getId().equals(communityMember.getUser().getId());
        }

        this.canDelete = canDelete;
        this.canRestore = canRestore;

        deleted = community.isDeleted();
        deleteComment = community.getDeleteComment();
    }
}
