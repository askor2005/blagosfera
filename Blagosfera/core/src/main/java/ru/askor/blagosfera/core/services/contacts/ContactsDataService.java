package ru.askor.blagosfera.core.services.contacts;

import ru.askor.blagosfera.domain.contacts.Contact;
import ru.radom.kabinet.model.ContactEntity;
import ru.radom.kabinet.model.ContactStatus;

import java.util.List;

/**
 * Created by vtarasenko on 14.04.2016.
 */
public interface ContactsDataService {

    Contact getByUserAndOther(Long userId, Long otherId);

    List<Contact> searchContacts(Long userId, Long otherId, String otherSearchString,
                                 ContactStatus sharerStatus, ContactStatus otherStatus,
                                 boolean filterGroup, Long groupId, Boolean deleted,
                                 int page, int pageSize, String orderBy, boolean asc);

    Long getCount(Long userId, Long otherId, String otherSearchString,
                  ContactStatus sharerStatus, ContactStatus otherStatus,
                  boolean filterGroup, Long groupId, Boolean deleted);

    List<Contact> getContacts(Long userId, ContactStatus sharerStatus, ContactStatus otherStatus);

    List<Contact> getContactsByOtherId(Long otherId, ContactStatus sharerStatus, ContactStatus otherStatus);

    //TODO возврат entity нужен пока только для совместимости с системой оповещений
    ContactEntity save(Contact contact);

    void delete(Long id);

    List<Contact> getByContactGroup(Long groupId);
}
