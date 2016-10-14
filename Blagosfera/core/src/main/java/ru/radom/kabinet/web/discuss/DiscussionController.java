package ru.radom.kabinet.web.discuss;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.discussion.CommentDao;
import ru.radom.kabinet.dao.discussion.DiscussionTopicDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.discussion.CommentVote.Vote;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.radom.kabinet.model.discussion.DiscussionTopic;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.discuss.DiscussionService;
import ru.radom.kabinet.services.rating.RatingService;
import ru.radom.kabinet.utils.DiscussionUtils;
import ru.radom.kabinet.web.utils.Breadcrumb;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/discuss")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private SharerDao sharerDao;
    
    @Autowired
    private DiscussionTopicDao discussionTopicDao;
    
    @Autowired
	private CommentDao commentDao;

    @Autowired
    private RatingService ratingService;

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String showDiscussionForm(Model model) {
    	model.addAttribute("discussionForm", new DiscussionForm());
    	model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Создание обсуждения", "/discuss/new"));
        return "discussionNew";
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser(){
        return SecurityUtils.getUser();
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String createDiscussion(Model model, @ModelAttribute("discussionForm") DiscussionForm discussionForm, UserEntity userEntity) {
        final Discussion discussion = discussionService.createDiscussion(discussionForm, userEntity, userEntity);
        return "redirect:/discuss/view/" + discussion.getId();
    }

    @RequestMapping(value = "/view/{discussion}", method = RequestMethod.GET)
    public String createDiscussionForm(Model model, @PathVariable Discussion discussion, UserEntity userEntity, HttpServletResponse response) throws IOException {
        if(discussion == null) response.sendError(HttpServletResponse.SC_NOT_FOUND);
    	if(!discussionService.hasAccess(userEntity, discussion)) response.sendError(403);
        DiscussionUtils.attachDiscussion(model, discussion, discussionService, sharerDao);
        ratingService.appendToModel(model, discussion.getRoot());
        model.addAttribute("sharer", userEntity);
        model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Обсуждение " + discussion.getTitle(), "/discuss/view/" + discussion.getId()));
        return "discussionView";
    }

    @RequestMapping("/my")
    public String myDiscussions(Model model) {
        List<Discussion> list = discussionService.getDiscussionsForCurrentUser();
        model.addAttribute("discussions", list);
        final List<Long> ids = FluentIterable.from(list)
                .transform(new Function<Discussion, Long>() {
                    @Override
                    public Long apply(Discussion d) {
                        return d.getRoot().getId();
                    }
                }).toList();
        final Map<Long, Double> ratingsSums = ratingService.sumWeights(ids, CommentEntity.class);
        model.addAttribute("ratingsSums", ratingsSums);
        model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Мои обсуждения", "/discuss/new"));
        return "discussionMy";
    }

    @RequestMapping("/find")
    public String findDiscussions(Model model) {
        List<Discussion> list = discussionService.getAllDiscussions();
        model.addAttribute("discussions", list);
        model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Поиск обсуждения", "/discuss/new"));
        return "discussionFind";
    }

    @RequestMapping(value = "/topic/areas.json", produces = "application/json;charset=utf-8")
    @ResponseBody
    public List<DiscussionTopic> discussionAreas() {
    	return discussionTopicDao.findTop();
    }
    
    @RequestMapping(value = "/topic/fields.json", produces = "application/json;charset=utf-8")
    @ResponseBody
    public List<DiscussionTopic> discussionFields(@RequestParam("area") Long areaId) {
    	final DiscussionTopic area = discussionTopicDao.getById(areaId);
    	final List<DiscussionTopic> fields = discussionTopicDao.findByParent(area); 	
    	return fields;
    	
    }
    
    @RequestMapping(value = "/topic/topics.json", produces = "application/json;charset=utf-8")
    @ResponseBody
    public List<DiscussionTopic> discussionTopics(@RequestParam("field") Long fieldId) {
    	final DiscussionTopic field = discussionTopicDao.getById(fieldId);
    	final List<DiscussionTopic> topics = discussionTopicDao.findByParent(field);
    	return topics;
    }
    
    @RequestMapping(value = "/comment/{commentEntity}/vote")
    @ResponseBody
    public int commentVote(@PathVariable CommentEntity commentEntity, @RequestParam("action") Vote vote, UserEntity userEntity) {
    	discussionService.commentVote(commentEntity, vote, userEntity);
    	CommentEntity voted = discussionService.getCommentById(commentEntity.getId());
    	return commentDao.getCommentRating(voted);
    }
    
    @RequestMapping(value = "/comment/edit.json")
    @ResponseBody
    public String editComment(@RequestParam String id, @RequestParam String comment) {
    	Long commentId = Long.valueOf(id.replace("comment-text_", ""));
    	discussionService.editComment(commentId, comment);
    	return comment;
    }
}
