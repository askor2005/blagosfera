package ru.radom.kabinet.services.communities.log;

import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.community.log.CommunityLogEvent;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 04.04.2016.
 */
public interface CommunityLogEventDomainService {

    CommunityLogEvent getById(Long id);

    List<CommunityLogEvent> getByCommunityId(Long communityId);

    List<CommunityLogEvent> find(Long communityId, Long userId, Date dateFrom, Date dateTo, CommunityEventType type, int page, int perPage);

    CommunityLogEvent save(CommunityLogEvent communityLogEvent);

    CommunityLogEvent delete(Long id);
}
