package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.discussion.CommentDao;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.radom.kabinet.utils.DateUtils;

@Component("discussionSerializer")
public class DiscussionSerializer extends AbstractSerializer<Discussion> {

	@Autowired
	private CommentDao commentDao;

	public JSONObject serializeSingle(Discussion discussion, CommentEntity lastCommentEntity) {
		if (discussion == null) {
			return null;
		} else {
			final JSONObject json = new JSONObject();
			json.put("id", discussion.getId());
			json.put("title", discussion.getTitle());
			if (lastCommentEntity != null) {
				// json.put("commentsCount", commentDao.countByDiscussion(d));
				json.put("commentsCount", discussion.getCommentsCount());
				json.put("lastCommentAuthor", lastCommentEntity.getOwner().getShortName());
				json.put("lastCommentAuthorLink", lastCommentEntity.getOwner().getLink());
				json.put("lastCommentDate", DateUtils.formatDate(lastCommentEntity.getCreatedAt(), DateUtils.Format.DATE_TIME_SHORT));
			}
			return json;
		}
	}

	@Override
	public JSONObject serializeInternal(Discussion discussion) {
		return serializeSingle(discussion, commentDao.getLast(discussion));
	}

}
