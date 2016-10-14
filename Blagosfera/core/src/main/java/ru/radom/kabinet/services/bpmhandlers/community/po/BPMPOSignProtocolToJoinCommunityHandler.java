package ru.radom.kabinet.services.bpmhandlers.community.po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.po.dto.BPMPOSignProtocolToJoinCommunityDto;
import ru.radom.kabinet.services.communities.sharermember.behavior.po.POSharerCommunityMemberBehavior;

import java.util.List;
import java.util.Map;

/**
 * Подписан протокол принятия пайщиков в ПО
 * Created by vgusev on 10.08.2016.
 */
@Service("poSignProtocolToJoinCommunityHandler")
@Transactional
public class BPMPOSignProtocolToJoinCommunityHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private POSharerCommunityMemberBehavior poSharerCommunityMemberBehavior;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMPOSignProtocolToJoinCommunityDto bpmpoSignProtocolToJoinCommunityDto = serializeService.toObject(parameters, BPMPOSignProtocolToJoinCommunityDto.class);
        List<CommunityMember> result = poSharerCommunityMemberBehavior.onSignedProtocolToJoinSharersToCooperative(bpmpoSignProtocolToJoinCommunityDto.getMemberIds());
        return serializeService.toPrimitiveObject(result);
    }

}