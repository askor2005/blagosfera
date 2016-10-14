package ru.askor.blagosfera.core.services.contacts;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.services.security.RosterService;
import ru.askor.blagosfera.data.jpa.repositories.ChatMessageReceiverRepository;
import ru.askor.blagosfera.data.jpa.repositories.DialogsRepository;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.askor.blagosfera.domain.contacts.ContactGroup;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.user.ContactEvent;
import ru.askor.blagosfera.domain.events.user.ContactEventType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.ContactStatus;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.DialogEntity;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.web.chat.dto.ContactDto;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by vtarasenko on 08.04.2016.
 */
@Service
@Transactional
public class ContactsServiceImpl implements ContactsService {
    private static final Logger logger = LoggerFactory.createLogger(ContactsService.class);

    @Autowired
    private ContactsDataService contactsDataService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private ContactsGroupDataService contactsGroupDataService;

    @Autowired
    private DialogsRepository dialogsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageReceiverRepository chatMessageReceiverRepository;

    @Autowired
    private RosterService rosterService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    public ContactsServiceImpl() {
    }

    @Override
    public List<Contact> searchContactsOrderByOther(Long userId, Long otherId, String otherSearchString, ContactStatus sharerStatus, ContactStatus otherStatus, boolean filterGroup, Long groupId, int page, int pageSize, String orderBy, boolean asc) {
        return contactsDataService.searchContacts(userId, otherId, otherSearchString,
                sharerStatus, otherStatus, filterGroup,
                groupId, false, page, pageSize, "other." + orderBy, asc);
    }

    @Override
    public List<Contact> getContacts(Long userId, ContactStatus sharerStatus, ContactStatus otherStatus) {
        return contactsDataService.getContacts(userId, sharerStatus, otherStatus);
    }

    @Override
    public List<Contact> getContactsByOtherId(Long otherId, ContactStatus sharerStatus, ContactStatus otherStatus) {
        return contactsDataService.getContactsByOtherId(otherId, sharerStatus, otherStatus);
    }

    @Override
    public Long getDefaultGroupCount(Long userId) {
        return contactsDataService.getCount(userId, null, null, ContactStatus.ACCEPTED, ContactStatus.ACCEPTED, true, null, false);
    }

    @Override
    public Long getCount(Long userId) {
        return contactsDataService.getCount(userId, null, null, ContactStatus.ACCEPTED, ContactStatus.ACCEPTED, false, null, false);
    }

    @Override
    public Contact addContact(Long userId, Long otherId, Long groupId) {
        Contact contact = contactsDataService.getByUserAndOther(userId, otherId);
        Contact mirrorContact = contactsDataService.getByUserAndOther(otherId, userId);
        User user = userDataService.getByIdMinData(userId);
        User other = userDataService.getByIdMinData(otherId);
        assert user != null;
        assert other != null;
        boolean isNewContact = ((contact == null) && (mirrorContact == null));

        if (contact == null) {
            contact = new Contact();
            contact.setUser(user);
            contact.setOther(other);
            contact.setOtherStatus(mirrorContact == null ? ContactStatus.NEW : mirrorContact.getSharerStatus());
        }

        if (isNewContact) {
            contact.setRequestDate(new Date());
        }
        if (groupId != null) {
            ContactGroup contactGroup = contactsGroupDataService.getByIdAndUserId(groupId, userId);
            if ((contactGroup != null) && (!contact.getContactGroups().contains(contactGroup))) {
                contact.getContactGroups().add(contactGroup);
            }
        }
        contact.setSharerStatus(ContactStatus.ACCEPTED);
        blagosferaEventPublisher.publishEvent(new ContactEvent(this, ContactEventType.ACCEPTED, contact));
        if (mirrorContact == null) {
            mirrorContact = new Contact();
            mirrorContact.setSharerStatus(ContactStatus.NEW);
            mirrorContact.setUser(other);
            mirrorContact.setOther(user);
        }

        mirrorContact.setOtherStatus(ContactStatus.ACCEPTED);

        if (isNewContact) {
            mirrorContact.setRequestDate(new Date());
        }
        contactsDataService.save(contact);
        contactsDataService.save(mirrorContact);

        if (isNewContact) {
            blagosferaEventPublisher.publishEvent(new ContactEvent(this, ContactEventType.ADD, contact));
        }

        contact.getOther().setLastLogin(userDataService.getLastLogin(otherId));//это чтобы вернуть по json
        return contact;

    }

    @Override
    public Contact deleteContact(Long userId, Long otherId) {
        Contact contact = contactsDataService.getByUserAndOther(userId, otherId);
        contact.getOther().setLastLogin(userDataService.getLastLogin(otherId));
        assert contact != null;
        contactsDataService.delete(contact.getId());
        Contact mirrorContact = contactsDataService.getByUserAndOther(otherId, userId);

        if (mirrorContact != null) {
            contactsDataService.delete(mirrorContact.getId());
        }

        blagosferaEventPublisher.publishEvent(new ContactEvent(this, ContactEventType.DELETE, contact));
        contact.setContactGroups(new HashSet<>());
        return contact;
    }

    @Override
    public Contact getMirror(Contact contact) {
        return contactsDataService.getByUserAndOther(contact.getOther().getId(), contact.getUser().getId());
    }

    @Override
    public ContactDto getContactWithUnreadMessages(Long receiverId, Long senderId) {
        Pageable pageable = new PageRequest(0, 1, Sort.Direction.ASC, "id");
        List<DialogEntity> dialogsContacts = dialogsRepository.getDialogsByUsersWithUnreadMessages(receiverId, senderId, pageable);
        ContactDto result = null;
        if (dialogsContacts != null && !dialogsContacts.isEmpty()) {
            UserEntity sender = userRepository.findOne(senderId);
            DialogEntity dialog = dialogsContacts.get(0);
            result = getContactDtoBySender(sender, dialog, receiverId);
        }
        return result;
    }

    @Override
    public List<ContactDto> getContactsByIdsOrUnreadMessages(Long receiverId, List<Long> ids, boolean withUnreadMessages) {
        List<DialogEntity> dialogsContacts = dialogsRepository.getDialogsBySharersOrUnreadMessages(receiverId, ids, withUnreadMessages);
        return getContactDtoListFromDialogs(receiverId, dialogsContacts, false);
    }

    @Override
    public List<ContactDto> searchContacts(Long receiverId, String searchString, int page, int perPage, boolean onlyOnline) {
        Pageable pageable = new PageRequest(page, perPage, Sort.Direction.ASC, "id");
        if (searchString != null) {
            searchString = "%" + searchString.toLowerCase() + "%";
        }
        List<DialogEntity> dialogsContacts = dialogsRepository.searchContacts(receiverId, searchString, pageable);
        return getContactDtoListFromDialogs(receiverId, dialogsContacts, onlyOnline);
    }

    @Override
    public Contact deleteContactGroup(Long userId, Long otherId, Long groupId) {
        Contact contact = contactsDataService.getByUserAndOther(userId, otherId);
        assert contact != null;
        ContactGroup contactGroup = contactsGroupDataService.getById(groupId);
        assert  contactGroup != null;
        if (contact.getContactGroups() != null) {
            contact.getContactGroups().remove(contactGroup);
        }
        contactsDataService.save(contact);
        return contact;
    }

    @Override
    public Contact addContact(Long userId, Long otherId, Long[] groupIds) {
        User user = userDataService.getByIdMinData(userId);
        User other = userDataService.getByIdMinData(otherId);
        Contact contact = contactsDataService.getByUserAndOther(userId, other.getId());
        Contact mirrorContact = contactsDataService.getByUserAndOther(other.getId(), userId);
        assert user != null;
        assert other != null;
        boolean isNewContact = ((contact == null) && (mirrorContact == null));

        if (contact == null) {
            contact = new Contact();
            contact.setUser(user);
            contact.setOther(other);
            contact.setOtherStatus(mirrorContact == null ? ContactStatus.NEW : mirrorContact.getSharerStatus());
        }

        if (isNewContact) {
            contact.setRequestDate(new Date());
        }
        contact.getContactGroups().clear();
        for (Long groupId : groupIds) {
            ContactGroup contactGroup = contactsGroupDataService.getByIdAndUserId(groupId, userId);
            if ((contactGroup != null) && (!contact.getContactGroups().contains(contactGroup))) {
                contact.getContactGroups().add(contactGroup);
            }
        }
        contact.setSharerStatus(ContactStatus.ACCEPTED);
        blagosferaEventPublisher.publishEvent(new ContactEvent(this, ContactEventType.ACCEPTED, contact));
        if (mirrorContact == null) {
            mirrorContact = new Contact();
            mirrorContact.setSharerStatus(ContactStatus.NEW);
            mirrorContact.setUser(other);
            mirrorContact.setOther(user);
        }

        mirrorContact.setOtherStatus(ContactStatus.ACCEPTED);

        if (isNewContact) {
            mirrorContact.setRequestDate(new Date());
        }
        contactsDataService.save(contact);
        contactsDataService.save(mirrorContact);

        if (isNewContact) {
            blagosferaEventPublisher.publishEvent(new ContactEvent(this, ContactEventType.ADD, contact));
        }

        contact.getOther().setLastLogin(userDataService.getLastLogin(other.getId()));//это чтобы вернуть по json
        return contact;
    }

    private List<ContactDto> getContactDtoListFromDialogs(Long receiverId, List<DialogEntity> dialogsContacts, boolean onlyOnline) {
        List<ContactDto> result = new ArrayList<>();
        if (dialogsContacts != null && !dialogsContacts.isEmpty()) {
            for (DialogEntity dialog : dialogsContacts) {
                Set<UserEntity> users = dialog.getUsers();
                UserEntity sender = null;
                for (UserEntity user : users) {
                    if (!user.getId().equals(receiverId)) {
                        sender = user;
                        break;
                    }
                }
                if (sender != null) {
                    ContactDto contactDto = getContactDtoBySender(sender, dialog, receiverId);
                    if (!onlyOnline || contactDto.isOnline()) {
                        result.add(contactDto);
                    }
                }
            }
        }
        return result;
    }

    private ContactDto getContactDtoBySender(UserEntity sender, DialogEntity dialog, Long receiverId) {
        boolean isOnline = rosterService.isUserOnline(sender.getEmail());
        ContactDto contactDto = new ContactDto();
        contactDto.setId(sender.getId());
        contactDto.setAvatar(sender.getAvatar());
        contactDto.setFullName(sender.getFullName());
        contactDto.setIkp(sender.getIkp());
        contactDto.setLink(sender.getLink());
        contactDto.setOnline(isOnline);
        contactDto.setDialogId(dialog.getId());
        contactDto.setCountMessages(chatMessageReceiverRepository.countUnredMessages(dialog.getId(), receiverId, sender.getId()));
        return contactDto;
    }
}
