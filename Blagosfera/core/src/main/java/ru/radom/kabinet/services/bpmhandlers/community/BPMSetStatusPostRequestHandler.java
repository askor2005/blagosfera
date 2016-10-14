package ru.radom.kabinet.services.bpmhandlers.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityPostRequest;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.dto.BPMSetStatusPostRequestDto;
import ru.radom.kabinet.services.communities.CommunityPostRequestDomainService;

import java.util.Map;

/**
 *
 * Created by vgusev on 02.08.2016.
 */
@Service("setStatusPostRequestHandler")
@Transactional
public class BPMSetStatusPostRequestHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityPostRequestDomainService communityPostRequestDomainService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMSetStatusPostRequestDto bpmSetStatusPostRequest = serializeService.toObject(parameters, BPMSetStatusPostRequestDto.class);
        CommunityPostRequest communityPostRequest = communityPostRequestDomainService.getById(bpmSetStatusPostRequest.getPostRequestId());
        Map<String, Object> result = null;
        if (communityPostRequest != null) {
            communityPostRequest.setStatus(bpmSetStatusPostRequest.getCommunityPostRequestStatus());
            communityPostRequest = communityPostRequestDomainService.save(communityPostRequest);
            result = serializeService.toPrimitiveObject(communityPostRequest);
        }
        return result;
    }
}