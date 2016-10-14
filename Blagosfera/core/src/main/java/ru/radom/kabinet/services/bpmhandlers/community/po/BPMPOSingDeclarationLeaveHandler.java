package ru.radom.kabinet.services.bpmhandlers.community.po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.po.dto.BPMPOSingDeclarationLeaveDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.behavior.po.POSharerCommunityMemberBehavior;

import java.util.Map;

/**
 * Обработчик подписания заявления на выход из ПО
 * Created by vgusev on 18.08.2016.
 */
@Service("poSingDeclarationLeaveHandler")
@Transactional
public class BPMPOSingDeclarationLeaveHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private POSharerCommunityMemberBehavior poSharerCommunityMemberBehavior;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMPOSingDeclarationLeaveDto bpmpoSingDeclarationLeaveDto = serializeService.toObject(parameters, BPMPOSingDeclarationLeaveDto.class);
        CommunityMember communityMember = communityMemberDomainService.getByCommunityIdAndUserId(bpmpoSingDeclarationLeaveDto.getCommunityId(), bpmpoSingDeclarationLeaveDto.getUserId());
        poSharerCommunityMemberBehavior.onSignedDeclarationToLeaveCooperative(communityMember);
        return true;
    }

}