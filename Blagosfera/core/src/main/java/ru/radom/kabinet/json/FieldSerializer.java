package ru.radom.kabinet.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldPossibleValueEntity;

@Component("fieldSerializer")
public class FieldSerializer extends AbstractSerializer<FieldEntity> {
	@Autowired
	private FieldsGroupSerializer fieldsGroupSerializer;

	@Autowired
	private FieldPossibleValueSerializer fieldPossibleValueSerializer;

	@Override
	public JSONObject serializeInternal(FieldEntity field) {
		return serializeSingleField(field);
	}

	public JSONObject serializeSingleField(FieldEntity field) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", field.getId());
		jsonObject.put("internalName", field.getInternalName());
		jsonObject.put("name", field.getName());
		jsonObject.put("required", field.isRequired());
		jsonObject.put("points", field.getPoints());
		jsonObject.put("position", field.getPosition());
		if (field.isUseCase() != null) {
			jsonObject.put("useCase", field.isUseCase());
		} else {
			jsonObject.put("useCase", false);
		}
		if (field.getFieldsGroup() != null) {
			jsonObject.put("group", fieldsGroupSerializer.serializeSingleFieldsGroup(field.getFieldsGroup()));
		}
		jsonObject.put("hiddenByDefault", field.isHiddenByDefault());
		jsonObject.put("hideable", field.isHideable());
		jsonObject.put("type", field.getType().toString());

		JSONArray jsonArray = new JSONArray();
		for (FieldPossibleValueEntity fieldPossibleValue : field.getPossibleValues()) {
			jsonArray.put(fieldPossibleValueSerializer.serialize(fieldPossibleValue));
		}
		jsonObject.put("possibleValues", jsonArray);

		jsonObject.put("comment", field.getComment() != null ? field.getComment() : "");
		jsonObject.put("example", field.getExample() != null ? field.getExample() : "");
		jsonObject.put("unique", field.isUnique());
		jsonObject.put("verifiedEditable", field.isVerifiedEditable());
		jsonObject.put("attachedFile", field.getAttachedFile());
		jsonObject.put("isMetaField", false);
		return jsonObject;
	}
}