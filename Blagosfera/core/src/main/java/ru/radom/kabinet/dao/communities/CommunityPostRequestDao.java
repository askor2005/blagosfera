package ru.radom.kabinet.dao.communities;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.communities.CommunityPostEntity;
import ru.radom.kabinet.model.communities.postrequest.CommunityPostRequestEntity;

import java.util.List;

/**
 * Dao класс для работы с запросами на должности
 * Created by vgusev on 28.08.2015.
 */
@Repository("communityPostRequestDao")
public class CommunityPostRequestDao extends Dao<CommunityPostRequestEntity> {

    /**
     * Получить запросы на работу для участника объединения
     * @param member
     * @return
     */
    public List<CommunityPostRequestEntity> getList(CommunityMemberEntity member) {
        Criteria criteria = getCriteria().add(Restrictions.eq("receiver", member.getUser())).add(Restrictions.eq("community", member.getCommunity()));
        return find(criteria);
    }

    /**
     * Получить запрос по участнику объединения и по должности
     * @param member
     * @param communityPost
     * @return
     */
    public CommunityPostRequestEntity get(CommunityMemberEntity member, CommunityPostEntity communityPost) {
        Criteria criteria = getCriteria().add(Restrictions.eq("receiver", member.getUser())).add(Restrictions.eq("communityPost", communityPost));
        return (CommunityPostRequestEntity)criteria.uniqueResult();
    }
}
