package ru.radom.kabinet.security.context;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.security.SecurityUtils;

@Component("requestContext")
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestContextImpl implements RequestContext {

    private boolean isJustLogged;
    private CommunityEntity community;
    private CommunityMemberEntity communityMember;
    private UserEntity tempUserEntity;
    private Long communityId;

    @Override
    public boolean isJustLogged() {
        return isJustLogged;
    }

    @Override
    public void setIsJustLogged(boolean isJustLogged) {
        this.isJustLogged = isJustLogged;
    }

    @Override
    public CommunityEntity getCommunity() {
        return community;
    }

    @Override
    public void setCommunity(CommunityEntity community) {
        this.community = community;
    }

    @Override
    public Long getCommunityId() {
        return communityId;
    }

    @Override
    public void setCommunityId(Long id) {
        communityId = id;
    }

    @Override
    public CommunityMemberEntity getCommunityMember() {
        return communityMember;
    }

    @Override
    public void setCommunityMember(CommunityMemberEntity communityMember) {
        this.communityMember = communityMember;
    }

    @Override
    public boolean isCommunityMember() {
        return communityMember != null;
    }

    @Override
    public boolean isActiveCommunityMember() {
        boolean result = false;
        CommunityMemberStatus status = getCommunityMemberStatus();

        if (status != null) {
            switch (status) {
                case MEMBER: // Член сообщества
                case REQUEST_TO_LEAVE: // Подана заявка на выход
                case LEAVE_IN_PROCESS: // Выход из сообщества в рассмотрении
                    result = true;
            }
        }

        return result;
    }

    @Override
    public boolean isCommunityCreator() {
        if (communityMember != null && communityMember.isCreator()) {
            return true;
        }

        if (community != null && community.getCreator().getId().equals(SecurityUtils.getUser().getId())) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isRootCommunityCreator() {
        return (community != null) && (community.getRoot() != null)
                && community.getRoot().getCreator().getId().equals(SecurityUtils.getUser().getId());
    }

    public void setTempUserEntity(UserEntity tempUserEntity) {
        this.tempUserEntity = tempUserEntity;
    }

    public UserEntity getTempUserEntity() {
        return tempUserEntity;
    }

    private CommunityMemberStatus getCommunityMemberStatus() {
        if (communityMember != null) {
            return communityMember.getStatus();
        } else {
            return null;
        }
    }
}
