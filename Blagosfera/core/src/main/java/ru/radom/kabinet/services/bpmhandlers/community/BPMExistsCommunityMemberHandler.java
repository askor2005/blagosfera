package ru.radom.kabinet.services.bpmhandlers.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.dto.BPMExistsCommunityMemberDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;

import java.util.Map;

/**
 *
 * Created by vguser on 05.08.2016.
 */
@Service("existsCommunityMemberHandler")
@Transactional
public class BPMExistsCommunityMemberHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMExistsCommunityMemberDto bpmExistsCommunityMemberDto = serializeService.toObject(parameters, BPMExistsCommunityMemberDto.class);
        boolean result;
        if (bpmExistsCommunityMemberDto.getStatus() != null) {
            result = communityMemberDomainService.exists(bpmExistsCommunityMemberDto.getCommunityId(), bpmExistsCommunityMemberDto.getUserId(), bpmExistsCommunityMemberDto.getStatus());
        } else {
            result = communityMemberDomainService.exists(bpmExistsCommunityMemberDto.getCommunityId(), bpmExistsCommunityMemberDto.getUserId());
        }
        return result;
    }
}