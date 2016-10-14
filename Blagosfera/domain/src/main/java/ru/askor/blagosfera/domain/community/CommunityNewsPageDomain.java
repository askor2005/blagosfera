package ru.askor.blagosfera.domain.community;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * Created by vgusev on 11.03.2016.
 */
@Data
@AllArgsConstructor
public class CommunityNewsPageDomain implements Serializable {

    public static final long serialVersionUID = 1L;

    private boolean isCanDelete;

    private boolean isCanJoin;

    private boolean isCanRequest;

    private boolean isCanCancelRequest;

    private boolean isCanAcceptInvite;

    private boolean isCanRejectInvite;

    private boolean isCanLeave;

    private boolean isCanCancelRequestToLeave;

    private String maxFieldValueHeight;

    private boolean isCanTransferMoney;

    private boolean isCanJoinInCommunityAsOrganization;

    private boolean isConsumerSociety;

    private CommunityMember communityMember;

    private Map<String, Boolean> permissions;

}
