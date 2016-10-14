package ru.askor.blagosfera.core.services.userlist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.askor.blagosfera.core.services.userlist.dto.UserForListDto;

import java.util.List;

/**
 *
 * Created by vgusev on 23.05.2016.
 */
@Getter
@AllArgsConstructor
public class UserListPageData {

    private int totalCount;

    private List<UserForListDto> users;
}
