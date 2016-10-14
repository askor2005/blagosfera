package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.RameraTextEntity;

@Component
public class RameraTextSerializer extends AbstractSerializer<RameraTextEntity>{

	@Override
	public JSONObject serializeInternal(RameraTextEntity object) {
		final JSONObject json = new JSONObject();
		json.put("id", object.getId());
		json.put("code", object.getCode());
		json.put("description", object.getDescription());
		json.put("text", object.getText());
        json.put("isHtml", object.isHtml());
		return json;
	}

}
