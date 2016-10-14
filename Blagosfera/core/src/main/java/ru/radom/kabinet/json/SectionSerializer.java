/*
package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import ru.radom.kabinet.model.web.Section;

// TODO Удалить
@Component
public class SectionSerializer extends AbstractSerializer<Section> {

	@Override
	public JSONObject serializeInternal(Section object) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", object.getId());
		jsonObject.put("title", object.getTitle());
		jsonObject.put("position", object.getPosition());
		jsonObject.put("link", object.getLink());
		jsonObject.put("icon", object.getIcon());
		jsonObject.put("imageUrl", object.getImageUrl());
		jsonObject.put("editable", object.isEditable());
		jsonObject.put("type", object.getType());
		jsonObject.put("application", serializationManager.serialize(object.getApplication()));
		jsonObject.put("published", object.getPublished());
		return jsonObject;
	}

}
*/
