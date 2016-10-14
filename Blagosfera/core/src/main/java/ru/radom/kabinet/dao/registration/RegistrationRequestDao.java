package ru.radom.kabinet.dao.registration;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.registration.RegistrationRequest;
import ru.radom.kabinet.model.registration.RegistrationRequestStatus;
import ru.radom.kabinet.utils.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository("registrationRequestDao")
public class RegistrationRequestDao extends Dao<RegistrationRequest> {


    public Long create(final LongIdentifiable object, final UserEntity registrator){
        return save(new RegistrationRequest(object, registrator));
    }

    /*public boolean isExists(final Sharer user, final Sharer registrator){
        return count(Restrictions.eq("registrator", registrator), Restrictions.eq("user", user), Restrictions.eq("status", RegistrationRequestStatus.NEW)) > 0;
    }*/

    public RegistrationRequest getRequestByObject(final LongIdentifiable object) {
        return findFirst(Restrictions.eq("object", object), Restrictions.eq("status", RegistrationRequestStatus.NEW));
    }
    
    /*public RegistrationRequest getUserRequest(final Sharer user){
        return findFirst(Restrictions.eq("user", user), Restrictions.eq("status", RegistrationRequestStatus.NEW));
    }*/

    // Запрос на поиск заявое на сертификацию от пользователей
    private static final String GET_USER_REQUEST_IDS =
            "select req.id " +
            "   from registration_requests req " +
            "   join sharers s on req.object_id = s.id " +
            "where " +
            "   s.verified != true and " +
            "   req.object_type = :object_type and " +
            "   req.registrator_id = :user_id and " +
            "   lower(s.search_string) like lower(:search_string) ";


    /**
     * Список запросов на регистрацию от пользователей системы
     * @param registrator
     * @param nameTemplate
     * @param orderBy
     * @param asc
     * @param offset
     * @param limit
     * @param status
     * @return
     */
    public List<RegistrationRequest> searchUserRequests(final UserEntity registrator, final String nameTemplate, final String orderBy, final boolean asc, final int offset, final int limit, final RegistrationRequestStatus status ) {
        // Сделано через нативный SQL потому как на AnyMetaDef поле не создаётся алиас для поиска по полям
        String orderStr = "order by ";
        if ("search_string".equals(orderBy)) {
            orderStr += "s.search_string ";
        } else if ("created".equals(orderBy)) {
            orderStr += "req.created ";
        }
        if (asc) {
            orderStr += "asc";
        } else {
            orderStr += "desc";
        }
        String limitOffsetStr = " limit " + limit + " offset " + offset;

        Query query = createSQLQuery(GET_USER_REQUEST_IDS + orderStr + limitOffsetStr);
        query.setLong("user_id", registrator.getId());
        query.setString("object_type", Discriminators.SHARER);
        query.setString("search_string", "%" + (nameTemplate == null ? "" : nameTemplate) + "%");
        List<BigInteger> bigIntegers = query.list();

        List<RegistrationRequest> result = new ArrayList<>();
        for (BigInteger bigInteger : bigIntegers) {
            Criteria criteria = getCriteria();
            criteria.add(Restrictions.eq("id", bigInteger.longValue()));
            if (status != null) criteria.add(Restrictions.eq("status", status));
            RegistrationRequest request = (RegistrationRequest)criteria.uniqueResult();
            if (request != null) {
                result.add(request);
            }
        }
        return result;
    }

    // Запрос на поиск заявое на сертификацию от организаций
    private static final String GET_COMMUNITY_REQUEST_IDS =
            "select req.id " +
            "   from registration_requests req " +
            "   join communities c on req.object_id = c.id " +
            "where " +
            "   req.object_type = :object_type and " +
            "   req.registrator_id = :user_id and " +
            "   lower(c.name) like lower(:search_string) ";

    /**
     * Фильтрация запросов от организаций
     * @param registrator
     * @param nameTemplate
     * @param orderBy
     * @param asc
     * @param offset
     * @param limit
     * @param status
     * @return
     */
    public List<RegistrationRequest> searchCommunityRequests(final UserEntity registrator, final String nameTemplate, final String orderBy, final boolean asc, final  int offset, final int limit, final RegistrationRequestStatus status ) {
        // Сделано через нативный SQL потому как на AnyMetaDef поле не создаётся алиас для поиска по полям
        String orderStr = "order by ";
        if ("search_string".equals(orderBy)) {
            orderStr += "c.name ";
        } else if ("created".equals(orderBy)) {
            orderStr += "req.created ";
        }
        if (asc) {
            orderStr += "asc";
        } else {
            orderStr += "desc";
        }
        String limitOffsetStr = " limit " + limit + " offset " + offset;

        Query query = createSQLQuery(GET_COMMUNITY_REQUEST_IDS + orderStr + limitOffsetStr);
        query.setLong("user_id", registrator.getId());
        query.setString("object_type", Discriminators.COMMUNITY);
        query.setString("search_string", "%" + (nameTemplate == null ? "" : nameTemplate) + "%");
        List<BigInteger> bigIntegers = query.list();

        List<RegistrationRequest> result = new ArrayList<>();
        for (BigInteger bigInteger : bigIntegers) {
            Criteria criteria = getCriteria();
            criteria.add(Restrictions.eq("id", bigInteger.longValue()));
            if (status != null) criteria.add(Restrictions.eq("status", status));
            RegistrationRequest request = (RegistrationRequest)criteria.uniqueResult();
            if (request != null) {
                result.add(request);
            }
        }
        return result;
    }

    /**
     * Количество запросов на регистрацию
     * @param registrator
     * @param status
     * @param className
     * @return
     */
    public Integer count(final UserEntity registrator, final RegistrationRequestStatus status, String className) {
        final Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("registrator", registrator));
        criteria.add(Restrictions.eq("object.class", className));
        if (status != null) criteria.add(Restrictions.eq("status", status));

        return ((Long) criteria.setProjection(Projections.count("id")).uniqueResult()).intValue();
    }

    public void updateStatus(final RegistrationRequest registrationRequest, final RegistrationRequestStatus status, final String comment){
        if(status != null){
            registrationRequest.setStatus(status);
            registrationRequest.setUpdated(new Date());
            if(!StringUtils.isEmpty(comment)) registrationRequest.setComment(comment);
            update(registrationRequest);
        }
    }


}
