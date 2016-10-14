package ru.radom.kabinet.services.bpmhandlers.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.dto.BPMDeleteCommunityMemberDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;

import java.util.Map;

/**
 *
 * Created by vgusev on 05.08.2016.
 */
@Service("deleteCommunityMemberHandler")
@Transactional
public class BPMDeleteCommunityMemberHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMDeleteCommunityMemberDto bpmDeleteCommunityMemberDto = serializeService.toObject(parameters, BPMDeleteCommunityMemberDto.class);
        CommunityMember communityMember = communityMemberDomainService.getByCommunityIdAndUserId(bpmDeleteCommunityMemberDto.getCommunityId(), bpmDeleteCommunityMemberDto.getUserId());
        if (communityMember != null) {
            communityMember = communityMemberDomainService.delete(communityMember.getId());
        }
        return serializeService.toPrimitiveObject(communityMember);
    }
}