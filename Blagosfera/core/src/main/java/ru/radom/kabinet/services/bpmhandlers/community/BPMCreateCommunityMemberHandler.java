package ru.radom.kabinet.services.bpmhandlers.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.dto.BPMCreateCommunityMemberDto;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.util.Date;
import java.util.Map;

/**
 *
 * Created by vgusev on 05.08.2016.
 */
@Service("createCommunityMemberHandler")
@Transactional
public class BPMCreateCommunityMemberHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private CommunityDataService communityDataService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMCreateCommunityMemberDto bpmCreateCommunityMemberDto = serializeService.toObject(parameters, BPMCreateCommunityMemberDto.class);
        CommunityMember communityMember = communityMemberDomainService.getByCommunityIdAndUserId(bpmCreateCommunityMemberDto.getCommunityId(), bpmCreateCommunityMemberDto.getUserId());
        if (communityMember == null) {
            communityMember = new CommunityMember();
            communityMember.setUser(userDataService.getByIdMinData(bpmCreateCommunityMemberDto.getUserId()));
            communityMember.setCommunity(communityDataService.getByIdMinData(bpmCreateCommunityMemberDto.getCommunityId()));
            communityMember.setCreator(false);
            communityMember.setInviter(userDataService.getByIdMinData(bpmCreateCommunityMemberDto.getInviterId()));
            communityMember.setRequestDate(new Date());
        }
        communityMember.setStatus(bpmCreateCommunityMemberDto.getStatus());
        communityMember = communityMemberDomainService.save(communityMember);
        return serializeService.toPrimitiveObject(communityMember);
    }
}