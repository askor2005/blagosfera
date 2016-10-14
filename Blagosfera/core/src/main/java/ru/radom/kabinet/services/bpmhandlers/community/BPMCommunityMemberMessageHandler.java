package ru.radom.kabinet.services.bpmhandlers.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.dto.BPMCommunityMemberMessageDto;
import ru.radom.kabinet.services.bpmhandlers.user.BPMUserMessageHandler;
import ru.radom.kabinet.services.bpmhandlers.user.dto.BPMUserMessageDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сообщение участникам объединения по ИДам
 * Created by vgusev on 18.08.2016.
 */
@Service("communityMemberMessageHandler")
@Transactional
public class BPMCommunityMemberMessageHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private BPMUserMessageHandler bpmUserMessageHandler;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMCommunityMemberMessageDto bpmCommunityMemberMessageDto = serializeService.toObject(parameters, BPMCommunityMemberMessageDto.class);
        List<CommunityMember> members = communityMemberDomainService.getByIds(bpmCommunityMemberMessageDto.getMemberIds(), false, true, false, false);
        List<Object> result = new ArrayList<>();
        if (members != null) {
            for (CommunityMember member : members) {
                Map<String, Object> parametersFoMessage = new HashMap<>();
                BPMUserMessageDto bpmUserMessageDto = new BPMUserMessageDto();
                bpmUserMessageDto.setContent(bpmCommunityMemberMessageDto.getContent());
                bpmUserMessageDto.setUserId(member.getUser().getId());
                parametersFoMessage.putAll(bpmCommunityMemberMessageDto.getParameters());
                parametersFoMessage.put("contextUser", serializeService.toPrimitiveObject(member.getUser()));
                parametersFoMessage.put("contextMember", serializeService.toPrimitiveObject(member));
                result.add(bpmUserMessageHandler.handlerMessage(bpmUserMessageDto));
            }
        }
        return result;
    }

}