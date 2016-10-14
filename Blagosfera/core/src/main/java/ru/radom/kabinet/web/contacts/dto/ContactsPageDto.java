package ru.radom.kabinet.web.contacts.dto;

import lombok.Data;

import java.util.List;

/**
 * информация для страницы поиска контактов
 *
 */
@Data
public class ContactsPageDto {
    //группы пользователя
    private List<ContactGroupDto> groups;
    private String defaultGroupCountWord;
    //количество контактов в дефолтной группе
    private Long defaultGroupCount;
    //количество контактов пользователя
    private Long contactsCount;
}
