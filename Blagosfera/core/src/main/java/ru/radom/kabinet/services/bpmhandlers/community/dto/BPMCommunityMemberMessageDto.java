package ru.radom.kabinet.services.bpmhandlers.community.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 18.08.2016.
 */
@Getter
public class BPMCommunityMemberMessageDto {

    private List<Long> memberIds;

    private String content;

    private Map<String, Object> parameters;
}
