package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.user.ContactEvent;
import ru.askor.blagosfera.domain.events.user.ContactEventType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.ContactDao;
import ru.radom.kabinet.dao.ContactsGroupDao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.ContactEntity;
import ru.radom.kabinet.model.ContactStatus;
import ru.radom.kabinet.model.ContactsGroupEntity;
import ru.radom.kabinet.model.UserEntity;

import java.util.Date;
import java.util.List;

// сервис по работе с контактами и списками контактов

/*@Service("contactsService")
@Transactional
public class ContactsService {

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

	@Autowired
	private ContactDao contactDao;

	@Autowired
	private ContactsGroupDao contactsGroupDao;

	@Autowired
	private SharerDao sharerDao;

	public List<ContactEntity> getContacts(User user, boolean online) {
		return contactDao.getContacts(sharerDao.loadById(user.getId()), online);
	}

	public List<ContactEntity> getOnline(User user) {
		return contactDao.getOnline(sharerDao.loadById(user.getId()));
	}

    public ContactEntity getMirror(ContactEntity contact) {
        if (contact != null) {
            return contactDao.getBySharers(contact.getOther().getId(), contact.getUser());
        } else {
            return null;
        }
    }
}*/
