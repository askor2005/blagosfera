package ru.radom.kabinet.dao.rating;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.rating.Ratable;
import ru.radom.kabinet.model.rating.Rating;
import ru.radom.kabinet.utils.StringUtils;

import java.math.BigInteger;
import java.util.*;

@Repository("ratingDao")
public class RatingDao extends Dao<Rating> {

    public List<Rating> getByAuthor(UserEntity user) {
        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("user", user));
        return find(conjunction);
    }

    public Rating getByAuthorLast(Long userId, final Ratable content) {
        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("user.id", userId));
        conjunction.add(Restrictions.eq("content", content));
        conjunction.add(Restrictions.eq("deleted", false));
        return findFirst(Order.desc("created"), conjunction);
    }

    public Double sumWeights(final Ratable content) {
        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("deleted", false));
        conjunction.add(Restrictions.eq("content", content));
        return (Double) aggregate(Projections.sum("weight"), conjunction);
    }

    private Criterion getCriterionForPage(final Ratable content, final String userNamePattern, final Date fromDate,
                                          final Date toDate, final Double weight, final Boolean onlyActive) {
        final Conjunction conjunction = new Conjunction();
        if (onlyActive) conjunction.add(Restrictions.eq("deleted", false));
        if ((userNamePattern != null) && !userNamePattern.isEmpty())
            conjunction.add(Restrictions.ilike("userAlias.searchString", userNamePattern, MatchMode.ANYWHERE));
        if (fromDate != null) conjunction.add(Restrictions.ge("created", fromDate));
        if (toDate != null) conjunction.add(Restrictions.lt("created", toDate));
        if (weight > 0) conjunction.add(Restrictions.gt("weight", 0d));
        if (weight < 0) conjunction.add(Restrictions.lt("weight", 0d));
        conjunction.add(Restrictions.eq("content", content));
        return conjunction;
    }

    public Integer count(final Ratable content, final String userNamePattern, final Date fromDate,
                         final Date toDate, final Double weight, final Boolean onlyActive) {
        Criteria criteria = getCriteria();
        criteria.createAlias("user", "userAlias");
        criteria.add(getCriterionForPage(content, userNamePattern, fromDate, toDate, weight, onlyActive));

        return ((Long) criteria.setProjection(Projections.count("id")).uniqueResult()).intValue();
    }

    public List<Rating> find(final Integer offset, final Integer limit, final Ratable content, final String userNamePattern, final Date fromDate,
                             final Date toDate, final Double weight, final Boolean onlyActive) {
        Criteria criteria = getCriteria();
        criteria.createAlias("user", "userAlias");
        criteria.add(getCriterionForPage(content, userNamePattern, fromDate, toDate, weight, onlyActive));
        criteria.setFirstResult(offset);
        criteria.setMaxResults(limit);
        return find(criteria);
    }

    public Integer countUsers(final Ratable content) {
        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("deleted", false));
        conjunction.add(Restrictions.eq("content", content));
        return count(conjunction);
    }

    public Map<Long, Double> sumWeights(final List<Long> ids, final Class contentClass){
        if(contentClass == null) throw new IllegalArgumentException("content type is unknown");
        if(!Ratable.class.isAssignableFrom(contentClass)) throw new IllegalArgumentException("content type is not ratable");
        if((ids == null) || ids.isEmpty()) return Maps.newHashMap();

        final String contentType = Discriminators.get(contentClass);
        if(StringUtils.isEmpty(contentType)) throw new IllegalArgumentException(String.format("discriminators for class [%s] undefined", contentClass.getCanonicalName()));

        final List results = createSQLQuery(String.format("SELECT content_id, SUM(weight) FROM ratings WHERE content_id in (%s) AND content_type = '%s' GROUP BY content_id",
                Joiner.on(",").join(ids), contentType)).list();
        final Map<Long, Double> resultMap = new HashMap();
        if(results != null) {
            for(Iterator iterator = results.iterator(); iterator.hasNext();){
                final Object[] row = (Object[]) iterator.next();
                resultMap.put(((BigInteger) row[0]).longValue(), (Double) row[1]);

            }
        }
        return resultMap;
    }

    public Map<Long, Double> getWeights(Long userId, final List<Long> ids, final Class contentClass){
        if(userId == null) return Maps.newHashMap();
        if(contentClass == null) throw new IllegalArgumentException("content type is unknown");
        if(!Ratable.class.isAssignableFrom(contentClass)) throw new IllegalArgumentException("content type is not ratable");
        if((ids == null) || ids.isEmpty()) return Maps.newHashMap();

        final String contentType = Discriminators.get(contentClass);
        if(StringUtils.isEmpty(contentType)) throw new IllegalArgumentException(String.format("discriminators for class [%s] undefined", contentClass.getCanonicalName()));

        final List results = createSQLQuery(String.format("SELECT content_id, weight, MAX(created) FROM ratings WHERE deleted = false AND content_id in (%s) AND content_type = '%s' AND user_id = %s GROUP BY content_id, weight",
                Joiner.on(",").join(ids), contentType, userId)).list();
        final Map<Long, Double> resultMap = new HashMap();
        if(results != null) {
            for(Iterator iterator = results.iterator(); iterator.hasNext();){
                final Object[] row = (Object[]) iterator.next();
                resultMap.put(((BigInteger) row[0]).longValue(), (Double) row[1]);

            }
        }
        return resultMap;
    }
}
