package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.data.jpa.entities.account.SharebookEntity;
import ru.radom.kabinet.utils.StringUtils;

@Component
public class BookAccountSerializer extends AbstractSerializer<SharebookEntity> {

	@Override
	public JSONObject serializeInternal(SharebookEntity object) {
		JSONObject json = new JSONObject();
		json.put("id", object.getId());
		if (object.getAccount() != null) {
			json.put("balance", StringUtils.formatMoney(object.getAccount().getBalance()));
			json.put("type", serializationManager.serialize(object.getAccount().getType()));
		}
		if (object.getBonusAccount() != null) {
			json.put("bonus", StringUtils.formatMoney(object.getBonusAccount().getBalance()));
		}
		return json;
	}

}
