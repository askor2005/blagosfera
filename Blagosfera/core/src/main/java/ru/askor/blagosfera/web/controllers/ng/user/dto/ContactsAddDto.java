package ru.askor.blagosfera.web.controllers.ng.user.dto;

import lombok.Data;

/**
 * Created by vtarasenko on 09.08.2016.
 */
@Data
public class ContactsAddDto {
    private Long otherId;
    private Long[] groupIds;
}
