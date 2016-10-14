package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.communities.schema.CommunitySchemaConnectionTypeEntity;

@Component("communitySchemaConnectionTypeSerializer")
public class CommunitySchemaConnectionTypeSerializer extends AbstractSerializer<CommunitySchemaConnectionTypeEntity> {

	@Override
	public JSONObject serializeInternal(CommunitySchemaConnectionTypeEntity type) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", type.getId());
		jsonObject.put("name", type.getName());
		jsonObject.put("color", type.getColor());
		jsonObject.put("reversable", type.isReversable());
		return jsonObject;
	}

}
