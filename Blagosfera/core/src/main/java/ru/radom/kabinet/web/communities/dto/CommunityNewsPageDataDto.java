package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunityNewsPageDomain;

import java.util.Map;

/**
 *
 * Created by vgusev on 10.03.2016.
 */
@Data
public class CommunityNewsPageDataDto {

    private boolean isCanDelete;

    private boolean isCanJoin;

    private boolean isCanRequest;

    private boolean isCanCancelRequest;

    private boolean isCanAcceptInvite;

    private boolean isCanRejectInvite;

    private boolean isCanLeave;

    private boolean isCanCancelRequestToLeave;

    private CommunityNewsPageDto community;

    private String maxFieldValueHeight;

    private boolean isCanTransferMoney;

    private boolean isCanJoinInCommunityAsOrganization;

    private boolean isConsumerSociety;

    private Map<String, Boolean> permissions;

    public static CommunityNewsPageDataDto toDto(CommunityNewsPageDto community, CommunityNewsPageDomain communityNewsPageDomain) {
        CommunityNewsPageDataDto result = new CommunityNewsPageDataDto();
        result.setCommunity(community);
        result.setCanDelete(communityNewsPageDomain.isCanDelete());
        result.setCanJoin(communityNewsPageDomain.isCanJoin());
        result.setCanRequest(communityNewsPageDomain.isCanRequest());
        result.setCanCancelRequest(communityNewsPageDomain.isCanCancelRequest());
        result.setCanAcceptInvite(communityNewsPageDomain.isCanAcceptInvite());
        result.setCanRejectInvite(communityNewsPageDomain.isCanRejectInvite());
        result.setCanLeave(communityNewsPageDomain.isCanLeave());
        result.setCanCancelRequestToLeave(communityNewsPageDomain.isCanCancelRequestToLeave());
        result.setMaxFieldValueHeight(communityNewsPageDomain.getMaxFieldValueHeight());
        result.setCanTransferMoney(communityNewsPageDomain.isCanTransferMoney());
        result.setCanJoinInCommunityAsOrganization(communityNewsPageDomain.isCanJoinInCommunityAsOrganization());
        result.setConsumerSociety(communityNewsPageDomain.isConsumerSociety());
        result.setPermissions(communityNewsPageDomain.getPermissions());
        return result;
    }

}
