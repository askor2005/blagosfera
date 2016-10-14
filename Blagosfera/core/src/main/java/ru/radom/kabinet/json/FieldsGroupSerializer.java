package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;

@Component("fieldsGroupSerializer")
public class FieldsGroupSerializer extends AbstractSerializer<FieldsGroupEntity> {
	@Override
	public JSONObject serializeInternal(FieldsGroupEntity fieldsGroup) {
		return serializeSingleFieldsGroup(fieldsGroup);
	}

	public JSONObject serializeSingleFieldsGroup(FieldsGroupEntity fieldsGroup) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", fieldsGroup.getId());
		jsonObject.put("internalName", fieldsGroup.getInternalName());
		jsonObject.put("name", fieldsGroup.getName());
		jsonObject.put("position", fieldsGroup.getPosition());
		jsonObject.put("objectType", fieldsGroup.getObjectType());
		return jsonObject;
	}
}