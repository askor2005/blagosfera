package ru.radom.kabinet.services.bpmhandlers.community.po;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.po.dto.BPMPOAcceptRequestsCommunityDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.behavior.po.POSharerCommunityMemberBehavior;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.util.List;
import java.util.Map;

/**
 * Принять запросы вступления в ПО
 * Created by vgusev on 10.08.2016.
 */
@Service("poAcceptRequestsCommunityHandler")
@Transactional
public class BPMPOAcceptRequestsCommunityHandler implements BPMHandler {

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
        BPMPOAcceptRequestsCommunityDto bpmpoAcceptRequestsCommunityDto = serializeService.toObject(parameters, BPMPOAcceptRequestsCommunityDto.class);
        List<CommunityMember> members = communityMemberDomainService.getByIds(bpmpoAcceptRequestsCommunityDto.getMemberIds(), true, true, true, false);
        boolean notifySignEvent = BooleanUtils.toBooleanDefaultIfNull(bpmpoAcceptRequestsCommunityDto.getNotifySignEvent(), true);
        User accepter = userDataService.getByIdFullData(bpmpoAcceptRequestsCommunityDto.getAccepterId());

        CommunityMemberResponseDto result = poSharerCommunityMemberBehavior.acceptRequests(members, accepter, notifySignEvent);
        return serializeService.toPrimitiveObject(result);
    }

}