package ru.radom.kabinet.services.bpmhandlers.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityPostRequest;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.dto.BPMCommunityPostRequestDto;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityPostRequestDomainService;

import java.util.Map;

/**
 *
 * Created by vgusev on 02.08.2016.
 */
@Service("appointToPostHandler")
@Transactional
public class BPMAppointToPostHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private CommunityPostRequestDomainService communityPostRequestDomainService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMCommunityPostRequestDto bpmCommunityPostRequest = serializeService.toObject(parameters, BPMCommunityPostRequestDto.class);
        CommunityPostRequest communityPostRequest = communityPostRequestDomainService.getById(bpmCommunityPostRequest.getPostRequestId());
        communitiesService.appoint(communityPostRequest);
        return serializeService.toPrimitiveObject(communityPostRequest);
    }

}
