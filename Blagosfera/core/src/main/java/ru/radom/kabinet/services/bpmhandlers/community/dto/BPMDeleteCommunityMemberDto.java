package ru.radom.kabinet.services.bpmhandlers.community.dto;

import lombok.Getter;

/**
 *
 * Created by vgusev on 05.08.2016.
 */
@Getter
public class BPMDeleteCommunityMemberDto {

    private Long userId;

    private Long communityId;
}
