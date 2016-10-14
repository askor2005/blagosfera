package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.services.ProfileFilling;

@Component("profileFillingSerializer")
public class ProfileFillingSerializer extends AbstractSerializer<ProfileFilling> {

	@Override
	public JSONObject serializeInternal(ProfileFilling object) {
		JSONObject jsonObject =new JSONObject();
		jsonObject.put("percent", object.getPercent());
		jsonObject.put("filledPoints", object.getFilledPoints());
		jsonObject.put("totalPoints", object.getTotalPoints());
		jsonObject.put("avatarLoaded", object.isAvatarLoaded());
		jsonObject.put("allRequiredFilled", object.isAllReqiredFilled());
		jsonObject.put("filledFields", serializationManager.serializeCollection(object.getFilledFields()));
		jsonObject.put("notFilledFields", serializationManager.serializeCollection(object.getNotFilledFields()));
		
		jsonObject.put("treshold", object.getTreshold());
		jsonObject.put("hoursBeforeArchivation", object.getHoursBeforeArchivation());
		jsonObject.put("hoursBeforeDeletion", object.getHoursBeforeDeletion());
		
		jsonObject.put("archived", object.isArchived());
		jsonObject.put("deleted", object.isDeleted());
		
		return jsonObject;
	}

}
