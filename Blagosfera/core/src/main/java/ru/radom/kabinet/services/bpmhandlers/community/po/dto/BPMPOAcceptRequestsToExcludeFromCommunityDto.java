package ru.radom.kabinet.services.bpmhandlers.community.po.dto;

import lombok.Getter;

import java.util.List;

/**
 *
 * Created by vgusev on 18.08.2016.
 */
@Getter
public class BPMPOAcceptRequestsToExcludeFromCommunityDto {

    private List<Long> memberIds;

    private Long excluderId;

    private Boolean notifySignEvent;
}
