package ru.askor.blagosfera.core.services.userlist;

import ru.askor.blagosfera.domain.user.User;

import java.util.List;

/**
 * Сервис для работы со списками пользователей
 * Created by vgusev on 23.05.2016.
 */
public interface UserListService {

    List<UserListDataSource> getByUser(User user);

}
