package ru.askor.blagosfera.core.services.contacts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.askor.blagosfera.domain.contacts.ContactGroup;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by vtarasenko on 09.04.2016.
 */
@Service
@Transactional
public class ContactsGroupServiceImpl implements ContactsGroupService {
    @Autowired
    private ContactsGroupDataService contactsGroupDataService;
    @Autowired
    private ContactsDataService contactsDataServiceImpl;

    @Override
    public List<ContactGroup> getByUser(Long userId) {
        return contactsGroupDataService.getByUser(userId);
    }

    @Override
    public void deleteGroup(Long groupId, Long userId) {
        ContactGroup contactsGroup = contactsGroupDataService.getByIdAndUserId(groupId, userId);
        assert contactsGroup != null;
        /*for (Contact contact : contactsDataServiceImpl.getByContactGroup(contactsGroup.getId())) {
            contact.setContactsGroup(null);
            contactsDataServiceImpl.save(contact);
        }*/
        contactsGroupDataService.delete(contactsGroup.getId());
    }

    @Override
    public ContactGroup getByUserAndId(Long userId, Long groupId) {
        return contactsGroupDataService.getByIdAndUserId(groupId, userId);
    }
    //сохраняем группу
    @Override
    public void saveGroup(Long userId, String name, int color, Long id) throws Exception {
        ContactGroup contactGroupDomain = null;
        if (id != null) {
            contactGroupDomain = contactsGroupDataService.getByIdAndUserId(id, userId);
            if (contactGroupDomain == null) {
                throw new Exception("группа с таким Id не найдена!");
            }
        }
        else {
            contactGroupDomain = new ContactGroup();
        }
        assert ((color >= 1) && (color <= 10));
        if ((name == null) || (name.isEmpty())){
            throw new Exception("название не может быть пустым");
        }
        if (contactsGroupDataService.nameExists(userId, name,id)) {
            throw new Exception("Список с таким названием уже существует");
        }
        contactGroupDomain.setUserId(userId);
        contactGroupDomain.setName(name);
        contactGroupDomain.setColor(color);
        contactsGroupDataService.save(contactGroupDomain);
    }
}
