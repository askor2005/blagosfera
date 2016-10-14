package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.communities.CommunityPostEntity;

@Component
public class CommunityPostSerializer extends AbstractSerializer<CommunityPostEntity> {

	@Override
	public JSONObject serializeInternal(CommunityPostEntity object) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", object.getId());
		jsonObject.put("name", object.getName());
		jsonObject.put("position", object.getPosition());
		jsonObject.put("vacanciesCount", object.getVacanciesCount());
		jsonObject.put("ceo", object.isCeo());
		jsonObject.put("schemaUnit", serializationManager.serialize(object.getSchemaUnit()));
		jsonObject.put("permissions", serializationManager.serializeCollection(object.getPermissions()));
		return jsonObject;
	}

}
