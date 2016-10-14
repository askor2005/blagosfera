package ru.radom.kabinet.services.bpmhandlers.community.po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.po.dto.BPMPORejectRequestCommunityDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.behavior.po.POSharerCommunityMemberBehavior;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 12.08.2016.
 */
@Service("poRejectRequestCommunityHandler")
@Transactional
public class BPMPORejectRequestCommunityHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private POSharerCommunityMemberBehavior poSharerCommunityMemberBehavior;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMPORejectRequestCommunityDto bpmpoRejectRequestCommunityDto = serializeService.toObject(parameters, BPMPORejectRequestCommunityDto.class);
        CommunityMember communityMember = communityMemberDomainService.getByIdFullData(bpmpoRejectRequestCommunityDto.getMemberId());
        User rejecter = userDataService.getByIdFullData(bpmpoRejectRequestCommunityDto.getRejecterId());
        CommunityMemberResponseDto result = poSharerCommunityMemberBehavior.rejectRequestsFromCommunityOwner(
                Collections.singletonList(communityMember), rejecter);

        return serializeService.toPrimitiveObject(result);
    }

}