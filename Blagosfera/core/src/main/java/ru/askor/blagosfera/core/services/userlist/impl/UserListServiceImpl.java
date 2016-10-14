package ru.askor.blagosfera.core.services.userlist.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.userlist.UserListDataSource;
import ru.askor.blagosfera.core.services.userlist.UserListDataSourceProvider;
import ru.askor.blagosfera.core.services.userlist.UserListService;
import ru.askor.blagosfera.domain.user.User;

import java.util.List;

/**
 *
 * Created by vgusev on 23.05.2016.
 */
@Service
@Transactional
public class UserListServiceImpl implements UserListService {

    private List<UserListDataSourceProvider> userListDataSourceProviders;

    @Autowired
    public void setUserListDataSourceProviders(List<UserListDataSourceProvider> userListDataSourceProviders) {
        this.userListDataSourceProviders = userListDataSourceProviders;
    }

    @Override
    public List<UserListDataSource> getByUser(User user) {
        return null;
    }
}
