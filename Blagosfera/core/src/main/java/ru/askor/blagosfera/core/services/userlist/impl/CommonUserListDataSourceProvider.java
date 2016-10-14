package ru.askor.blagosfera.core.services.userlist.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.userlist.UserListDataSource;
import ru.askor.blagosfera.core.services.userlist.UserListDataSourceProvider;
import ru.askor.blagosfera.core.services.userlist.UserListPageData;
import ru.askor.blagosfera.core.services.userlist.dto.UserForListDto;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.util.Collections;
import java.util.List;

/**
 *
 * Created by vgusev on 23.05.2016.
 */
@Service
@Transactional
public class CommonUserListDataSourceProvider implements UserListDataSourceProvider {

    private static final String TYPE = "CommonUserList";

    private static final UserListDataSource COMMON_USER_LIST_DATA_SOURCE = new UserListDataSource(-1l, TYPE, "Пользователи системы");

    @Autowired
    private UserDataService userDataService;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<UserListDataSource> getUserListDataSources(User user) {
        return Collections.singletonList(COMMON_USER_LIST_DATA_SOURCE);
    }

    @Override
    public UserListDataSource getUserListDataSourceById(Long sourceId) {
        return COMMON_USER_LIST_DATA_SOURCE;
    }

    @Override
    public UserListPageData getBySourceId(Long sourceId, int pageNumber, int perPageCount) {
        List<User> users = userDataService.getNotDeletedByPage(pageNumber, perPageCount);
        int totalCount = userDataService.getTotalCount();
        return new UserListPageData(totalCount, UserForListDto.toDtoList(users, null));
    }
}
