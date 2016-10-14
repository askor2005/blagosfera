package ru.askor.blagosfera.core.services.contacts;

import ru.askor.blagosfera.domain.contacts.ContactGroup;

import java.util.List;

/**
 * Created by vtarasenko on 09.04.2016.
 */
public interface ContactsGroupService {
    public List<ContactGroup> getByUser(Long userId);

    void deleteGroup(Long groupId, Long userId);

    ContactGroup getByUserAndId(Long userId, Long groupId);

    void saveGroup(Long userId, String name, int color, Long id) throws Exception;
}
