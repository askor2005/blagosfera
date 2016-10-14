package ru.radom.kabinet.json.cyberbrain;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.json.AbstractSerializer;
import ru.radom.kabinet.model.cyberbrain.ScoreInfo;
import ru.radom.kabinet.utils.DateUtils;

@Component("scoreInfoSerializer")
public class ScoreInfoSerializer extends AbstractSerializer<ScoreInfo> {

	@Override
	public JSONObject serializeInternal(ScoreInfo scoreInfo) {
		JSONObject jsonScoreInfo = new JSONObject();
        jsonScoreInfo.put("id", scoreInfo.getId());
        jsonScoreInfo.put("score", scoreInfo.getScore().toString());
        jsonScoreInfo.put("periodFrom", DateUtils.dateToString(scoreInfo.getPeriodFrom(), "yyyy/MM/dd HH:mm:ss"));
        jsonScoreInfo.put("periodTo", DateUtils.dateToString(scoreInfo.getPeriodTo(), "yyyy/MM/dd HH:mm:ss"));

		if (scoreInfo.getScoreObject() != null) {
			JSONObject jsonScoreObject = new JSONObject();
            jsonScoreObject.put("id", scoreInfo.getScoreObject().getId());
            jsonScoreObject.put("name", scoreInfo.getScoreObject().getName());
            jsonScoreObject.put("description", scoreInfo.getScoreObject().getDescription());
            jsonScoreInfo.put("scoreObject", jsonScoreObject);
		}

		return jsonScoreInfo;
	}
}
