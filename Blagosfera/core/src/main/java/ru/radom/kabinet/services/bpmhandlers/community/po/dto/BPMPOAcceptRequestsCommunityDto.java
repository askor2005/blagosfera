package ru.radom.kabinet.services.bpmhandlers.community.po.dto;

import lombok.Getter;

import java.util.List;

/**
 *
 * Created by vgusev on 10.08.2016.
 */
@Getter
public class BPMPOAcceptRequestsCommunityDto {

    private List<Long> memberIds;

    private Long accepterId;

    private Boolean notifySignEvent;
}
