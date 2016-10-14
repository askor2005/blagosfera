package ru.radom.kabinet.services.bpmhandlers.community.dto;

import lombok.Getter;
import ru.askor.blagosfera.domain.community.CommunityPostRequestStatus;

/**
 *
 * Created by vgusev on 02.08.2016.
 */
@Getter
public class BPMSetStatusPostRequestDto {

    private Long postRequestId;

    private CommunityPostRequestStatus communityPostRequestStatus;
}
