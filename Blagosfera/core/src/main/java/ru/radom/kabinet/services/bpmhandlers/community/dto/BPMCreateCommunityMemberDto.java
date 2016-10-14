package ru.radom.kabinet.services.bpmhandlers.community.dto;

import lombok.Getter;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;

/**
 *
 * Created by vgusev on 05.08.2016.
 */
@Getter
public class BPMCreateCommunityMemberDto {

    private Long userId;

    private Long communityId;

    private CommunityMemberStatus status;

    private Long inviterId;
}
