package ru.askor.blagosfera.core.services.contacts;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.contacts.ContactGroupRepository;
import ru.askor.blagosfera.data.jpa.repositories.contacts.ContactsRepository;
import ru.askor.blagosfera.data.jpa.specifications.contacts.ContactSpecifications;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.askor.blagosfera.domain.contacts.ContactGroup;
import ru.radom.kabinet.model.ContactEntity;
import ru.radom.kabinet.model.ContactStatus;
import ru.radom.kabinet.model.ContactsGroupEntity;
import ru.radom.kabinet.model.UserEntity;

import javax.transaction.Transactional;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by vtarasenko on 08.04.2016.
 */
@Service
@Transactional
public class ContactsDataServiceImpl implements ContactsDataService {
    private static final Logger logger = LoggerFactory.createLogger(ContactsDataService.class);

    @Autowired
    private ContactGroupRepository contactGroupRepository;

    @Autowired
    private ContactsRepository contactsRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Contact getByUserAndOther(Long userId, Long otherId) {
        ContactEntity contact = contactsRepository.findByUserAndOther(userId, otherId);
        return contact != null ? contact.toDomain() : null;
    }

    @Override
    public List<Contact> searchContacts(Long userId, Long otherId, String otherSearchString,
                                        ContactStatus sharerStatus, ContactStatus otherStatus,
                                        boolean filterGroup, Long groupId, Boolean deleted,
                                        int page, int pageSize, String orderBy, boolean asc) {
        UserEntity user = (userId != null) ? userRepository.findOne(userId) : null;
        UserEntity other = (otherId != null) ? userRepository.findOne(otherId) : null;
        ContactsGroupEntity contactsGroup = (groupId != null) ? contactGroupRepository.findOne(groupId) : null;
        Specification<ContactEntity> specification = buildSearchSpecification(user, other, otherSearchString,
                sharerStatus, otherStatus, filterGroup, contactsGroup, deleted);
        PageRequest pageRequest = new PageRequest(page, pageSize, new Sort(new Sort.Order(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy)));
        return ContactEntity.toDomainList(contactsRepository.findAll(specification, pageRequest).getContent());
    }

    @Override
    public Long getCount(Long userId, Long otherId, String otherSearchString,
                         ContactStatus sharerStatus, ContactStatus otherStatus,
                         boolean filterGroup, Long groupId, Boolean deleted) {
        UserEntity user = (userId != null) ? userRepository.findOne(userId) : null;
        UserEntity other = (otherId != null) ? userRepository.findOne(otherId) : null;
        ContactsGroupEntity contactsGroup = (groupId != null) ? contactGroupRepository.findOne(groupId) : null;
        Specification<ContactEntity> specification = buildSearchSpecification(user, other, otherSearchString,
                sharerStatus, otherStatus, filterGroup, contactsGroup, deleted);
        return contactsRepository.count(specification);
    }

    @Override
    public List<Contact> getContacts(Long userId, ContactStatus sharerStatus, ContactStatus otherStatus) {
        Specification<ContactEntity> specification = buildSearchSpecification(userRepository.findOne(userId), null, null,
                sharerStatus, otherStatus, false, null, false);
        return ContactEntity.toDomainList(contactsRepository.findAll(specification));
    }

    @Override
    public List<Contact> getContactsByOtherId(Long otherId, ContactStatus sharerStatus, ContactStatus otherStatus) {
        Specification<ContactEntity> specification = buildSearchSpecification(null, userRepository.findOne(otherId), null,
                sharerStatus, otherStatus, false, null, false);
        return ContactEntity.toDomainList(contactsRepository.findAll(specification));
    }

    //TODO возврат entity нужен пока только для совместимости с системой оповещений
    @Override
    public ContactEntity save(Contact contact) {
        ContactEntity contactEntity;

        if (contact.getId() == null) {
            contactEntity = new ContactEntity();
        } else {
            contactEntity = contactsRepository.getOne(contact.getId());
        }
        contactEntity.getContactsGroups().clear();
        if (contact.getContactGroups() != null) {
            contact.getContactGroups().forEach(contactGroup -> {
                contactEntity.getContactsGroups().add(contactGroupRepository.findOne(contactGroup.getId()));
            });
        }
       // contactEntity.setContactsGroup(contact.getContactsGroup() != null ? contactGroupRepository.findOne(contact.getContactsGroup().getId()) : null);
        contactEntity.setUser(userRepository.findOne(contact.getUser().getId()));
        contactEntity.setOther(userRepository.findOne(contact.getOther().getId()));
        contactEntity.setSharerStatus(contact.getSharerStatus());
        contactEntity.setOtherStatus(contact.getOtherStatus());
        contactEntity.setRequestDate(contact.getRequestDate());
        return contactsRepository.saveAndFlush(contactEntity);
    }

    @Override
    public void delete(Long id) {
        ContactEntity contactEntity = contactsRepository.findOne(id);
        assert contactEntity != null;
        if (contactEntity.getContactsGroups() != null) {
            contactEntity.getContactsGroups().clear();
        }
        contactsRepository.delete(contactEntity);
    }

    @Override
    public List<Contact> getByContactGroup(Long groupId) {
        return contactGroupRepository.findOne(groupId).getContacts().stream().map(contact -> contact.toDomain()).collect(Collectors.toList());
    }

    private Specifications<ContactEntity> buildSearchSpecification(UserEntity user, UserEntity other, String otherSearchString,
                                                                   ContactStatus sharerStatus, ContactStatus otherStatus,
                                                                   boolean filterByGroup, ContactsGroupEntity group, Boolean deleted) {
        Specifications<ContactEntity> result = Specifications.where(ContactSpecifications.allContacts());

        if (user != null) {
            result = result.and(ContactSpecifications.userEquals(user));
        }

        if (other != null) {
            result = result.and(ContactSpecifications.otherEquals(other));
        }

        if (otherSearchString != null) {
            result = result.and(ContactSpecifications.otherSearchStringLike(otherSearchString));
        }

        if (sharerStatus != null) {
            result = result.and(ContactSpecifications.sharerContactStatusEquals(sharerStatus));
        }

        if (otherStatus != null) {
            result = result.and(ContactSpecifications.otherStatusEquals(otherStatus));
        }

        if (filterByGroup) {
            result = result.and(ContactSpecifications.contactsGroupEquals(group));
        }

        if (deleted != null) {
            result = result.and(ContactSpecifications.otherDeleted(deleted));
        }

        return result;
    }
}
