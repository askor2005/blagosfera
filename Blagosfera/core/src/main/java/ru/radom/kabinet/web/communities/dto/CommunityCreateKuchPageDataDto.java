package ru.radom.kabinet.web.communities.dto;

import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.voting.BatchVotingConstants;
import ru.radom.kabinet.voting.CommonVotingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 13.05.2016.
 */
public class CommunityCreateKuchPageDataDto {

    public int minCountParticipants;

    public List<String> requirementsForCreateMeeting;

    public Map<String, String> votingTypes;

    public CommunityFullDataDto community;

    public List<CommunityMemberKuchPageDto> communityMembers;

    public boolean votersNeedBeVerified;

    public CommunityCreateKuchPageDataDto(int minCountParticipants, Community community, CommunityMember selfMember, List<CommunityMember> communityMembers, boolean votersNeedBeVerified) {
        this.minCountParticipants = minCountParticipants;
        this.community = new CommunityFullDataDto(community, selfMember);
        this.votingTypes = BatchVotingConstants.VOTING_TYPES;
        if (communityMembers != null) {
            this.communityMembers = new ArrayList<>();
            for (CommunityMember communityMember : communityMembers) {
                this.communityMembers.add(new CommunityMemberKuchPageDto(communityMember));
            }
        }
        this.votersNeedBeVerified = votersNeedBeVerified;
    }

    public CommunityCreateKuchPageDataDto(List<String> requirementsForCreateMeeting, Community community, CommunityMember selfMember) {
        this.requirementsForCreateMeeting = requirementsForCreateMeeting;
        this.community = new CommunityFullDataDto(community, selfMember);
    }
}
