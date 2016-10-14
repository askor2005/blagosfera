package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.ContactEntity;

@Component("contactSerializer")
public class ContactSerializer extends AbstractSerializer<ContactEntity> {

	@Override
	public JSONObject serializeInternal(ContactEntity contact) {
		return serializationManager.serialize(contact.getOther());
	}

}
