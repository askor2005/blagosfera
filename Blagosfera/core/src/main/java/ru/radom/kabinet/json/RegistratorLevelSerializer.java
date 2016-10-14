package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.registration.RegistratorLevel;

@Component
public class RegistratorLevelSerializer extends AbstractSerializer<RegistratorLevel>{

	@Override
	public JSONObject serializeInternal(RegistratorLevel level) {
		final JSONObject json = new JSONObject();
        json.put("mnemo", level.getMnemo().replace(RegistratorLevel.PREFIX + ".", ""));
        json.put("name", level.getName());
		return json;
	}

}
