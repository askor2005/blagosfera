package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.fields.FieldPossibleValueEntity;

@Component("fieldPossibleValueSerializer")
public class FieldPossibleValueSerializer extends AbstractSerializer<FieldPossibleValueEntity> {
    @Override
    public JSONObject serializeInternal(FieldPossibleValueEntity fieldPossibleValue) {
        return serializeSingle(fieldPossibleValue);
    }

    public JSONObject serializeSingle(FieldPossibleValueEntity fieldPossibleValue) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", fieldPossibleValue.getId());
        if (fieldPossibleValue.getField() != null) {
            JSONObject jsonField = new JSONObject();
            jsonField.put("id", fieldPossibleValue.getField().getId());
            jsonObject.put("field", jsonField);
        }
        jsonObject.put("stringValue", fieldPossibleValue.getStringValue());
        jsonObject.put("position", fieldPossibleValue.getPosition());
        return jsonObject;
    }
}