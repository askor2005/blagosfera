package ru.radom.kabinet.services.bpmhandlers.community.po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.po.dto.BPMPOCancelRequestCommunityDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.behavior.po.POSharerCommunityMemberBehavior;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;

import java.util.Map;

/**
 * Отменить запрос вступления в ПО
 * Created by vgusev on 12.08.2016.
 */
@Service("poCancelRequestCommunityHandler")
@Transactional
public class BPMPOCancelRequestCommunityHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private POSharerCommunityMemberBehavior poSharerCommunityMemberBehavior;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMPOCancelRequestCommunityDto bpmpoCancelRequestCommunityDto = serializeService.toObject(parameters, BPMPOCancelRequestCommunityDto.class);
        CommunityMember communityMember = communityMemberDomainService.getByIdFullData(bpmpoCancelRequestCommunityDto.getMemberId());
        CommunityMemberResponseDto result = poSharerCommunityMemberBehavior.cancelRequestFromMember(communityMember, null);

        return serializeService.toPrimitiveObject(result);
    }

}