package ru.radom.kabinet.services.discuss;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.discussion.DiscussionDomain;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.news.CommentEvent;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.discussion.CommentDao;
import ru.radom.kabinet.dao.discussion.CommentVoteDao;
import ru.radom.kabinet.dao.discussion.DiscussionDao;
import ru.radom.kabinet.dao.discussion.DiscussionTopicDao;
import ru.radom.kabinet.dao.news.NewsDao;
import ru.radom.kabinet.dao.rating.RatingDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.discussion.CommentVote;
import ru.radom.kabinet.model.discussion.CommentVote.Vote;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.radom.kabinet.model.discussion.DiscussionTopic;
import ru.radom.kabinet.model.news.News;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.StompService;
import ru.radom.kabinet.services.discuss.netexchange.CommentInfoMessage;
import ru.radom.kabinet.services.discuss.netexchange.NewCommentMessage;
import ru.radom.kabinet.web.discuss.DiscussionForm;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("discussionService")
public class DiscussionServiceImpl implements DiscussionService {

	private final static Logger logger = LoggerFactory.getLogger(DiscussionServiceImpl.class);

	@Autowired
	private StompService stompService;

	@Autowired
	private DiscussionDao discussionDao;

	@Autowired
	private CommentDao commentDao;

	@Autowired
	private DiscussionTopicDao discussionTopicDao;

	@Autowired
	private CommentVoteDao commentVoteDao;

	@Autowired
	private SharerDao sharerDao;

	@Autowired
	private NewsDao newsDao;

    @Autowired
    private RatingDao ratingDao;

	@Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Discussion createDiscussion(DiscussionBuilder builder) {
		Discussion discussion = builder.build();
		discussionDao.saveOrUpdate(discussion);

		return discussion;
	}

	public List<CommentEntity> getComments(CommentEntity parent) {
		return commentDao.getChildren(parent);
	}

	public List<CommentEntity> getComments(Discussion discussion) {
		return getComments(discussion.getRoot());
	}

	@Override
	public List<CommentsTreeQueryResult> getCommentsTree(Discussion discussion, int startSince, int limit) {
		return calculateRatings(commentDao.getCommentsTree(discussion, startSince, limit));
	}

	@Override
	public List<CommentsTreeQueryResult> getCommentsTree(Discussion discussion) {
		return calculateRatings(commentDao.getCommentsTree(discussion));
	}

    private List<CommentsTreeQueryResult> calculateRatings(final List<CommentsTreeQueryResult> commentsPlainTree) {
        final List<Long> ids = FluentIterable.from(commentsPlainTree)
                .transform(new Function<CommentsTreeQueryResult, Long>() {
                    @Override
                    public Long apply(CommentsTreeQueryResult c) {
                        return c.getId().longValue();
                    }
                }).toList();
        if(!ids.isEmpty()){
            final Map<Long, Double> ratingsSumWeight = ratingDao.sumWeights(ids, CommentEntity.class);
            final Map<Long, Double> ratingsWeights = ratingDao.getWeights(SecurityUtils.getUser().getId(), ids, CommentEntity.class);
            for (CommentsTreeQueryResult c : commentsPlainTree) {
                c.setRatingSum(ratingsSumWeight.get(c.getId().longValue()));
                c.setRatingWeight(ratingsWeights.get(c.getId().longValue()));
            }
        }
        return commentsPlainTree;
    }

	@Override
	public void processNewCommentMessage(Long discussionId, NewCommentMessage message, User commentOwnerUser) {
		CommentEntity parent = commentDao.getById(message.getParent());
		Discussion discussion = discussionDao.getById(discussionId);

		// TODO Переделать
		UserEntity commentOwner = sharerDao.getById(commentOwnerUser.getId());
		CommentEntity commentEntity = this.addComment(commentOwnerUser.getId(), message.getText(), parent);
		CommentInfoMessage infoMessage = new CommentInfoMessage(commentEntity);
		infoMessage.setInsertAfter(getInsertAfter(commentEntity)); // TODO: не совсем
																// корректно,
																// должно
																// вставляться
																// после
																// последнего
																// элемента
																// дерева
																// родительского
																// документа
		infoMessage.setIsOwn(commentOwner.equals(commentEntity.getOwner()));

		String discussionTopic = discussionTopic(discussion);
		logger.info("send message to:{}", discussionTopic);
		stompService.send(discussionTopic, infoMessage);

		// отправляем нотификацию только о комментариях первого уровня
		if (commentEntity.getParent().equals(discussion.getRoot())) {
			News linkedNews = newsDao.getByDiscussion(discussion);
			if (linkedNews != null) {
                blagosferaEventPublisher.publishEvent(new CommentEvent(this, linkedNews, commentEntity));
			}
		}
	}

	@Transactional
	private Long getInsertAfter(CommentEntity commentEntity) {
		CommentEntity insertAfter = commentEntity.getParent();
		// TODO: получаются спорные эффекты
		// while (insertAfter.getChildren().size() > 0) {
		// CommentEntity lastChild =
		// insertAfter.getChildren().get(insertAfter.getChildren().size() - 1);
		// if (lastChild.equals(commentEntity) && (insertAfter.getChildren().size() >
		// 2)) {
		// insertAfter =
		// insertAfter.getChildren().get(insertAfter.getChildren().size() - 2);
		// }
		// }

		return insertAfter.getId();
	}

	@Override
	public String discussionTopic(Discussion discussion) {
		return String.format("/topic/discuss_%d", discussion.getId());
	}

    @Override
    public String discussionTopicName(Discussion discussion) {
        return String.format("discuss_%d", discussion.getId());
    }

	@Override
	public CommentEntity getCommentById(Long id) {
		return commentDao.getById(id);
	}

	@Override
	public void removeDiscussion(Discussion discussion) {
		discussionDao.delete(discussion);
	}

	@Override
	public void prepareComments(Discussion discussion) {
		List<CommentEntity> commentEntities = commentDao.find(Restrictions.eq("parentDiscussion", discussion));
		for (CommentEntity c : commentEntities) {
			c.setParentDiscussion(null);
			commentDao.saveOrUpdate(c);
		}
		commentDao.flush();
	}

	@Override
	public List<Discussion> getAllDiscussions() {
		return discussionDao.discussionsAll();
	}

	@Override
	public List<Discussion> getDiscussionsForCurrentUser() {
		return this.getDiscussions(sharerDao.getById(SecurityUtils.getUser().getId()));
	}

	@Override
	public List<Discussion> getDiscussions(UserEntity userEntity) {
		return discussionDao.discussionsForAuthor(userEntity);
	}

	@Override
	public CommentEntity addComment(Long userId, String message, CommentEntity parent) {
        CommentEntity commentEntity = new CommentBuilder(sharerDao.getById(userId)).message(message).parent(parent).build();
		commentDao.save(commentEntity);
		return commentEntity;
	}

	@Override
	public List<Discussion> getDiscussionsForScope(RadomAccount scope) {
		return discussionDao.discussionsForScope(scope);
	}

	@Override
	public Discussion createDiscussion(DiscussionForm form, UserEntity author, RadomAccount scope) {
		final Discussion discussion = transform(form, author, scope);
		discussionDao.saveOrUpdate(discussion);
		return discussion;
	}

	private Discussion transform(DiscussionForm form, UserEntity author, RadomAccount scope) {
		final Long id = form.getId();
		final Discussion discussion;
		if (id == null) {
			discussion = new Discussion();
			final CommentEntity rootCommentEntity = new CommentEntity();
			rootCommentEntity.setOwner(author);
			rootCommentEntity.setMessage(form.getContent());
			rootCommentEntity.setParentDiscussion(discussion);
			discussion.setRoot(rootCommentEntity);
		} else {
			discussion = discussionDao.getById(id);
		}
		discussion.setAuthor(author);
		discussion.setScope(scope);

		final DiscussionTopic topic = discussionTopicDao.getById(form.getTopic());
		discussion.setTopic(topic);
		discussion.setTitle(form.getTitle());
		discussion.setRecommendations(form.getDescription());
		discussion.setTimeLimit(form.getTimeLimit());
		discussion.setCommentsLimit(form.getCommentsLimit());
		discussion.setMandatoryPeriod(calcMandatoryPeriod(form));
		discussion.setPubliclyCommentable(form.getPublicEvaluation());
        discussion.setPubliclyVisible(!form.getVisible());
        if (form.getParticipants() != null){
            discussion.getAllowedUserEntities().addAll(form.getParticipants());
        }
		if (form.getCommunities() != null) {
			discussion.getAllowedCommunities().addAll(form.getCommunities());
		}

		return discussion;
	}

	private Calendar calcMandatoryPeriod(DiscussionForm form) {
		int days = form.getMandatoryPeriod();
		if (days > 0) {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DAY_OF_MONTH, days);
			return now;
		} else {
			return null;
		}
	}

	@Override
	public boolean hasAccess(UserEntity userEntity, Discussion discussion) {
		Set<CommunityEntity> allowedCommunities = discussion.getAllowedCommunities();
		Set<UserEntity> allowedUserEntities = discussion.getAllowedUserEntities();
		if (discussion.getAuthor().equals(userEntity))
			return true;
		if (discussion.isPubliclyVisible() || (allowedCommunities.isEmpty() && allowedUserEntities.isEmpty()))
			return true;
		if (allowedUserEntities.contains(userEntity))
			return true;

		for (CommunityEntity c : allowedCommunities) {
			for (CommunityMemberEntity cm : c.getMembers()) {
				if (cm.getStatus() == CommunityMemberStatus.MEMBER && cm.getUser().equals(userEntity))
					return true;
			}
		}

		return false;

	}

	@Override
	@Transactional
	public void commentVote(CommentEntity commentEntity, Vote vote, UserEntity userEntity) {
		CommentVote commentVote = commentVoteDao.findBySharerAndComment(userEntity, commentEntity);
		if (commentVote == null) {
			commentVote = new CommentVote();
			commentVote.setSharer(userEntity);
			commentVote.setCommentEntity(commentEntity);
		}
		commentVote.setVote(vote);
		commentVoteDao.saveOrUpdate(commentVote);
	}

	@Override
	public void editComment(Long commentId, String comment) {
		final CommentEntity c = commentDao.getById(commentId);
		if (c != null) {
			c.setMessage(comment);
			commentDao.saveOrUpdate(c);
		}
	}

    @Override
    public List<CommentEntity> getCommentsByIds(List<Long> ids) {
        return commentDao.getByIds(ids);
    }

	@Override
	@Transactional(readOnly = true)
	public DiscussionDomain fillLastCommentInfo(Discussion discussion) {

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
