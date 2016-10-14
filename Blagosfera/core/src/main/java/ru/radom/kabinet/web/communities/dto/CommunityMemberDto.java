package ru.radom.kabinet.web.communities.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.radom.kabinet.json.TimeStampDateSerializer;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.web.user.dto.UserDataDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 11.03.2016.
 */
public class CommunityMemberDto {

    public Long id;
    public CommunityMemberStatus status;
    public boolean creator;
    public UserDataDto user;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    public Date requestDate;

    public String sendRequestHumanString = "";
    public Long communityId;
    public String communityName;
    public boolean communityDeleted;

    public CommunityMemberDto() {
    }

    public CommunityMemberDto (CommunityMember communityMember) {
        id = communityMember.getId();
        status = communityMember.getStatus();
        creator = communityMember.isCreator();
        requestDate = communityMember.getRequestDate();
        communityId = communityMember.getCommunity().getId();
        communityName = communityMember.getCommunity().getFullRuName();
        communityDeleted = communityMember.getCommunity().isDeleted();

        if (communityMember.getRequestDate() != null) {
            int hoursDistance = DateUtils.getDistanceHours(communityMember.getRequestDate(), new Date());
            if (hoursDistance > 1) {
                sendRequestHumanString = DateUtils.getHumanReadableDistanceAccusative(hoursDistance);
            } else {
                sendRequestHumanString = "менее 1 часа";
            }
        }

        if (communityMember.getUser() != null) {
            user = new UserDataDto(communityMember.getUser());
        }
    }

    public static List<CommunityMemberDto> toDtoList(List<CommunityMember> communityMembers) {
        List<CommunityMemberDto> result = null;
        if (communityMembers != null) {
            result = new ArrayList<>();
            for (CommunityMember member : communityMembers) {
                result.add(new CommunityMemberDto(member));
            }
        }
        return result;
    }
}
