package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.data.jpa.entities.account.AccountEntity;
import ru.radom.kabinet.utils.StringUtils;

@Component
public class AccountSerializer extends AbstractSerializer<AccountEntity> {

	@Override
	public JSONObject serializeInternal(AccountEntity object) {
		JSONObject json = new JSONObject();
		json.put("id", object.getId());
		json.put("balance", StringUtils.formatMoney(object.getBalance()));
		json.put("type", serializationManager.serialize(object.getType()));
		return json;
	}

}
