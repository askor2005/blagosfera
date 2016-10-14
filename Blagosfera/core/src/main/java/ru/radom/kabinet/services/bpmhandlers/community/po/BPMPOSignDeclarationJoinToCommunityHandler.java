package ru.radom.kabinet.services.bpmhandlers.community.po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.po.dto.BPMPOSignDeclarationJoinToCommunityDto;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.communities.sharermember.behavior.po.POSharerCommunityMemberBehavior;

import java.util.Map;

/**
 * Отправить запрос на вступление в ПО после подписания заявления
 * Created by vgusev on 10.08.2016.
 */
@Service("poSignDeclarationJoinToCommunityHandler")
@Transactional
public class BPMPOSignDeclarationJoinToCommunityHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private POSharerCommunityMemberBehavior poSharerCommunityMemberBehavior;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMPOSignDeclarationJoinToCommunityDto bpmpoSignDeclarationJoinToCommunityDto = serializeService.toObject(parameters, BPMPOSignDeclarationJoinToCommunityDto.class);
        CommunityMember member = communityMemberDomainService.getByCommunityIdAndUserId(bpmpoSignDeclarationJoinToCommunityDto.getCommunityId(), bpmpoSignDeclarationJoinToCommunityDto.getUserId());
        CommunityMember result = poSharerCommunityMemberBehavior.onSignedDeclarationToEntranceCooperative(member.getId());
        return serializeService.toPrimitiveObject(result);
    }

}