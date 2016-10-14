package ru.radom.kabinet.json;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.discussion.CommentDao;
import ru.radom.kabinet.dao.rating.RatingDao;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.radom.kabinet.model.news.News;
import ru.radom.kabinet.security.SecurityUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component("newsCollectionSerializer")
public class NewsCollectionSerializer extends AbstractCollectionSerializer<News> {

	@Autowired
	private CommentDao commentDao;

    @Autowired
    private RatingDao ratingDao;
	
	@Autowired
	private NewsSerializer newsSerializer;

	@Override
	public JSONArray serializeInternal(Collection<News> collection) {
		JSONArray jsonArray = new JSONArray();
		
		List<Discussion> discussions = new ArrayList<Discussion>();
		for (News news : collection) {
			discussions.add(news.getDiscussion());
		}

		final Map<Discussion, CommentEntity> lastCommentsMap = commentDao.getLasts(discussions);
        final List<Long> ids = FluentIterable.from(collection)
                .transform(new Function<News, Long>() {
                    @Override
                    public Long apply(News news) {
                        return news.getId();
                    }
                }).toList();
        final Map<Long, Double> ratingsSumWeight = ratingDao.sumWeights(ids, News.class);
        final Map<Long, Double> ratingsWeights = ratingDao.getWeights(SecurityUtils.getUser().getId(), ids, News.class);

		for (News news : collection) {
			jsonArray.put(newsSerializer.serializeSingle(news, news.getDiscussion(), lastCommentsMap.get(news.getDiscussion()), ratingsSumWeight.get(news.getId()), ratingsWeights.get(news.getId())));
		}
		
		return jsonArray;
	}


}
