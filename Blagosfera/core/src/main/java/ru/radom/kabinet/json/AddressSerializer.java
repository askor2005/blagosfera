package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.domain.Address;

@Component
public class AddressSerializer extends AbstractSerializer<Address>{

	@Override
	public JSONObject serializeInternal(Address address) {
		final JSONObject json = new JSONObject();
        json.put("full", address.getFullAddress());
		json.put("longitude", address.getLongitude());
        json.put("latitude", address.getLatitude());
		return json;
	}

}
