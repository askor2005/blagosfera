package ru.radom.kabinet.web.communities.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.community.CommunityEventTypeGroup;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.kabinet.json.ShortDateSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 04.04.2016.
 */
@Data
public class CommunityActivityJournalPageDataDto {

    @Data
    private class EventTypeGroup {
        private CommunityEventTypeGroup typeGroup;

        private String name;

        private List<EventType> eventTypes;
    }

    @Data
    private class EventType {
        private CommunityEventType type;

        private String name;
    }

    private CommunityAnyPageDto community;

    @JsonSerialize(using = ShortDateSerializer.class)
    private Date fromDate;

    @JsonSerialize(using = ShortDateSerializer.class)
    private Date toDate;

    private List<EventTypeGroup> typeGroups;

    public CommunityActivityJournalPageDataDto(
            Community community, CommunityMember selfMember, Date fromDate,  Date toDate) {
        setCommunity(CommunityAnyPageDto.toDto(community, selfMember));
        setFromDate(fromDate);
        setToDate(toDate);
        List<EventTypeGroup> typeGroups = new ArrayList<>();
        for (CommunityEventTypeGroup typeGroup : CommunityEventTypeGroup.values()) {
            EventTypeGroup group = new EventTypeGroup();
            group.setName(typeGroup.toString());
            group.setTypeGroup(typeGroup);
            List<EventType> eventTypes = new ArrayList<>();
            for (CommunityEventType type : CommunityEventType.values()) {
                if (type.getGroup().equals(typeGroup)) {
                    EventType eventType = new EventType();
                    eventType.setName(type.toString());
                    eventType.setType(type);
                    eventTypes.add(eventType);
                }
            }
            group.setEventTypes(eventTypes);
            typeGroups.add(group);
        }
        setTypeGroups(typeGroups);
    }
}
