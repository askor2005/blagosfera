package ru.radom.kabinet.json.cyberbrain;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.json.AbstractSerializer;
import ru.radom.kabinet.model.cyberbrain.UserProblem;

@Component("userProblemSerializer")
public class UserProblemSerializer extends AbstractSerializer<UserProblem> {

	@Override
	public JSONObject serializeInternal(UserProblem userProblem) {
		JSONObject jsonUserProblem = new JSONObject();
        jsonUserProblem.put("id", userProblem.getId());
        jsonUserProblem.put("description", userProblem.getDescription());

		if (userProblem.getTagObject() != null) {
			JSONObject jsonTagObject = new JSONObject();
            jsonTagObject.put("id", userProblem.getTagObject().getId());
            jsonTagObject.put("name", userProblem.getTagObject().getEssence());
            jsonUserProblem.put("tag_object", jsonTagObject);
		}

        if (userProblem.getTagMany() != null) {
            JSONObject jsonTagMany = new JSONObject();
            jsonTagMany.put("id", userProblem.getTagMany().getId());
            jsonTagMany.put("name", userProblem.getTagMany().getEssence());
            jsonUserProblem.put("tag_many", jsonTagMany);
        }

        if (userProblem.getCommunity() != null) {
            JSONObject jsonCommunity = new JSONObject();
            jsonCommunity.put("id", userProblem.getCommunity().getId());
            jsonCommunity.put("name", userProblem.getCommunity().getName());
            jsonUserProblem.put("community", jsonCommunity);
        }

		return jsonUserProblem;
	}
}