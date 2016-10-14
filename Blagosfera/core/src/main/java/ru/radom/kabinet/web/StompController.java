package ru.radom.kabinet.web;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import ru.askor.blagosfera.data.jpa.repositories.DialogsRepository;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.DialogEntity;
import ru.radom.kabinet.services.ChatService;
import ru.radom.kabinet.services.discuss.DiscussionService;
import ru.radom.kabinet.services.discuss.netexchange.NewCommentMessage;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.security.Principal;

@Controller
public class StompController {

	@Autowired
	private UserDataService userDataService;

	@Autowired
	private ChatService chatService;

	@Autowired
	private DiscussionService discussionService;

    @Autowired
    private DialogsRepository dialogsRepository;

	@MessageMapping("print_chat_message_to_server")
	public void processPrintChatMessage(Principal principal, String payload) {
		User sender = userDataService.getByEmail(principal.getName());
		JSONObject jsonPayload = new JSONObject(payload);
        Long dialogId = jsonPayload.getLong("dialogId");
        DialogEntity dialog = dialogsRepository.findOne(dialogId);

		// TODO Переделать
        for (UserEntity receiver : dialog.getUsers()) {
			User receiverUser = receiver.toDomain();
            if (!receiver.getId().equals(sender.getId())) chatService.processPrintMessage(sender, receiverUser, dialog);
        }
	}

	@MessageMapping("discuss_{discussion}")
	public void processNewCommentMessage(Principal principal, @DestinationVariable Long discussion, String json) throws Exception {
		User user = userDataService.getByEmail(principal.getName());

		JSONObject jsonPayload = new JSONObject(json);
		String text = jsonPayload.getString("comment");
		Long parent = jsonPayload.getLong("parent");

		NewCommentMessage message = new NewCommentMessage();
		message.setText(text);
		message.setParent(parent);
		message.setOwn(true);
		message.setRating(0);

		discussionService.processNewCommentMessage(discussion, message, user);
	}
}
