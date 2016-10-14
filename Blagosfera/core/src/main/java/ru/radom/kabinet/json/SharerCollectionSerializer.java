package ru.radom.kabinet.json;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.ContactDao;
import ru.radom.kabinet.model.ContactEntity;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.security.SecurityUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Component("sharerCollectionSerializer")
public class SharerCollectionSerializer extends AbstractCollectionSerializer<UserEntity> {

	@Autowired
	private SharerSerializer sharerSerializer;
	
	@Autowired
	private ContactDao contactDao;

	@Override
	public JSONArray serializeInternal(Collection<UserEntity> collection) {
		List<ContactEntity> contacts = contactDao.getList(SecurityUtils.getUser().getId(), collection);
		JSONArray jsonArray = new JSONArray();
		for (UserEntity userEntity : collection) {
			ContactEntity contact = null;
			Iterator<ContactEntity> contactsIterator = contacts.iterator();
			while (contactsIterator.hasNext()) {
				ContactEntity current = contactsIterator.next();
				if (current.getOther().equals(userEntity)) {
					contact = current;
					contactsIterator.remove();
					break;
				}
			}
			jsonArray.put(sharerSerializer.serializeSingleSharer(userEntity, contact));
		}
		
		return jsonArray;
	}

}
