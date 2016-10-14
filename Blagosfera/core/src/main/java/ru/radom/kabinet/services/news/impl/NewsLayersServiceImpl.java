package ru.radom.kabinet.services.news.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.discussion.DiscussionDomain;
import ru.askor.blagosfera.domain.news.NewsItem;
import ru.radom.kabinet.dao.discussion.CommentDao;
import ru.radom.kabinet.dao.rating.RatingDao;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.radom.kabinet.model.news.News;
import ru.radom.kabinet.services.news.NewsLayersService;

import java.util.Arrays;


/**
 * Реализация интерфейса NewsLayersService
 */
@Service("newsLayersService")
public class NewsLayersServiceImpl implements NewsLayersService {

    @Autowired
    private RatingDao ratingDao;

    @Autowired
    private CommentDao commentDao;


    @Override
    @Transactional(readOnly = true)
    public NewsItem makeDomainForSharer(News news, Long userId) {

        NewsItem result = news.toDomain();
        result.setDiscussion(makeDiscussionDomain(news.getDiscussion()));
        result.setRatingSum(ratingDao.sumWeights(news));
        result.setRatingWeight(ratingDao.getWeights(userId, Arrays.asList(news.getId()), News.class).get(news.getId()));

        return result;
    }

    private DiscussionDomain makeDiscussionDomain(Discussion discussion) {

        if (discussion == null) {
            return null;
        }

        DiscussionDomain result = discussion.toDomain();

        //Ищем последний комментарий
        CommentEntity lastCommentEntity = commentDao.getLast(discussion);

        if (lastCommentEntity != null) {
            result.setLastCommentDate(lastCommentEntity.getCreatedAt());
            result.setLastCommentAuthor(lastCommentEntity.getOwner().getShortName());
            result.setLastCommentAuthorLink(lastCommentEntity.getOwner().getLink());
        }

        return result;
    }
}
