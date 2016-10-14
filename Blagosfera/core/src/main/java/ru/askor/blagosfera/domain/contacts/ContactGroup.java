package ru.askor.blagosfera.domain.contacts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

/**
 * модель данных группы контакта
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactGroup {
    private Long id;
    private String name;
    private Long userId;
    private int color;
    private long contactsCount;
    @Override
    public int hashCode() {
        return ((getId() == null) ? super.hashCode() : getId().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ContactGroup)) {
            return false;
        }
        if (obj == null || Hibernate.getClass(getClass()) != Hibernate.getClass(obj.getClass())) {
            return false;
        }
        ContactGroup other = (ContactGroup) obj;
        return getId() != null && other.getId() != null && getId().equals(other.getId());
    }
}
