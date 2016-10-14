package ru.radom.kabinet.web.communities.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.community.log.CommunityLogEvent;
import ru.askor.blagosfera.domain.community.log.CommunityMemberLogEvent;
import ru.askor.blagosfera.domain.community.log.CommunityNewsLogEvent;
import ru.radom.kabinet.json.FullDateSerializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 04.04.2016.
 */
@Data
public class CommunityActivityJournalDto {

    private Long id;

    private String userShortName;

    private String userLink;

    private String type;

    @JsonSerialize(using = FullDateSerializer.class)
    private Date date;

    private String additionalLink;

    private String additionalContent;

    public CommunityActivityJournalDto(CommunityLogEvent communityLogEvent) {
        setId(communityLogEvent.getId());
        setUserShortName(communityLogEvent.getUser().getShortName());
        setUserLink(communityLogEvent.getUser().getLink());
        setType(communityLogEvent.getType().toString());
        setDate(communityLogEvent.getDate());
        if (communityLogEvent instanceof CommunityMemberLogEvent) {
            CommunityMemberLogEvent communityMemberLogEvent = (CommunityMemberLogEvent)communityLogEvent;
            if (communityMemberLogEvent.getMemberUser() != null) {
                setAdditionalLink(communityMemberLogEvent.getMemberUser().getLink());
                setAdditionalContent(communityMemberLogEvent.getMemberUser().getShortName());
            }
        } else if (communityLogEvent instanceof CommunityNewsLogEvent) {
            CommunityNewsLogEvent communityNewsLogEvent = (CommunityNewsLogEvent)communityLogEvent;
            if (communityNewsLogEvent.getNews() != null) {
                setAdditionalLink(communityNewsLogEvent.getNews().getLink());
                setAdditionalContent( communityNewsLogEvent.getNews().getTitle());
            }
        }
    }

    public static List<CommunityActivityJournalDto> toDtoList(List<CommunityLogEvent> communityLogEvents) {
        List<CommunityActivityJournalDto> result;
        if (communityLogEvents != null) {
            result = communityLogEvents.stream().map(CommunityActivityJournalDto::new).collect(Collectors.toList());
        } else {
            result = new ArrayList<>();
        }
        return result;
    }
}
