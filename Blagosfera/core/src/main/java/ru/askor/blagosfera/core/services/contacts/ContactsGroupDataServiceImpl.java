package ru.askor.blagosfera.core.services.contacts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.contacts.ContactGroupRepository;
import ru.askor.blagosfera.domain.contacts.ContactGroup;
import ru.radom.kabinet.model.ContactsGroupEntity;
import ru.radom.kabinet.model.UserEntity;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vtarasenko on 09.04.2016.
 */
@Transactional
@Service
public class ContactsGroupDataServiceImpl implements ContactsGroupDataService {
    @Autowired
    private ContactGroupRepository contactGroupRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public List<ContactGroup> getByUser(Long userId) {
        UserEntity userEntity = userRepository.findOne(userId);
        return contactGroupRepository.findByUser(userEntity).stream().map(contactsGroup -> contactsGroup.toDomain()).collect(Collectors.toList());
    }
    @Override
    public ContactGroup getById(Long id) {
        ContactsGroupEntity contactsGroup =contactGroupRepository.findOne(id);
        return contactsGroup != null ? contactsGroup.toDomain() : null;
    }

    @Override
    public ContactGroup getByIdAndUserId(Long id, Long userId) {
        ContactsGroupEntity result = contactGroupRepository.findByIdAndUserId(id, userId);
        return result != null ? result.toDomain() : null;
    }

    @Override
    public void delete(Long id) {
        ContactsGroupEntity contactsGroupEntity = contactGroupRepository.findOne(id);
        if (contactsGroupEntity.getContacts() != null) {
            contactsGroupEntity.getContacts().clear();
        }
        contactGroupRepository.delete(contactsGroupEntity);
    }

    @Override
    public void save(ContactGroup contactGroupDomain) {
        ContactsGroupEntity contactsGroup = contactGroupDomain.getId() != null ?  contactGroupRepository.findOne(contactGroupDomain.getId()) : new ContactsGroupEntity();
        contactsGroup.setColor(contactGroupDomain.getColor());
        contactsGroup.setName(contactGroupDomain.getName());
        contactsGroup.setUser(userRepository.findOne(contactGroupDomain.getUserId()));
        contactGroupRepository.saveAndFlush(contactsGroup);
    }

    @Override
    public boolean nameExists(Long userId, String name, Long id) {
        return  id == null ? contactGroupRepository.nameExists(userId,name) : contactGroupRepository.nameExists(userId,name,id);
    }
}
