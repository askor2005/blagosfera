package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.data.jpa.entities.cms.HelpSectionEntity;

@Component
public class HelpSectionSerializer extends AbstractSerializer<HelpSectionEntity> {

	@Override
	public JSONObject serializeInternal(HelpSectionEntity object) {
		JSONObject json = new JSONObject();
		json.put("id", object.getId());
		json.put("name", object.getName());
		return json;
	}

}
