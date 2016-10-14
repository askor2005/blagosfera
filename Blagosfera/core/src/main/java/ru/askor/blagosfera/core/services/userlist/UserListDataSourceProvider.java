package ru.askor.blagosfera.core.services.userlist;

import ru.askor.blagosfera.domain.user.User;

import java.util.List;

/**
 *
 * Created by vgusev on 23.05.2016.
 */
public interface UserListDataSourceProvider {

    String getType();

    List<UserListDataSource> getUserListDataSources(User user);

    UserListDataSource getUserListDataSourceById(Long sourceId);

    UserListPageData getBySourceId(Long sourceId, int pageNumber, int perPageCount);
}
