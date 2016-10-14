package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.communities.schema.CommunitySchemaEntity;

@Component("CommunitySchemaSerializer")
public class CommunitySchemaSerializer  extends AbstractSerializer<CommunitySchemaEntity> {
	@Override
	public JSONObject serializeInternal(CommunitySchemaEntity schema) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("units", serializationManager.serializeCollection(schema.getUnits()));
		jsonObject.put("bgImageUrl", schema.getBgImageUrl());
		jsonObject.put("width", schema.getWidth());
		jsonObject.put("height", schema.getHeight());
		jsonObject.put("scrollLeft", schema.getScrollLeft());
		jsonObject.put("scrollTop", schema.getScrollTop());
		return jsonObject;
	}

}
