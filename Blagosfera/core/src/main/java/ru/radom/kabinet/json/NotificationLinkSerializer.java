package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.notifications.NotificationLinkEntity;

@Component("notificationLinkSerializer")
public class NotificationLinkSerializer extends AbstractSerializer<NotificationLinkEntity> {

	@Override
	public JSONObject serializeInternal(NotificationLinkEntity link) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", link.getId());
		jsonObject.put("title", link.getTitle());
		jsonObject.put("url", link.getUrl());
		jsonObject.put("ajax", link.isAjax());
		jsonObject.put("markAsRead", link.isMakrAsRead());
		jsonObject.put("type", link.getType());
		jsonObject.put("position", link.getPosition());
		return jsonObject;
	}

}
