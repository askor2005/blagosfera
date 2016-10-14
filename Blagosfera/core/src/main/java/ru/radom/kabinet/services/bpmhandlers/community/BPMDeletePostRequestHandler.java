package ru.radom.kabinet.services.bpmhandlers.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityPostRequest;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.dto.BPMDeletePostRequestDto;
import ru.radom.kabinet.services.communities.CommunityPostRequestDomainService;

import java.util.Map;

/**
 *
 * Created by vgusev on 03.08.2016.
 */
@Service("deletePostRequestHandler")
@Transactional
public class BPMDeletePostRequestHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityPostRequestDomainService communityPostRequestDomainService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMDeletePostRequestDto bpmSetStatusPostRequest = serializeService.toObject(parameters, BPMDeletePostRequestDto.class);
        CommunityPostRequest communityPostRequest = communityPostRequestDomainService.getById(bpmSetStatusPostRequest.getPostRequestId());
        Map<String, Object> result = null;
        if (communityPostRequest != null) {
            communityPostRequestDomainService.delete(communityPostRequest.getId());
            result = serializeService.toPrimitiveObject(communityPostRequest);
        }
        return result;
    }
}