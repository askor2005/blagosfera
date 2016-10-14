package ru.radom.kabinet.services.rating;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.user.RatingEvent;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.dao.DaoManager;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.rating.RatingDao;
import ru.radom.kabinet.dto.StringObjectHashMap;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.rating.Ratable;
import ru.radom.kabinet.model.rating.Rating;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Scope("singleton")
@Service("ratingService")
public class RatingService {

    private final static String POSITIVE_DIRECTION = "+";

    public final static String COUNT_KEY = "count";

    public final static String RATING_KEY = "rating";

    public final static String ME_KEY = "me";

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Autowired
    private DaoManager daoManager;

    @Autowired
    private RatingDao ratingDao;

    @Autowired
    private SharerDao sharerDao;

    public Ratable getContent(final Long contentId, final String contentType) {
        Ratable content = null;
        if(StringUtils.isEmpty(contentType)) throw new IllegalArgumentException("content must be specified");
        final Class daoClass = Discriminators.getClass(contentType);
        final Dao<? extends LongIdentifiable> dao = daoManager.getDao(daoClass);
        if(dao != null){
            content = (Ratable)dao.getById(contentId);
        }
        if (content == null) throw new IllegalArgumentException(
                String.format("ratable content with id=[%s], type=[%s] not exists", contentId, contentType));
        return content;
    }

    public Rating saveRating(final Long contentId, final String contentType, Long userId, final String direction) {
        final boolean positive = POSITIVE_DIRECTION.equals(direction);
        final Ratable content = getContent(contentId, contentType);
        final Rating last = ratingDao.getByAuthorLast(SecurityUtils.getUser().getId(), content);
        if (last != null) {
            // return last rating in case of nothing changed
            if ((last.getWeight() > 0) == positive) {
                return last;
            }
            last.setDeleted(true);
            ratingDao.update(last);
        }
        Rating rating = new Rating();
        rating.setContent(content);
        rating.setCreated(new Date());
        rating.setUser(sharerDao.getById(userId));
        rating.setDeleted(false);
        final double weight = 1;
        rating.setWeight(positive ? weight : -weight);
        ratingDao.save(rating);
        return rating;
    }

    public Map countMultiple(final List<Long> contentIds, final String contentType) {
        final Map<String, Object> result = new StringObjectHashMap();
        for (Long id : contentIds) {
            final Map<String, Object> ratingMap = new StringObjectHashMap();
            final Ratable content = getContent(id, contentType);
            Rating rating = ratingDao.getByAuthorLast(SecurityUtils.getUser().getId(), content);
            final Number count = sumWeights(id, contentType);
            ratingMap.put(COUNT_KEY, count.intValue());
            if (rating != null) {
                ratingMap.put(ME_KEY, rating.getWeight().intValue());
            }
            result.put(contentType + id.toString(), ratingMap);
        }

        return result;
    }

    public Number sumWeights(final Long contentId, final String contentType) {
        final Ratable content = getContent(contentId, contentType);
        final Number count = (Number)ratingDao.sumWeights(content);
        return (count == null) ? 0 : count;
    }

    public Number sumWeights(final Ratable content) {
        if(content == null) throw new IllegalArgumentException("content must be specified");
        final Number count = (Number)ratingDao.sumWeights(content);
        return (count == null) ? 0 : count;
    }

    public Rating getLast(Long userId, final Ratable content) {
        return ratingDao.getByAuthorLast(userId, content);
    }

    public void updateRating(final Rating rating, final Number count){
        blagosferaEventPublisher.publishEvent(new RatingEvent(this, rating, count.intValue()));
    }

    public Integer count(final Long contentId, final String contentType, final String userNamePattern, final Date fromDate,
                         final Date toDate, final Double weight, final Boolean onlyActive) {
        final Ratable content = getContent(contentId, contentType);

        return ratingDao.count(content, userNamePattern, fromDate, toDate, weight, onlyActive);
    }

    public List<Rating> page(final Integer offset, final Integer limit, final Long contentId, final String contentType,
                              final String userNamePattern, final Date fromDate, final Date toDate, final Double weight, final Boolean onlyActive) {
        final Ratable content = getContent(contentId, contentType);
        if((offset == null) || offset < 0) throw new IllegalArgumentException("offset must be specified and >= 0");
        if((limit == null) || limit < 0) throw new IllegalArgumentException("limit must be specified and >= 0");
        return ratingDao.find(offset, limit, content, userNamePattern, fromDate, toDate, weight, onlyActive);
    }

    public Map countUsers(final Long contentId, final String contentType) {
        final Map<String, Object> result = new StringObjectHashMap();
        final Ratable content = getContent(contentId, contentType);
        final Integer count = ratingDao.countUsers(content);
        final Rating last = ratingDao.getByAuthorLast(SecurityUtils.getUser().getId(), content);
        result.put("count", count);
        result.put(ME_KEY,  last != null ? last.toDomain() : null);
        return result;
    }

    public Map<Long, Double> sumWeights(final List<Long> ids, final Class contentClass){
        return ratingDao.sumWeights(ids, contentClass);
    }

    public void appendToModel(final Model model,final Ratable content){
        model.addAttribute("ratingSum", this.sumWeights(content).longValue());
        final Rating rootRating = getLast(SecurityUtils.getUser().getId(), content);
        model.addAttribute("ratingWeight", (rootRating == null) ? 0L : rootRating.getWeight());
    }
}
