package ru.radom.kabinet.json.cyberbrain;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.json.AbstractSerializer;
import ru.radom.kabinet.model.cyberbrain.ScoreSharer;
import ru.radom.kabinet.utils.DateUtils;

@Component("scoreSharerSerializer")
public class ScoreSharerSerializer extends AbstractSerializer<ScoreSharer> {

	@Override
	public JSONObject serializeInternal(ScoreSharer scoreSharer) {
		JSONObject jsonScoreSharer = new JSONObject();
        jsonScoreSharer.put("id", scoreSharer.getId());
        jsonScoreSharer.put("creationDate", DateUtils.dateToString(scoreSharer.getCreationDate(), "yyyy/MM/dd HH:mm:ss"));

        if (scoreSharer.getUserEntity() != null) {
            JSONObject jsonSharer = new JSONObject();
            jsonSharer.put("id", scoreSharer.getUserEntity().getId());
            jsonSharer.put("name", scoreSharer.getUserEntity().getFullName());
            jsonScoreSharer.put("sharer", jsonSharer);
        }

        if (scoreSharer.getScoreObject() != null) {
            JSONObject jsonScoreObject = new JSONObject();
            jsonScoreObject.put("id", scoreSharer.getScoreObject().getId());
            jsonScoreObject.put("name", scoreSharer.getScoreObject().getName());
            jsonScoreObject.put("description", scoreSharer.getScoreObject().getDescription());
            jsonScoreSharer.put("scoreObject", jsonScoreObject);
        }

        jsonScoreSharer.put("score", scoreSharer.getScore().toString());

        if (scoreSharer.getCommunity() != null) {
            JSONObject jsonCommunity = new JSONObject();
            jsonCommunity.put("id", scoreSharer.getCommunity().getId());
            jsonCommunity.put("name", scoreSharer.getCommunity().getName());
            jsonScoreSharer.put("community", jsonCommunity);
        }

		return jsonScoreSharer;
	}
}