package ru.askor.blagosfera.core.services.community.log;

import ru.askor.blagosfera.domain.community.log.CommunityVisitLog;

/**
 * Created by vtarasenko on 14.07.2016.
 */
public interface CommunityVisitLogDataService {
    CommunityVisitLog save(CommunityVisitLog communityVisitLog);

    void delete(Long id);
}
