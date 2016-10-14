package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.ContactsGroupEntity;

@Component("contactsGroupSerializer")
public class ContactsGroupSerializer extends AbstractSerializer<ContactsGroupEntity> {

	@Override
	public JSONObject serializeInternal(ContactsGroupEntity group) {
		JSONObject jsonContactsGroup = new JSONObject();
		jsonContactsGroup.put("id", group.getId());
		jsonContactsGroup.put("name", group.getName());
		jsonContactsGroup.put("color", group.getColor());
		return jsonContactsGroup;
	}

}
