package ru.radom.kabinet.services.discuss;

import ru.askor.blagosfera.domain.discussion.DiscussionDomain;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.discussion.CommentVote.Vote;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.radom.kabinet.services.discuss.netexchange.NewCommentMessage;
import ru.radom.kabinet.web.discuss.DiscussionForm;

import java.util.List;


public interface DiscussionService {
    Discussion createDiscussion(DiscussionBuilder builder);
    CommentEntity getCommentById(Long id);
    void removeDiscussion(Discussion discussion);
    List<Discussion> getAllDiscussions();
    List<Discussion> getDiscussionsForCurrentUser();
    List<Discussion> getDiscussions(UserEntity userEntity);
    CommentEntity addComment(Long userId, String message, CommentEntity parent);
    List<CommentEntity> getComments(CommentEntity parent);
    List<CommentEntity> getComments(Discussion discussion);
    List<CommentsTreeQueryResult> getCommentsTree(Discussion discussion, int start, int limit);
    List<CommentsTreeQueryResult> getCommentsTree(Discussion discussion);
    void processNewCommentMessage(Long discussion, NewCommentMessage message, User commentOwner);
    String discussionTopic(Discussion discussion);
    String discussionTopicName(Discussion discussion);
	List<Discussion> getDiscussionsForScope(RadomAccount scope);
	Discussion createDiscussion(DiscussionForm form, UserEntity userEntity, RadomAccount scope);
	void prepareComments(Discussion discussion);
	boolean hasAccess(UserEntity userEntity, Discussion discussion);
	void commentVote(CommentEntity commentEntity, Vote vote, UserEntity userEntity);
	void editComment(Long commentId, String comment);
    List<CommentEntity> getCommentsByIds(List<Long> ids);

    /**
     * Позволяет заполнить объект DiscussionDomain информацией о последнем коментарии
     * и вернуть заполненный Discussion объект
     * @param discussion
     * @return
     */
    DiscussionDomain fillLastCommentInfo(Discussion discussion);
}
