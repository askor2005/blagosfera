package ru.radom.kabinet.services.communities.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityLogEventRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.repositories.news.NewsRepository;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.community.log.CommunityLogEvent;
import ru.askor.blagosfera.domain.community.log.CommunityMemberLogEvent;
import ru.askor.blagosfera.domain.community.log.CommunityNewsLogEvent;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityLogEventEntity;
import ru.radom.kabinet.model.communities.CommunityMemberLogEventEntity;
import ru.radom.kabinet.model.communities.CommunityNewsLogEventEntity;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 04.04.2016.
 */
@Transactional
@Service
public class CommunityLogEventDomainServiceImpl implements CommunityLogEventDomainService {

    @Autowired
    private CommunityLogEventRepository communityLogEventRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private NewsRepository newsRepository;

    @Override
    public CommunityLogEvent getById(Long id) {
        return CommunityLogEventEntity.toDomainSafe(communityLogEventRepository.findOne(id));
    }

    @Override
    public List<CommunityLogEvent> getByCommunityId(Long communityId) {
        return CommunityLogEventEntity.toDomainList(communityLogEventRepository.findByCommunity_Id(communityId));
    }

    @Override
    public List<CommunityLogEvent> find(Long communityId, Long userId, Date dateFrom, Date dateTo, CommunityEventType type, int page, int perPage) {
        Pageable pageable = new PageRequest(page, perPage);
        List<CommunityLogEventEntity> data = null;
        if (userId != null && type != null) {
            data = communityLogEventRepository.findByCommunity_IdAndUserEntity_IdAndDateBetweenAndTypeOrderByDateDesc(communityId, userId, dateFrom, dateTo, type, pageable);
        } else if (userId == null && type != null) {
            data = communityLogEventRepository.findByCommunity_IdAndDateBetweenAndTypeOrderByDateDesc(communityId, dateFrom, dateTo, type, pageable);
        } else if (userId == null && type == null) {
            data = communityLogEventRepository.findByCommunity_IdAndDateBetweenOrderByDateDesc(communityId, dateFrom, dateTo, pageable);
        }
        return CommunityLogEventEntity.toDomainList(data);
    }

    @Override
    public CommunityLogEvent save(CommunityLogEvent communityLogEvent) {
        CommunityLogEventEntity entity = null;
        if (communityLogEvent.getId() == null) {
            if (communityLogEvent instanceof CommunityMemberLogEvent) {
                entity = new CommunityMemberLogEventEntity();
            } else if (communityLogEvent instanceof CommunityNewsLogEvent) {
                entity = new CommunityNewsLogEventEntity();
            } else {
                entity = new CommunityLogEventEntity();
            }
        } else {
            entity = communityLogEventRepository.findOne(communityLogEvent.getId());
        }

        ExceptionUtils.check(
                communityLogEvent.getCommunity() == null || communityLogEvent.getCommunity().getId() == null,
                "Не установлен ИД объединения"
        );
        ExceptionUtils.check(
                communityLogEvent.getUser() == null || communityLogEvent.getUser().getId() == null,
                "Не установлен ИД пользователя"
        );

        CommunityEntity communityEntity = communityRepository.getOne(communityLogEvent.getCommunity().getId());
        UserEntity userEntity = sharerDao.loadById(communityLogEvent.getUser().getId());

        entity.setType(communityLogEvent.getType());
        entity.setCommunity(communityEntity);
        entity.setUserEntity(userEntity);
        entity.setDate(communityLogEvent.getDate());

        if (entity instanceof CommunityNewsLogEventEntity) {
            CommunityNewsLogEvent communityNewsLogEvent = (CommunityNewsLogEvent)communityLogEvent;
            CommunityNewsLogEventEntity communityNewsLogEventEntity = (CommunityNewsLogEventEntity) entity;
            if (communityNewsLogEvent.getNews() != null && communityNewsLogEvent.getNews().getId() != null) {
                communityNewsLogEventEntity.setNews(newsRepository.getOne(communityNewsLogEvent.getNews().getId()));
            }
        } else if (entity instanceof CommunityMemberLogEventEntity) {
            CommunityMemberLogEventEntity communityMemberLogEventEntity = (CommunityMemberLogEventEntity) entity;
            CommunityMemberLogEvent communityNewsLogEvent = (CommunityMemberLogEvent)communityLogEvent;
            if (communityNewsLogEvent.getMemberUser() != null && communityNewsLogEvent.getMemberUser().getId() != null) {
                communityMemberLogEventEntity.setMemberUserEntity(sharerDao.loadById(communityNewsLogEvent.getMemberUser().getId()));
            }
        }

        entity = communityLogEventRepository.save(entity);
        return CommunityLogEventEntity.toDomainSafe(entity);
    }

    @Override
    public CommunityLogEvent delete(Long id) {
        CommunityLogEventEntity entity = communityLogEventRepository.findOne(id);
        CommunityLogEvent result = CommunityLogEventEntity.toDomainSafe(entity);
        communityLogEventRepository.delete(id);
        return result;
    }
}
