package ru.radom.kabinet.services.bpmhandlers.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.dto.BPMCommunityMemberWebSocketDto;
import ru.radom.kabinet.services.bpmhandlers.user.BPMUserWebSocketHandler;
import ru.radom.kabinet.services.bpmhandlers.user.dto.BPMUserWebSocketDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 19.08.2016.
 */
@Service("communityMemberWebSocketHandler")
@Transactional
public class BPMCommunityMemberWebSocketHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private BPMUserWebSocketHandler bpmUserWebSocketHandler;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMCommunityMemberWebSocketDto bpmCommunityMemberWebSocketDto = serializeService.toObject(parameters, BPMCommunityMemberWebSocketDto.class);
        List<CommunityMember> members = communityMemberDomainService.getByIds(bpmCommunityMemberWebSocketDto.getMemberIds(), false, true, false, false);
        List<Object> result = new ArrayList<>();
        if (members != null) {
            for (CommunityMember member : members) {
                Map<String, Object> parametersForSignal = new HashMap<>();
                parametersForSignal.putAll(bpmCommunityMemberWebSocketDto.getParameters());
                parametersForSignal.put("user", member.getUser());
                parametersForSignal.put("member", member);

                BPMUserWebSocketDto bpmUserWebSocketDto = new BPMUserWebSocketDto();
                bpmUserWebSocketDto.setUserId(member.getUser().getId());
                bpmUserWebSocketDto.setEventType(bpmCommunityMemberWebSocketDto.getEventType());
                bpmUserWebSocketDto.setParameters(parametersForSignal);
                result.add(bpmUserWebSocketHandler.handleDto(bpmUserWebSocketDto));
            }
        }
        return result;
    }
}