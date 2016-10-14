package ru.askor.blagosfera.core.services.community.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.log.CommunityVisitLog;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.sharer.UserDataService;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * Created by vtarasenko on 14.07.2016.
 */
@Service
@Transactional
public class CommunityVisitLogServiceImpl implements CommunityVisitLogService {
    @Autowired
    private CommunityDataService communityDataService;
    @Autowired
    private UserDataService userDataService;
    @Autowired
    private CommunityVisitLogDataService communityVisitLogDataService;
    @Override
    public void createCommunityVisitLog(String seolink, Long userId){
        Community community = communityDataService.getBySeoLinkOrIdMediumData(seolink);
        User user = userDataService.getByIdMinData(userId);
        assert community != null;
        assert user != null;
        CommunityVisitLog communityVisitLog = new CommunityVisitLog();
        communityVisitLog.setUser(user);
        communityVisitLog.setCommunity(community);
        communityVisitLog.setVisitTime(new Date());
        communityVisitLogDataService.save(communityVisitLog);
    }
}
