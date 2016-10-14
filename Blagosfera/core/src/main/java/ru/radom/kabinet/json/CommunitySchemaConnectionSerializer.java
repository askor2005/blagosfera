package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.communities.schema.CommunitySchemaConnectionEntity;

@Component("communitySchemaConnectionSerializer")
public class CommunitySchemaConnectionSerializer extends AbstractSerializer<CommunitySchemaConnectionEntity> {

	@Override
	public JSONObject serializeInternal(CommunitySchemaConnectionEntity connection) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", serializationManager.serialize(connection.getType()));
		jsonObject.put("sourceDraw2dId", connection.getSource().getId());
		jsonObject.put("targetDraw2dId", connection.getTarget().getId());
        jsonObject.put("draw2dId", connection.getDraw2dId());
		return jsonObject;
	}

}
