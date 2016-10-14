package ru.radom.kabinet.services.bpmhandlers.community.po;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.po.dto.BPMPORequestToCommunityDto;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.sharermember.behavior.po.POSharerCommunityMemberBehavior;
import ru.radom.kabinet.services.communities.sharermember.dto.CommunityMemberResponseDto;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.util.Map;

/**
 * Запрос вступления в ПО / Принятие приглашения вступления в ПО
 * Created by vgusev on 10.08.2016.
 */
@Service("poRequestToCommunityHandler")
@Transactional
public class BPMPORequestToCommunityHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityDataService communityDataService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private POSharerCommunityMemberBehavior poSharerCommunityMemberBehavior;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMPORequestToCommunityDto bpmpoRequestToCommunityDto = serializeService.toObject(parameters, BPMPORequestToCommunityDto.class);
        User requester = userDataService.getByIdFullData(bpmpoRequestToCommunityDto.getUserId());
        Community community = communityDataService.getByIdFullData(bpmpoRequestToCommunityDto.getCommunityId());
        boolean notifySignEvent = BooleanUtils.toBooleanDefaultIfNull(bpmpoRequestToCommunityDto.getNotifySignEvent(), true);

        CommunityMemberResponseDto result = poSharerCommunityMemberBehavior.request(community, requester, notifySignEvent);

        return serializeService.toPrimitiveObject(result);
    }

}