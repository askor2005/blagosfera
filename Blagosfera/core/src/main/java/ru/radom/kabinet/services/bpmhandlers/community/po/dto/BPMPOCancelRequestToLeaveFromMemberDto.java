package ru.radom.kabinet.services.bpmhandlers.community.po.dto;

import lombok.Getter;

import java.util.List;

/**
 *
 * Created by vgusev on 18.08.2016.
 */
@Getter
public class BPMPOCancelRequestToLeaveFromMemberDto {

    private Long userId;

    private Long communityId;

    private List<Long> memberIds;
}
