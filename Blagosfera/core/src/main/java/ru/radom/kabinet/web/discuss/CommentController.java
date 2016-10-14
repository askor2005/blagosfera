package ru.radom.kabinet.web.discuss;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.discuss.CommentsTreeQueryResult;
import ru.radom.kabinet.services.discuss.DiscussionService;
import ru.radom.kabinet.services.discuss.netexchange.CommentInfoMessage;
import ru.radom.kabinet.services.rating.RatingService;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/discuss/comment")
public class CommentController {

    private final static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private SerializationManager serializationManager;

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public
    @ResponseBody
    String addComment(
            Model model,
            @RequestParam(value = "parent", required = true) CommentEntity parent,
            @RequestParam(value = "message", required = true) String message
    ) {
        logger.debug("addNewComment:parent={}", parent.getId());
        CommentEntity commentEntity = discussionService.addComment(SecurityUtils.getUser().getId(), message, parent);

        return serializationManager.serialize(commentEntity).toString();
    }


    @RequestMapping(value = "/get/{commentEntity}", method = RequestMethod.GET)
    @ResponseBody
    public CommentInfoMessage getComment(
            @PathVariable CommentEntity commentEntity
    ) {

        return new CommentInfoMessage(commentEntity);
    }

    /**
     * Вернуть список комментариев со всей необходимой для отображения на странице информацией
     *
     * @param discussion
     * @param start
     * @param limit
     * @return список из CommentInfoMessage
     * @see CommentInfoMessage
     */
    @RequestMapping(value = "/list/{discussion}", method = RequestMethod.GET)
    @ResponseBody
    public List<CommentInfoMessage> getComments(
            @PathVariable Discussion discussion,
            @RequestParam int start,
            @RequestParam int limit
    ) {

        List<CommentsTreeQueryResult> commentsPlainTree = discussionService.getCommentsTree(discussion, start, limit);
        final List<Long> ids = FluentIterable.from(commentsPlainTree)
                .transform(new Function<CommentsTreeQueryResult, Long>() {
                    @Override
                    public Long apply(CommentsTreeQueryResult c) {
                        return c.getId().longValue();
                    }
                }).toList();
        final List<CommentEntity> commentEntities = discussionService.getCommentsByIds(ids);
        final Map<Long, CommentEntity> mappedComments = Maps.uniqueIndex(commentEntities, new Function<CommentEntity, Long>() {
            public Long apply(final CommentEntity c) {
                return c.getId();
            }
        });
        List<CommentInfoMessage> result = new LinkedList<>();
        for (CommentsTreeQueryResult c : commentsPlainTree) {
            CommentInfoMessage infoMessage = new CommentInfoMessage(mappedComments.get(c.getId().longValue())          );
            BigInteger insertAfter = c.getInsertAfter();
            if (insertAfter != null) {
                infoMessage.setInsertAfter(insertAfter.longValue());
            }
            infoMessage.setRatingSum(c.getRatingSum());
            infoMessage.setRatingWeight(c.getRatingWeight());
            result.add(infoMessage);
        }

        return result;
    }
}
