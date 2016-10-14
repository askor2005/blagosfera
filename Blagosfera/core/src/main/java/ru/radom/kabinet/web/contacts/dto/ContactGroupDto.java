package ru.radom.kabinet.web.contacts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.askor.blagosfera.domain.contacts.ContactGroup;
import ru.radom.kabinet.expressions.Functions;

/**
 * Created by vtarasenko on 09.04.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactGroupDto {
    private long id;
    private String name;
    private Long userId;
    private int color;
    private long contactsCount;
    private String contactsCountWord;
    public static ContactGroupDto toDto(ContactGroup contactGroupDomain) {
        return new ContactGroupDto(contactGroupDomain.getId(),contactGroupDomain.getName(),contactGroupDomain.getUserId(),contactGroupDomain.getColor(),contactGroupDomain.getContactsCount(), Functions.getDeclension(contactGroupDomain.getContactsCount(), "контакт", "контакта", "контактов"));
    }
}
