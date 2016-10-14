package ru.askor.blagosfera.core.services.userlist;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Источник данных для списка пользователей
 * Created by vgusev on 23.05.2016.
 */
@Getter
@AllArgsConstructor
public class UserListDataSource {

    private Long id;

    private String type;

    private String name;
}
