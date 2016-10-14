package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.discussion.CommentEntity;

@Component
public class CommentSerializer extends AbstractSerializer<CommentEntity>{

	@Override
	public JSONObject serializeInternal(CommentEntity object) {
		final JSONObject json = new JSONObject();
		json.put("id", object.getId());
		json.put("message", object.getMessage());
		//json.put("rating", object.getRating());
		return json;
	}

}
