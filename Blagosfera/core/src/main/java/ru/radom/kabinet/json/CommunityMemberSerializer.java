package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.utils.DateUtils;

import java.util.Date;

@Component("communityMemberSerializer")
public class CommunityMemberSerializer extends AbstractSerializer<CommunityMemberEntity>{

	@Autowired
	private SharerSerializer sharerSerializer;
	
	@Override
	public JSONObject serializeInternal(CommunityMemberEntity member) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", member.getId());
		jsonObject.put("status", member.getStatus());
		jsonObject.put("creator", member.isCreator());
		jsonObject.put("sharer", sharerSerializer.serializeSingleSharer(member.getUser(), null));
		if (member.getInviter() != null) {
			jsonObject.put("inviter", sharerSerializer.serializeSingleSharer(member.getInviter(), null));
		}
		if (member.getRequestDate() != null) {
			jsonObject.put("requestDate", DateUtils.formatDate(member.getRequestDate(), "dd.MM.yyyy HH:mm:ss"));
			jsonObject.put("requestHoursDistance", DateUtils.getDistanceHours(member.getRequestDate(), new Date()));
		}
		/*try {
			jsonObject.put("posts", serializationManager.serializeCollection(member.getPosts()));
		} catch (Exception doNothing) {}*/
		return jsonObject;
	}

}
