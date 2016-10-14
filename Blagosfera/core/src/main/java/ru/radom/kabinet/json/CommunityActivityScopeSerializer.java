package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.communities.CommunityActivityScope;

@Component
public class CommunityActivityScopeSerializer extends AbstractSerializer<CommunityActivityScope>{

	@Override
	public JSONObject serializeInternal(CommunityActivityScope object) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", object.getId());
		jsonObject.put("name", object.getName());
		return jsonObject;
	}

}
