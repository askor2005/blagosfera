package ru.radom.kabinet.services.bpmhandlers.community.po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.po.dto.BPMPORequestToExcludeCommunityDto;
import ru.radom.kabinet.services.bpmhandlers.community.po.dto.BPMPORequestToExcludeFromCommunityOwnerDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.behavior.po.POSharerCommunityMemberBehavior;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.util.Collections;
import java.util.Map;

/**
 *
 * Created by vgusev on 18.08.2016.
 */
@Service("poRequestToExcludeFromCommunityOwnerHandler")
@Transactional
public class BPMPORequestToExcludeFromCommunityOwnerHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private POSharerCommunityMemberBehavior poSharerCommunityMemberBehavior;

    @Autowired
    private UserDataService userDataService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMPORequestToExcludeFromCommunityOwnerDto bpmpoRequestToExcludeFromCommunityOwnerDto = serializeService.toObject(parameters, BPMPORequestToExcludeFromCommunityOwnerDto.class);
        CommunityMember communityMember = communityMemberDomainService.getByCommunityIdAndUserId(bpmpoRequestToExcludeFromCommunityOwnerDto.getCommunityId(), bpmpoRequestToExcludeFromCommunityOwnerDto.getUserId());
        User excluder = userDataService.getByIdFullData(bpmpoRequestToExcludeFromCommunityOwnerDto.getExcluderId());
        CommunityMemberResponseDto result = poSharerCommunityMemberBehavior.requestToExcludeFromCommunityOwner(communityMember, excluder);
        Map<String, Object> resultMap = serializeService.toPrimitiveObject(result);
        resultMap.put("excluder", serializeService.toPrimitiveObject(excluder));
        resultMap.put("memberIds", Collections.singletonList(communityMember.getId()));
        return resultMap;
    }

}