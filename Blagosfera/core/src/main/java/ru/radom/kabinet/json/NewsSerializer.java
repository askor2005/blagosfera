package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.discussion.CommentDao;
import ru.radom.kabinet.dao.rating.RatingDao;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.radom.kabinet.model.news.News;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.radom.kabinet.model.rating.Rating;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.DateUtils;

@Component("newsSerializer")
public class NewsSerializer extends AbstractSerializer<News> {

	@Autowired
	private SharerSerializer sharerSerializer;
	
	@Autowired
	private CommunitySerializer communitySerializer;
	
	@Autowired
	private DiscussionSerializer discussionSerializer;
	
	@Autowired
	private CommentDao commentDao;

	@Autowired
    private RatingDao ratingDao;
	
	public JSONObject serializeSingle(News news, Discussion discussion, CommentEntity lastCommentEntity, Double ratingSum, Double ratingWeight) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", news.getId());
		jsonObject.put("title", news.getTitle());
		jsonObject.put("link", news.getLink());
		jsonObject.put("authorType", news.getAuthor() instanceof UserEntity ? Discriminators.SHARER : Discriminators.SYSTEM_ACCOUNT);
		jsonObject.put("scopeType", news.getScope() instanceof UserEntity ? Discriminators.SHARER : Discriminators.COMMUNITY);
		
		RadomAccount author = news.getAuthor();
		if (author instanceof UserEntity) {
			jsonObject.put("author", sharerSerializer.serializeSingleSharer((UserEntity) news.getAuthor(), null));
		}
		
		RadomAccount scope = news.getScope();
		if (scope instanceof UserEntity) {
			jsonObject.put("scope", sharerSerializer.serializeSingleSharer((UserEntity) news.getScope(), null));
		} else if (scope instanceof CommunityEntity) {
			jsonObject.put("scope", communitySerializer.serialize((CommunityEntity) news.getScope()));
		}
		
		jsonObject.put("discussion", discussion == null ? null : discussionSerializer.serializeSingle(discussion, lastCommentEntity));
		
		jsonObject.put("date", DateUtils.formatDate(news.getDate(), "dd.MM.yyyy HH:mm:ss"));
		jsonObject.put("editDate", news.getEditDate() != null ? DateUtils.formatDate(news.getEditDate(), "dd.MM.yyyy HH:mm:ss") : null);
		
		jsonObject.put("editCount", news.getEditCount());
		
		jsonObject.put("text", news.getText());

        jsonObject.put("ratingSum", (ratingSum == null) ? 0 : ratingSum.longValue());
        jsonObject.put("ratingWeight", (ratingWeight == null) ? 0 : ratingWeight.longValue());

		return jsonObject;
	}
	
	@Override
	public JSONObject serializeInternal(News news) {
		Discussion discussion = news.getDiscussion();
        final Double ratingWeight = ratingDao.sumWeights(news);
        final Rating rating = ratingDao.getByAuthorLast(SecurityUtils.getUser().getId(), news);
		return serializeSingle(news, discussion, commentDao.getLast(discussion), ratingWeight, (rating == null) ? null : rating.getWeight());
	}

}
