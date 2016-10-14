package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.rating.Rating;
import ru.radom.kabinet.utils.DateUtils;

@Component
public class RatingSerializer extends AbstractSerializer<Rating> {

	@Override
	public JSONObject serializeInternal(Rating object) {
		JSONObject json = new JSONObject();
        json.put("id", object.getId());
		json.put("weight", object.getWeight().intValue());
        json.put("contentId", ((LongIdentifiable) object.getContent()).getId());
        json.put("contentType", Discriminators.get(object.getClass()));
		json.put("created", DateUtils.formatDate(object.getCreated(), DateUtils.Format.DATE_TIME_SHORT));
        json.put("deleted", object.isDeleted());
        json.put("user", serializationManager.serialize(object.getUser()));
		return json;
	}

}
