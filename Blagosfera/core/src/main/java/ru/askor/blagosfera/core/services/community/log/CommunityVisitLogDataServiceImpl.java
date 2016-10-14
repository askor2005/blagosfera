package ru.askor.blagosfera.core.services.community.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.data.jpa.entities.community.CommunityVisitLogEntity;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityVisitLogRepository;
import ru.askor.blagosfera.domain.community.log.CommunityVisitLog;

import javax.transaction.Transactional;

/**
 * Created by vtarasenko on 14.07.2016.
 */
@Service
@Transactional
public class CommunityVisitLogDataServiceImpl implements CommunityVisitLogDataService {
    @Autowired
    private CommunityVisitLogRepository communityVisitLogRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Override
    public CommunityVisitLog save(CommunityVisitLog communityVisitLog){
        CommunityVisitLogEntity communityVisitLogEntity = null;
        if (communityVisitLog.getId() == null) {
            communityVisitLogEntity = new CommunityVisitLogEntity();
        }
        else {
            communityVisitLogEntity = communityVisitLogRepository.findOne(communityVisitLog.getId());
        }
        communityVisitLogEntity.setVisitTime(communityVisitLog.getVisitTime());
        communityVisitLogEntity.setUser(userRepository.findOne(communityVisitLog.getUser().getId()));
        communityVisitLogEntity.setCommunity(communityRepository.findOne(communityVisitLog.getCommunity().getId()));
        return communityVisitLogRepository.save(communityVisitLogEntity).toDomain();
    }
    @Override
    public void delete(Long id) {
        communityVisitLogRepository.delete(id);
    }
}
