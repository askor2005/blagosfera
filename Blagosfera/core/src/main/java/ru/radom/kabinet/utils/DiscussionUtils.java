package ru.radom.kabinet.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.discuss.CommentsTreeQueryResult;
import ru.radom.kabinet.services.discuss.DiscussionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscussionUtils {

	private static ApplicationContext applicationContext;

	public static void setApplicationContext(ApplicationContext applicationContext) {
		DiscussionUtils.applicationContext = applicationContext;
	}

	/**
	 * Заполняет моедль представления полями, на основе которых создается представление обсуждения
	 * @param model
	 * @param discussion
	 * @param discussionService
	 * @param sharerDao
	 */
	public static void attachDiscussion(Model model, Discussion discussion, DiscussionService discussionService, SharerDao sharerDao) {
		if (discussion != null) {
			List<CommentsTreeQueryResult> commentsTree = discussionService.getCommentsTree(discussion, 0, 10);

			model.addAttribute("discussion", discussion);
	        model.addAttribute("commentsTree", commentsTree);
	        model.addAttribute("currentUser", SecurityUtils.getUser());
	        model.addAttribute("discussionTopic", discussionService.discussionTopicName(discussion));
	
	        Map<String, UserEntity> users = new HashMap<>();
	        for (CommentsTreeQueryResult comment: commentsTree){
	            String userId = comment.getOwnerIkp();
	            if (!users.containsKey(userId)) {
	                users.put(userId, sharerDao.getByIkp(userId, null));
	            }
	        }
	        model.addAttribute("users", users);
		}
	}
}
