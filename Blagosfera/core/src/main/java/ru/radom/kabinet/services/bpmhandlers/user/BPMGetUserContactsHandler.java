package ru.radom.kabinet.services.bpmhandlers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.contacts.ContactsDataService;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.SharerService;
import ru.radom.kabinet.model.ContactStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 15.08.2016.
 */
@Service("getUserContactsHandler")
@Transactional
public class BPMGetUserContactsHandler implements BPMHandler {

    @Autowired
    private ContactsDataService contactsDataService;

    @Autowired
    private SharerService sharerService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        Object sharerObj = parameters.get("sharer");
        User user = sharerService.tryGetUser(sharerObj);
        if (user != null) {
            List<Contact> contacts = contactsDataService.getContacts(user.getId(), ContactStatus.ACCEPTED, ContactStatus.ACCEPTED);
            return map(contacts);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Преобразовать список контактов в необходимый для отправки вид
     */
    private List<Map<String, Object>> map(List<Contact> contacts) {
        List<Map<String, Object>> result;
        if (contacts == null) {
            result = Collections.emptyList();
        } else {
            result = contacts.stream().map(contact -> sharerService.convertUserToSend(contact.getOther())).collect(Collectors.toList());
        }
        return result;
    }
}
