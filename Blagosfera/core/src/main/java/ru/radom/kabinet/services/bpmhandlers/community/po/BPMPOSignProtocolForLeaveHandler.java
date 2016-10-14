package ru.radom.kabinet.services.bpmhandlers.community.po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.po.dto.BPMPOSignProtocolForLeaveDto;
import ru.radom.kabinet.services.communities.sharermember.behavior.po.POSharerCommunityMemberBehavior;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 18.08.2016.
 */
@Service("poSignProtocolForLeaveHandler")
@Transactional
public class BPMPOSignProtocolForLeaveHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private POSharerCommunityMemberBehavior poSharerCommunityMemberBehavior;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMPOSignProtocolForLeaveDto bpmpoSignProtocolForLeaveDto = serializeService.toObject(parameters, BPMPOSignProtocolForLeaveDto.class);
        List<Long> userIds = poSharerCommunityMemberBehavior.onSignedProtocolForLeaveSharersFromCooperative(bpmpoSignProtocolForLeaveDto.getMemberIds());
        return userIds;
    }

}