package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.notifications.NotificationEntity;
import ru.radom.kabinet.utils.DateUtils;

@Component("notificationSerializer")
public class NotificationSerializer extends AbstractSerializer<NotificationEntity> {

	@Override
	public JSONObject serializeInternal(NotificationEntity notification) {
		JSONObject jsonNotification = new JSONObject();
		jsonNotification.put("id", notification.getId());
		jsonNotification.put("subject", notification.getSubject());
		jsonNotification.put("shortText", notification.getShortText());
		jsonNotification.put("text", notification.getText());
		jsonNotification.put("priority", notification.getPriority());
		jsonNotification.put("date", DateUtils.formatDate(notification.getDate(), "dd.MM.yyyy HH:mm:ss"));
		jsonNotification.put("read", notification.isRead());
		jsonNotification.put("links", serializationManager.serializeCollection(notification.getLinks()));
		JSONObject jsonSender = new JSONObject();
		jsonSender.put("name", ((UserEntity) notification.getSender()).getName());
		jsonSender.put("link", ((UserEntity) notification.getSender()).getLink());
		jsonSender.put("avatar", ((UserEntity) notification.getSender()).getAvatar());
		jsonNotification.put("sender", jsonSender);
		return jsonNotification;
	}

}
