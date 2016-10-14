package ru.radom.kabinet.services.bpmhandlers.community.po.dto;

import lombok.Getter;

/**
 *
 * Created by vgusev on 18.08.2016.
 */
@Getter
public class BPMPORequestToExcludeCommunityDto {

    private Long userId;

    private Long communityId;
}
