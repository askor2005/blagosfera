package ru.radom.kabinet.services.bpmhandlers.community.po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.po.dto.BPMPOCancelRequestToLeaveFromMemberDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.behavior.po.POSharerCommunityMemberBehavior;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 18.08.2016.
 */
@Service("poCancelRequestToLeaveFromMemberHandler")
@Transactional
public class BPMPOCancelRequestToLeaveFromMemberHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private POSharerCommunityMemberBehavior poSharerCommunityMemberBehavior;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMPOCancelRequestToLeaveFromMemberDto bpmpoCancelRequestToLeaveFromMemberDto = serializeService.toObject(parameters, BPMPOCancelRequestToLeaveFromMemberDto.class);
        if (bpmpoCancelRequestToLeaveFromMemberDto.getMemberIds() != null && !bpmpoCancelRequestToLeaveFromMemberDto.getMemberIds().isEmpty()) {
            List<CommunityMember> communityMembers = communityMemberDomainService.getByIds(bpmpoCancelRequestToLeaveFromMemberDto.getMemberIds(), false, true, true, false);
            for (CommunityMember communityMember : communityMembers) {
                poSharerCommunityMemberBehavior.cancelRequestToLeaveFromMember(communityMember);
            }
        } else {
            CommunityMember communityMember = communityMemberDomainService.getByCommunityIdAndUserId(bpmpoCancelRequestToLeaveFromMemberDto.getCommunityId(), bpmpoCancelRequestToLeaveFromMemberDto.getUserId());
            poSharerCommunityMemberBehavior.cancelRequestToLeaveFromMember(communityMember);
        }
        return true;
    }

}