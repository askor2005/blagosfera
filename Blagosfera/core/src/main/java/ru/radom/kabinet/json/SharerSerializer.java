package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.core.services.security.RosterService;
import ru.askor.blagosfera.domain.Address;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.askor.blagosfera.domain.contacts.ContactGroup;
import ru.radom.kabinet.dao.ContactDao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.ContactEntity;
import ru.radom.kabinet.model.ContactsGroupEntity;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.thread.ThreadParameters;

import java.util.ArrayList;
import java.util.Date;

@Component("sharerSerializer")
@Deprecated
public class SharerSerializer extends AbstractSerializer<UserEntity> {
	@Autowired
	private SharerDao sharerDao;
	//private

	@Autowired
	private ContactDao contactDao;

    @Autowired
    private RosterService rosterService;

	public JSONObject serializeSingleSharer(UserEntity userEntity, ContactEntity contact) {
		if (ThreadParameters.exists(userEntity.getEmail())) {
			return ThreadParameters.getParameter(userEntity.getEmail());
		}
		JSONObject jsonSharer = new JSONObject();
		Address actualAddress = sharerDao.getActualAddress(userEntity.getId());
		jsonSharer.put("actualCountry", actualAddress != null ? actualAddress.getCountry() : "");
		jsonSharer.put("actualCity", actualAddress != null ? actualAddress.getCity() : "");
		if (((actualAddress.getCity() == null) || (actualAddress.getCity().isEmpty())) && (actualAddress.getRegion() != null) &&( (actualAddress.getRegion().equals("Москва")) || (actualAddress.getRegion().equals("Санкт-Петербург")))) {
			jsonSharer.put("actualCity", actualAddress.getRegion());
		}
		jsonSharer.put("id", userEntity.getId());
		jsonSharer.put("ikp", userEntity.getIkp());
		jsonSharer.put("fullName", userEntity.getFullName());
		jsonSharer.put("shortName", userEntity.getShortName());
		jsonSharer.put("mediumName", userEntity.getMediumName());
		jsonSharer.put("groupName", userEntity.getGroup() != null ? userEntity.getGroup().getName() : "");
		jsonSharer.put("link", userEntity.getLink());
		jsonSharer.put("avatar", userEntity.getAvatar());
		//jsonSharer.put("balance", StringUtils.formatMoney(sharer.getBalance()));
		jsonSharer.put("verified", userEntity.isVerified());
		jsonSharer.put("registeredAt", DateUtils.formatDate(userEntity.getRegisteredAt(), DateUtils.Format.DATE));
		jsonSharer.put("sex", userEntity.getSex());
		//jsonSharer.put("officialAppeal", sharer.getOfficialAppeal());

		jsonSharer.put("lastLogin", userEntity.getLogoutDate() != null ? DateUtils.formatDate(userEntity.getLogoutDate(), DateUtils.Format.DATE_TIME_SHORT) : null);

        jsonSharer.put("online", rosterService.isUserOnline(userEntity.getEmail()));

		if (contact != null) {
			JSONObject jsonContact = new JSONObject();
			jsonContact.put("id", contact.getId());
			jsonContact.put("sharerStatus", contact.getSharerStatus());
			jsonContact.put("otherStatus", contact.getOtherStatus());
            if (contact.getContactsGroups() != null) {
				ArrayList<ContactsGroupEntity> contactsGroupEntities = new ArrayList<>();
				for (ContactsGroupEntity contactGroup : contact.getContactsGroups()) {
					contactsGroupEntities.add(contactGroup);
				}
				jsonContact.put("contactGroups", serializationManager.serializeCollection(contactsGroupEntities));
			}
			if (contact.getRequestDate() != null) {
				jsonContact.put("requestDate", DateUtils.formatDate(contact.getRequestDate(), "dd.MM.yyyy HH:mm:ss"));
				jsonContact.put("requestHoursDistance", DateUtils.getDistanceHours(contact.getRequestDate(), new Date()));
			}
			jsonSharer.put("contact", jsonContact);
		}
        /*jsonSharer.put("registrationOfficeAddress", serializationManager.serialize(sharer.getRegistratorOfficeAddress()));
        if(!StringUtils.isEmpty(sharer.getHomePhone())) jsonSharer.put("phone", sharer.getHomePhone());
        if(!StringUtils.isEmpty(sharer.getMobilePhone())) jsonSharer.put("mobilePhone", sharer.getMobilePhone());
        if(!StringUtils.isEmpty(sharer.getSkype())) jsonSharer.put("skype", sharer.getSkype());
        if(!StringUtils.isEmpty(sharer.getRegistratorOfficePhone())) jsonSharer.put("registratorOfficePhone", sharer.getRegistratorOfficePhone());
        if(!StringUtils.isEmpty(sharer.getRegistratorMobilePhone())) jsonSharer.put("registratorMobilePhone", sharer.getRegistratorMobilePhone());*/
		ThreadParameters.setParameter(userEntity.getEmail(), jsonSharer);
		return jsonSharer;
	}

	@Override
	public JSONObject serializeInternal(UserEntity userEntity) {
		ContactEntity contact = contactDao.getBySharers(SecurityUtils.getUser().getId(), userEntity.getId());
		return serializeSingleSharer(userEntity, contact);
	}

}
