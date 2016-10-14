package ru.radom.kabinet.services.communities;

import ru.askor.blagosfera.domain.community.CommunityDocumentRequest;
import ru.askor.blagosfera.domain.community.CommunityDocumentRequestsPage;

import java.util.List;

/**
 *
 * Created by vguse on 21.07.2016.
 */
public interface CommunityDocumentRequestService {

    CommunityDocumentRequest getById(Long id);

    CommunityDocumentRequest save(CommunityDocumentRequest communityDocumentRequest);

    void delete(Long id);

    void deleteRequestAndMember(Long id);

    void deleteRequestAndMember(Long communityId, Long userId);

    List<CommunityDocumentRequest> getByUserId(Long userId);

    List<CommunityDocumentRequest> getByCommunityId(Long communityId);

    CommunityDocumentRequest getByCommunityAndUser(Long communityId, Long userId);

    CommunityDocumentRequestsPage getByUserIdPage(Long userId, int page, int perPage);
}
