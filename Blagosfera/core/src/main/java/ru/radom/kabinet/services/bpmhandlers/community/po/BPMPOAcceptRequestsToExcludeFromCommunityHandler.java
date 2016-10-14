package ru.radom.kabinet.services.bpmhandlers.community.po;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.po.dto.BPMPOAcceptRequestsToExcludeFromCommunityDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.behavior.po.POSharerCommunityMemberBehavior;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 18.08.2016.
 */
@Service("poAcceptRequestsToExcludeFromCommunityHandler")
@Transactional
public class BPMPOAcceptRequestsToExcludeFromCommunityHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private POSharerCommunityMemberBehavior poSharerCommunityMemberBehavior;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private UserDataService userDataService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMPOAcceptRequestsToExcludeFromCommunityDto bpmpoAcceptRequestsToExcludeFromCommunityDto = serializeService.toObject(parameters, BPMPOAcceptRequestsToExcludeFromCommunityDto.class);
        List<CommunityMember> members = communityMemberDomainService.getByIds(bpmpoAcceptRequestsToExcludeFromCommunityDto.getMemberIds(), true, true, true, false);
        boolean notifySignEvent = BooleanUtils.toBooleanDefaultIfNull(bpmpoAcceptRequestsToExcludeFromCommunityDto.getNotifySignEvent(), true);
        User excluder = userDataService.getByIdFullData(bpmpoAcceptRequestsToExcludeFromCommunityDto.getExcluderId());

        CommunityMemberResponseDto result = poSharerCommunityMemberBehavior.acceptRequestsToExcludeFromCommunity(members, excluder, notifySignEvent);
        return serializeService.toPrimitiveObject(result);
    }

}