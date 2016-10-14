package ru.radom.kabinet.services.bpmhandlers.community.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 19.08.2016.
 */
@Getter
public class BPMCommunityMemberWebSocketDto {

    private List<Long> memberIds;

    private String eventType;

    private Map<String, Object> parameters;
}
