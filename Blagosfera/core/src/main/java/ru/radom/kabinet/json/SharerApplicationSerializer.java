package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.applications.SharerApplication;

@Component
public class SharerApplicationSerializer extends AbstractSerializer<SharerApplication> {

	@Override
	public JSONObject serializeInternal(SharerApplication object) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("installed", object.isInstalled());
		jsonObject.put("showInMenu", object.isShowInMenu());
		return jsonObject;
	}

}
