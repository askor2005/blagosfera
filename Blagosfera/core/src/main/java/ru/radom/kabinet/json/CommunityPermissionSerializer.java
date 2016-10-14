package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.communities.CommunityPermissionEntity;

@Component
public class CommunityPermissionSerializer extends AbstractSerializer<CommunityPermissionEntity> {

	@Override
	public JSONObject serializeInternal(CommunityPermissionEntity object) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", object.getId());
		jsonObject.put("name", object.getName());
		jsonObject.put("title", object.getTitle());
		jsonObject.put("position", object.getPosition());
		return jsonObject;
	}
}
