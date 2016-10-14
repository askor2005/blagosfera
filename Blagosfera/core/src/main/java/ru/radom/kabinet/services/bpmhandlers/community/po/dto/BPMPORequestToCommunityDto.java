package ru.radom.kabinet.services.bpmhandlers.community.po.dto;

import lombok.Getter;

/**
 *
 * Created by vgusev on 10.08.2016.
 */
@Getter
public class BPMPORequestToCommunityDto {

    private Long communityId;

    private Long userId;

    private Boolean notifySignEvent;
}
