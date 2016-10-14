package ru.askor.blagosfera.core.services.contacts;

import ru.askor.blagosfera.domain.contacts.ContactGroup;

import java.util.List;

/**
 * Created by vtarasenko on 14.04.2016.
 */
public interface ContactsGroupDataService {
    List<ContactGroup> getByUser(Long userId);

    ContactGroup getById(Long id);

    ContactGroup getByIdAndUserId(Long id, Long userId);

    void delete(Long id);

    void save(ContactGroup contactGroupDomain);

    boolean nameExists(Long userId, String name, Long id);
}
