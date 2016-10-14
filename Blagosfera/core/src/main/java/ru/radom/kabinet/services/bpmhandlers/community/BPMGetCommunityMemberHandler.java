package ru.radom.kabinet.services.bpmhandlers.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.dto.BPMGetCommunityMemberDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;

import java.util.Map;

/**
 *
 * Created by vgusev on 08.08.2016.
 */
@Service("getCommunityMemberHandler")
@Transactional
public class BPMGetCommunityMemberHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMGetCommunityMemberDto bpmGetCommunityMemberDto = serializeService.toObject(parameters, BPMGetCommunityMemberDto.class);
        CommunityMember communityMember = communityMemberDomainService.getByCommunityIdAndUserId(bpmGetCommunityMemberDto.getCommunityId(), bpmGetCommunityMemberDto.getUserId());
        CommunityMember result = null;
        if (bpmGetCommunityMemberDto.getStatus() != null &&
                bpmGetCommunityMemberDto.getStatus().equals(communityMember.getStatus()) ||
                bpmGetCommunityMemberDto.getStatus() == null) {
            result = communityMember;
        }
        return serializeService.toPrimitiveObject(result);
    }
}