package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.communities.schema.CommunitySchemaUnitMemberEntity;

@Component("CommunitySchemaUnitMemberSerializer")
public class CommunitySchemaUnitMemberSerializer  extends AbstractSerializer<CommunitySchemaUnitMemberEntity> {
	@Override
	public JSONObject serializeInternal(CommunitySchemaUnitMemberEntity member) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", member.getId());
		jsonObject.put("ikp", member.getUser().getIkp());
		jsonObject.put("fullName", member.getUser().getFullName());
		return jsonObject;
	}
}
