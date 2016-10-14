package ru.radom.kabinet.web.communities.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.BatchVotingMode;
import ru.askor.voting.domain.VotingType;
import ru.radom.kabinet.json.TimeStampDateSerializer;
import ru.radom.kabinet.services.batchVoting.BatchVotingConstructorService;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingTemplateDto;
import ru.radom.kabinet.web.user.dto.UserDataDto;

import java.util.*;

/**
 *
 * Created by vgusev on 10.03.2016.
 */
public class CommunityConstructorBatchVotingPageDataDto {

    public CommunityAnyPageDto community;
    public Map<String, String> behaviors = new HashMap<>();
    public Map<BatchVotingMode, String> modes = new HashMap<>();
    public List<UserDataDto> possibleVoters = new ArrayList<>();
    public List<UserDataDto> childGroupsVoters = new ArrayList<>();

    // Виды голосований
    public Map<VotingType, String> votingTypes = new HashMap<>();

    public List<UserDataDto> possibleCandidates = new ArrayList<>();

    @JsonSerialize(using = TimeStampDateSerializer.class)
    public Date currentDateTime;

    // Сдвиг времени от кринвича у сервера
    public int timeZoneOffset;

    public BatchVotingTemplateDto batchVotingTemplate;

    // Участники должны быть сертифицированны
    public boolean votersNeedBeVerified;

    public String sentenceHelpText;

    public String successDecreeHelpText;

    public String failDecreeHelpText;

    public CommunityConstructorBatchVotingPageDataDto() {
    }

    public CommunityConstructorBatchVotingPageDataDto(
            CommunityAnyPageDto community,
            List<CommunityMember> communityMembers,List<CommunityMember> communityMembersChildGroups,
            Long currentUserId,
            BatchVotingTemplateDto batchVotingTemplateDto,
            boolean votersNeedBeVerified,
            String sentenceHelpText, String successDecreeHelpText, String failDecreeHelpText) {
        setCommunity(community);

        getBehaviors().putAll(BatchVotingConstructorService.BEHAVIORS);
        getModes().putAll(BatchVotingConstructorService.MODES);
        getVotingTypes().putAll(BatchVotingConstructorService.VOTING_TYPES);

        List<User> possibleVoters = new ArrayList<>();
        List<User> possibleCandidates = new ArrayList<>();
        List<User> childGroupsVoters = new ArrayList<>();

        for (CommunityMember member : communityMembers) {
            if (!currentUserId.equals(member.getUser().getId())) {
                possibleVoters.add(member.getUser());
            }

            possibleCandidates.add(member.getUser());
        }

        for (CommunityMember member : communityMembersChildGroups) {
            if ((!currentUserId.equals(member.getUser().getId())) && (!possibleVoters.contains(member.getUser()))) {
                possibleVoters.add(member.getUser());
            }

            if (!possibleCandidates.contains(member.getUser())) {
                possibleCandidates.add(member.getUser());
            }

            childGroupsVoters.add(member.getUser());
        }

        getPossibleVoters().addAll(UserDataDto.toDtoList(possibleVoters));
        getPossibleCandidates().addAll(UserDataDto.toDtoList(possibleCandidates));
        getChildGroupsVoters().addAll(UserDataDto.toDtoList(childGroupsVoters));

        setCurrentDateTime(new Date());

        TimeZone timezone = TimeZone.getDefault();
        setTimeZoneOffset(timezone.getOffset(new Date().getTime()));
        setBatchVotingTemplate(batchVotingTemplateDto);
        setVotersNeedBeVerified(votersNeedBeVerified);

        this.sentenceHelpText = sentenceHelpText;
        this.successDecreeHelpText = successDecreeHelpText;
        this.failDecreeHelpText = failDecreeHelpText;
    }

    public CommunityAnyPageDto getCommunity() {
        return community;
    }

    public void setCommunity(CommunityAnyPageDto community) {
        this.community = community;
    }

    public Map<String, String> getBehaviors() {
        return behaviors;
    }

    public Map<BatchVotingMode, String> getModes() {
        return modes;
    }

    public List<UserDataDto> getPossibleVoters() {
        return possibleVoters;
    }

    public List<UserDataDto> getChildGroupsVoters() {
        return childGroupsVoters;
    }

    public Map<VotingType, String> getVotingTypes() {
        return votingTypes;
    }

    public List<UserDataDto> getPossibleCandidates() {
        return possibleCandidates;
    }

    public Date getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(Date currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    public int getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(int timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public BatchVotingTemplateDto getBatchVotingTemplate() {
        return batchVotingTemplate;
    }

    public void setBatchVotingTemplate(BatchVotingTemplateDto batchVotingTemplate) {
        this.batchVotingTemplate = batchVotingTemplate;
    }

    public boolean isVotersNeedBeVerified() {
        return votersNeedBeVerified;
    }

    public void setVotersNeedBeVerified(boolean votersNeedBeVerified) {
        this.votersNeedBeVerified = votersNeedBeVerified;
    }
}
