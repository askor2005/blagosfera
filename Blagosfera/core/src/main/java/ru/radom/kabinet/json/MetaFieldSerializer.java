package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.fields.MetaField;

@Component("metaFieldSerializer")
public class MetaFieldSerializer extends AbstractSerializer<MetaField> {
	@Autowired
	private FieldsGroupSerializer fieldsGroupSerializer;

	@Override
	public JSONObject serializeInternal(MetaField metaField) {
		return serializeSingle(metaField);
	}

	public JSONObject serializeSingle(MetaField metaField) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", metaField.getId());
		jsonObject.put("internalName", metaField.getInternalName());
		jsonObject.put("participantType", metaField.getParticipantType());
		if (metaField.getFieldsGroup() != null) {
			jsonObject.put("group", fieldsGroupSerializer.serializeSingleFieldsGroup(metaField.getFieldsGroup()));
		}
		jsonObject.put("position", metaField.getPosition());
		jsonObject.put("name", metaField.getName());
		jsonObject.put("description", metaField.getDescription());
		jsonObject.put("textValue", metaField.getTextValue());
		if (metaField.getUseCase() != null) {
			jsonObject.put("useCase", metaField.getUseCase());
		} else {
			jsonObject.put("useCase", false);
		}
		jsonObject.put("isMetaField", true);
		return jsonObject;
	}
}