package ru.radom.kabinet.services.communities;

import ru.askor.blagosfera.domain.community.CommunityPostRequest;

import java.util.List;

/**
 *
 * Created by vgusev on 21.03.2016.
 */
public interface CommunityPostRequestDomainService {

    CommunityPostRequest getById(Long id);

    List<CommunityPostRequest> getReceivedCommunityPostRequests(Long communityMemberId);

    List<CommunityPostRequest> getSendedCommunityPostRequests(Long communityMemberId);

    CommunityPostRequest save(CommunityPostRequest communityPostRequest);

    CommunityPostRequest delete(Long id);
}
