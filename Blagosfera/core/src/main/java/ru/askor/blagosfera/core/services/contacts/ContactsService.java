package ru.askor.blagosfera.core.services.contacts;

import ru.askor.blagosfera.domain.contacts.Contact;
import ru.radom.kabinet.model.ContactStatus;
import ru.radom.kabinet.web.chat.dto.ContactDto;

import java.util.List;

/**
 * Created by vtarasenko on 08.04.2016.
 */
public interface ContactsService {

    List<Contact> searchContactsOrderByOther(Long userId, Long otherId, String otherSearchString,
                                             ContactStatus sharerStatus, ContactStatus otherStatus,
                                             boolean filterGroup, Long groupId,
                                             int page, int pageSize, String orderBy, boolean asc);

    List<Contact> getContacts(Long userId, ContactStatus sharerStatus, ContactStatus otherStatus);

    List<Contact> getContactsByOtherId(Long otherId, ContactStatus sharerStatus, ContactStatus otherStatus);

    Long getDefaultGroupCount(Long userId);

    Long getCount(Long userId);

    Contact addContact(Long userId, Long otherId, Long groupId);

    Contact deleteContact(Long userId, Long otherId);

    Contact getMirror(Contact contact);

    ContactDto getContactWithUnreadMessages(Long receiverId, Long senderId);

    List<ContactDto> getContactsByIdsOrUnreadMessages(Long receiverId, List<Long> ids, boolean withUnreadMessages);

    List<ContactDto> searchContacts(Long receiverId, String searchString, int page, int perPage, boolean online);

    Contact deleteContactGroup(Long userId, Long otherId, Long groupId);

    Contact addContact(Long userId, Long otherId, Long[] groupIds);
}
