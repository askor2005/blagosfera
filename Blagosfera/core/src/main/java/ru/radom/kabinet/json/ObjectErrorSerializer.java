package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Component("objectErrorSerializer")
public class ObjectErrorSerializer extends AbstractSerializer<ObjectError> {

	@Override
	public JSONObject serializeInternal(ObjectError error) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("message", error.getDefaultMessage());
		jsonObject.put("code", error.getCode());
		if (error instanceof FieldError) {
			FieldError fieldError = (FieldError)error;
			jsonObject.put("field", fieldError.getField());
		}
		return jsonObject;
	}

}
