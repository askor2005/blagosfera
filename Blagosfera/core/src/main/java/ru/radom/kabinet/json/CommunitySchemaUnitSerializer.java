package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.communities.schema.CommunitySchemaUnitEntity;

@Component("CommunitySchemaUnitSerializer")
public class CommunitySchemaUnitSerializer extends AbstractSerializer<CommunitySchemaUnitEntity> {

	@Override
	public JSONObject serializeInternal(CommunitySchemaUnitEntity unit) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", unit.getId());
		jsonObject.put("type", unit.getType());
		jsonObject.put("name", unit.getName());
		jsonObject.put("x", unit.getX());
		jsonObject.put("y", unit.getY());
		jsonObject.put("width", unit.getWidth());
		jsonObject.put("height", unit.getHeight());
		jsonObject.put("bgColor", unit.getBgColor());
		
		jsonObject.put("draw2dId", unit.getDraw2dId());
		jsonObject.put("managerFullName", unit.getManager() != null ? unit.getManager().getFullName() : null);
		jsonObject.put("managerIkp", unit.getManager() != null ? unit.getManager().getIkp() : null);

		jsonObject.put("members", serializationManager.serializeCollection(unit.getMembers()));
		jsonObject.put("connections", serializationManager.serializeCollection(unit.getConnections()));
		return jsonObject;
	}

}
