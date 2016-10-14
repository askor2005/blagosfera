package ru.radom.kabinet.web.voting;

import ru.radom.kabinet.model.ContactEntity;
import ru.radom.kabinet.model.UserEntity;

import java.util.Comparator;
import java.util.List;

/**
 * Сортировка пользователей. Сначала те, кто в списке контактов.
 * Created by vgusev on 11.08.2015.
 */
public class VotersComparator implements Comparator<UserEntity> {

    private UserEntity currentVoter;

    public VotersComparator(UserEntity currentVoter) {
        this.currentVoter = currentVoter;
    }

    @Override
    public int compare(UserEntity a, UserEntity b) {
        List<ContactEntity> contacts = currentVoter.getContacts();
        boolean aInContacts = false;
        boolean bInContacts = false;
        for (ContactEntity contact : contacts) {
            if (contact.getOther().getId().longValue() == a.getId().longValue()) {
                aInContacts = true;
            }
            if (contact.getOther().getId().longValue() == b.getId().longValue()) {
                bInContacts = true;
            }
        }
        return /*aInContacts && !*/bInContacts ? 1 : -1;
    }
}
