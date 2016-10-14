package ru.askor.blagosfera.domain.contacts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.ContactStatus;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * модель данных контакта
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    private Long id;
    private User user;
    private User other;
    private Set<ContactGroup> contactGroups = new HashSet<>();
    private ContactStatus sharerStatus;
    private ContactStatus otherStatus;
    private Date requestDate;
    @Override
    public int hashCode() {
        return ((getId() == null) ? super.hashCode() : getId().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Contact)) {
            return false;
        }
        if (obj == null || Hibernate.getClass(getClass()) != Hibernate.getClass(obj.getClass())) {
            return false;
        }
        Contact other = (Contact) obj;
        return getId() != null && other.getId() != null && getId().equals(other.getId());
    }

}
