package ru.radom.kabinet.json.cyberbrain;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.json.AbstractSerializer;
import ru.radom.kabinet.model.cyberbrain.ScoreObject;

@Component("scoreObjectSerializer")
public class ScoreObjectSerializer extends AbstractSerializer<ScoreObject> {

	@Override
	public JSONObject serializeInternal(ScoreObject scoreObject) {
		JSONObject jsonScoreObject = new JSONObject();
        jsonScoreObject.put("id", scoreObject.getId());
        jsonScoreObject.put("name", scoreObject.getName());
        jsonScoreObject.put("description", scoreObject.getDescription());

		return jsonScoreObject;
	}
}
