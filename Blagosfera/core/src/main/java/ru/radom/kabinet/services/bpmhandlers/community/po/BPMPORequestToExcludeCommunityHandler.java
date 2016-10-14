package ru.radom.kabinet.services.bpmhandlers.community.po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.po.dto.BPMPORequestToExcludeCommunityDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.behavior.po.POSharerCommunityMemberBehavior;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;

import java.util.Map;

/**
 * Запрос участника на выход из ПО
 * Created by vgusev on 18.08.2016.
 */
@Service("poRequestToExcludeCommunityHandler")
@Transactional
public class BPMPORequestToExcludeCommunityHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private POSharerCommunityMemberBehavior poSharerCommunityMemberBehavior;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMPORequestToExcludeCommunityDto bpmpoRequestToExcludeCommunityDto = serializeService.toObject(parameters, BPMPORequestToExcludeCommunityDto.class);
        CommunityMember communityMember = communityMemberDomainService.getByCommunityIdAndUserId(bpmpoRequestToExcludeCommunityDto.getCommunityId(), bpmpoRequestToExcludeCommunityDto.getUserId());
        CommunityMemberResponseDto result = poSharerCommunityMemberBehavior.requestToExcludeFromMember(communityMember, null);

        return serializeService.toPrimitiveObject(result);
    }

}