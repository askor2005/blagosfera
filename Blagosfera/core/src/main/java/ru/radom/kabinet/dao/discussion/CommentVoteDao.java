package ru.radom.kabinet.dao.discussion;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.discussion.CommentVote;

@Repository("commentVoteDao")
public class CommentVoteDao extends Dao<CommentVote> {
	
	public CommentVote findBySharerAndComment(final UserEntity user, final CommentEntity commentEntity) {
		return findFirst(Restrictions.eq("user", user), Restrictions.eq("comment", commentEntity));
	}
}
