package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dto.StringObjectHashMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component("stringObjectHashMapSerializer")
public class StringObjectHashMapSerializer extends AbstractSerializer<StringObjectHashMap> {

	private final static List<Class> PRIMITIVES = Arrays.asList(new Class[] { String.class, Integer.class, Boolean.class, Long.class });

	@Override
	public JSONObject serializeInternal(StringObjectHashMap map) {
		JSONObject jsonObject = new JSONObject();
		for (Map.Entry<String, Object> entry : map.entrySet()) {

			if (entry.getValue() == null) {
				jsonObject.put(entry.getKey(), JSONObject.NULL);
			} else if (PRIMITIVES.contains(entry.getValue().getClass())) {
				jsonObject.put(entry.getKey(), entry.getValue());
			} else if (entry.getValue() instanceof Collection) {
				jsonObject.put(entry.getKey(), serializationManager.serializeCollection((Collection) entry.getValue()));
			} else {
				jsonObject.put(entry.getKey(), serializationManager.serialize(entry.getValue()));
			}
		}
		return jsonObject;
	}

}
