package ru.radom.kabinet.json.cyberbrain;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.json.AbstractSerializer;
import ru.radom.kabinet.model.cyberbrain.UserProblemPerformer;

@Component("userProblemPerformerSerializer")
public class UserProblemPerformerSerializer extends AbstractSerializer<UserProblemPerformer> {

	@Override
	public JSONObject serializeInternal(UserProblemPerformer userProblemPerformer) {
		JSONObject jsonUserProblemPerformer = new JSONObject();
        jsonUserProblemPerformer.put("id", userProblemPerformer.getId());

        if (userProblemPerformer.getUserProblem() != null) {
            JSONObject jsonUserProblem = new JSONObject();
            jsonUserProblem.put("id", userProblemPerformer.getUserProblem().getId());
            jsonUserProblem.put("name", userProblemPerformer.getUserProblem().getDescription());
            jsonUserProblemPerformer.put("user_problem", jsonUserProblem);
        }

		if (userProblemPerformer.getTagObject() != null) {
			JSONObject jsonTagObject = new JSONObject();
            jsonTagObject.put("id", userProblemPerformer.getTagObject().getId());
            jsonTagObject.put("name", userProblemPerformer.getTagObject().getEssence());
            jsonUserProblemPerformer.put("tag_object", jsonTagObject);
		}

        if (userProblemPerformer.getTagMany() != null) {
            JSONObject jsonTagMany = new JSONObject();
            jsonTagMany.put("id", userProblemPerformer.getTagMany().getId());
            jsonTagMany.put("name", userProblemPerformer.getTagMany().getEssence());
            jsonUserProblemPerformer.put("tag_many", jsonTagMany);
        }

        if (userProblemPerformer.getPerformer() != null) {
            JSONObject jsonPerformer = new JSONObject();
            jsonPerformer.put("id", userProblemPerformer.getPerformer().getId());
            jsonPerformer.put("name", userProblemPerformer.getPerformer().getFullName());
            jsonUserProblemPerformer.put("performer", jsonPerformer);
        }

		return jsonUserProblemPerformer;
	}
}