package ru.radom.kabinet.model.communities.postappointbehavior.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityPostRequest;
import ru.radom.kabinet.model.communities.postappointbehavior.IPostAppointBehavior;
import ru.radom.kabinet.services.communities.CommunityDataService;

/**
 * Класс - поведение назначения на должность по умолчанию
 * Created by vgusev on 28.08.2015.
 */
@Service(DefaultPostAppointBehavior.NAME)
@Transactional
public class DefaultPostAppointBehavior/* extends BasePostAppointBehavior*/ implements IPostAppointBehavior {

    public static final String NAME = "defaultPostAppointBehavior";

    @Autowired
    private CommunityDataService communityDataService;

    @Override
    public PostAppointData start(CommunityPostRequest communityPostRequest) {
        //appointMemberToPost(communityPostRequest);
        Community community = communityDataService.getByIdFullData(communityPostRequest.getCommunity().getId());
        return new PostAppointData(community, PostAppointResultType.DEFAULT);
    }
}
