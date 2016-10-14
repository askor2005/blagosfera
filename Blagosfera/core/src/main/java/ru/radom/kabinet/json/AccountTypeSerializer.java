package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.data.jpa.entities.account.AccountTypeEntity;

@Component
public class AccountTypeSerializer extends AbstractSerializer<AccountTypeEntity>{

	@Override
	public JSONObject serializeInternal(AccountTypeEntity object) {
		JSONObject json = new JSONObject();
		json.put("id", object.getId());
		json.put("name", object.getName());
		return json;
	}

}
