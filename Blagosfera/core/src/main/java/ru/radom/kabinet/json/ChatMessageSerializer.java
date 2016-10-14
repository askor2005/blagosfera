package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.chat.ChatMessage;
import ru.radom.kabinet.services.ChatService;
import ru.radom.kabinet.utils.DateUtils;

@Component("chatMessageSerializer")
public class ChatMessageSerializer extends AbstractSerializer<ChatMessage> {

	@Autowired
	private ChatService chatService;
	
	@Override
	public JSONObject serializeInternal(ChatMessage chatMessage) {
		JSONObject chatMessageJson = new JSONObject();
        chatMessageJson.put("id", chatMessage.getId());
		chatMessageJson.put("text", chatMessage.getText());
		chatMessageJson.put("date", DateUtils.formatDate(chatMessage.getDate(), "dd.MM.yyyy HH:mm:ss"));
		chatMessageJson.put("editDate", chatMessage.getEditDate() != null ? DateUtils.formatDate(chatMessage.getEditDate(), "dd.MM.yyyy HH:mm:ss") : null);
		chatMessageJson.put("editCount", chatMessage.getEditCount());
		chatMessageJson.put("read", chatService.isRead(chatMessage));
		chatMessageJson.put("deleted", chatMessage.isDeleted());
		chatMessageJson.put("allowEdit", chatService.checkAge(chatMessage));
		chatMessageJson.put("sender", serializationManager.serialize(chatMessage.getSender()));
		chatMessageJson.put("dialog", chatMessage.getDialog().getId());
		chatMessageJson.put("dialogName", chatMessage.getDialog().getName());
        chatMessageJson.put("uuid", chatMessage.getUuid());
		chatMessageJson.put("fileMessage", chatMessage.getFileMessage());
		chatMessageJson.put("fileChatMessageState", chatMessage.getFileChatMessageState());
		chatMessageJson.put("fileSize", chatMessage.getFileSize());
		chatMessageJson.put("fileLoadedPercent", chatMessage.getFileLoadedPercent());

        return chatMessageJson;
	}

}
