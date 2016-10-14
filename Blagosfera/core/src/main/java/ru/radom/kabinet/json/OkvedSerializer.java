package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.OkvedEntity;

@Component("okvedSerializer")
public class OkvedSerializer extends AbstractSerializer<OkvedEntity> {

	@Override
	public JSONObject serializeInternal(OkvedEntity okved) {
		JSONObject okvedJson = new JSONObject();
		okvedJson.put("id", okved.getId());
		okvedJson.put("code", okved.getCode());
		okvedJson.put("shortName", okved.getShortName());
		okvedJson.put("longName", okved.getLongName());
		return okvedJson;
	}

}
